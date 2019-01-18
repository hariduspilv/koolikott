package ee.hm.dop.service.statistics;

import com.google.common.collect.Lists;
import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.dao.UserDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.reviewmanagement.newdto.DomainWithChildren;
import ee.hm.dop.service.reviewmanagement.newdto.TaxonAndUserRequest;
import ee.hm.dop.service.reviewmanagement.newdto.UserWithTaxons;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewStatisticsTaxonRequestBuilder {

    @Inject
    private UserDao userDao;
    @Inject
    private TaxonDao taxonDao;
    @Inject
    private NewStatisticsCommonRequestBuilder commonRequestBuilder;

    public List<TaxonAndUserRequest> composeRequest(List<Taxon> filterTaxons) {
        List<UserWithTaxons> userWithTaxons = convertToUserWithTaxon(userDao.getUsersByRole(Role.MODERATOR));

        List<TaxonAndUserRequest> taxonAndUserRequests = new ArrayList<>();
        for (Taxon searchTaxon : filterTaxons) {
            TaxonAndUserRequest request = new TaxonAndUserRequest();
            request.setUsers(Lists.newArrayList());
            for (UserWithTaxons userWithTaxon : userWithTaxons) {
                if (userWithTaxon.hasTaxon(searchTaxon)) {
                    request.getUsers().add(userWithTaxon.getUser());
                }
            }
            if (searchTaxon instanceof Domain) {
                Domain domain = (Domain) searchTaxon;
                request.setEducationalContext(domain.getEducationalContext());
                request.setDomain(domain);
            } else if (searchTaxon instanceof  Subject){
                Subject subject = (Subject) searchTaxon;
                request.setEducationalContext(subject.getDomain().getEducationalContext());
                request.setDomain(subject.getDomain());
                request.setSubject(subject);
            }

            if (CollectionUtils.isEmpty(request.getUsers())) {
                request.setNoResults(true);
            } else {
                if (searchTaxon instanceof Domain) {
                    Domain domain = (Domain) searchTaxon;
                    DomainWithChildren domainWithChildren = commonRequestBuilder.convertRealDomainForTaxon(domain);
                    request.setDomainWithChildren(domainWithChildren);
                } else if (searchTaxon instanceof Subject) {
                    Subject subject = (Subject) searchTaxon;
                    DomainWithChildren domainWithChildren = commonRequestBuilder.convertSubjectDomain(subject, subject.getDomain());
                    request.setDomainWithChildren(domainWithChildren);
                }
            }
            taxonAndUserRequests.add(request);

            //todo merge domains
        }
        return taxonAndUserRequests;
    }

    private List<UserWithTaxons> convertToUserWithTaxon(List<User> users) {
        List<UserWithTaxons> userWithTaxons = new ArrayList<>();
        for (User user : users) {
            List<Taxon> userDomainsAndSubjects = new ArrayList<>();
            for (Taxon userTaxon : taxonDao.getUserTaxons(user)) {
                if (userTaxon instanceof Domain) {
                    userDomainsAndSubjects.add(userTaxon);
                    userDomainsAndSubjects.addAll(((Domain) userTaxon).getSubjects());
                } else if (userTaxon instanceof Subject) {
                    userDomainsAndSubjects.add(userTaxon);
                }
            }
            userWithTaxons.add(new UserWithTaxons(user, userDomainsAndSubjects));
        }
        return userWithTaxons;
    }
}
