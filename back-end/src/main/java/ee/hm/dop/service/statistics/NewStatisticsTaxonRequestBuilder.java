package ee.hm.dop.service.statistics;

import com.google.common.collect.Lists;
import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.dao.UserDao;
import ee.hm.dop.dao.specialized.StatisticsDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.newdto.DomainWithChildren;
import ee.hm.dop.service.reviewmanagement.newdto.TaxonAndUserRequest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class NewStatisticsTaxonRequestBuilder {

    @Inject
    private UserDao userDao;
    @Inject
    private TaxonDao taxonDao;
    @Inject
    private StatisticsDao statisticsDao;

    public List<TaxonAndUserRequest> composeRequest(StatisticsFilterDto filter) {
        List<TaxonAndUserRequest> taxonAndUserRequests = new ArrayList<>();
        List<User> users = userDao.getUsersByRole(Role.MODERATOR);
        for (Taxon taxon : filter.getTaxons()) {
            TaxonAndUserRequest request = new TaxonAndUserRequest();
            request.setTaxon(taxon);
            request.setUsers(Lists.newArrayList());
            for (User user : users) {
                //todo not sure will work so easily
                List<Taxon> userTaxons = taxonDao.getUserTaxons(user);
                if (userTaxons.contains(taxon)){
                    request.getUsers().add(user);
                }
            }
            taxonAndUserRequests.add(request);
        }
        /*List<DomainWithChildren> domainsWithChildren = new ArrayList<>();
        for (Taxon taxon : taxons) {
            if (taxon instanceof Domain) {
                domainsWithChildren.add(convertRealDomain((Domain) taxon));
            } else if (!(taxon instanceof Subject)) {
                //todo log something, user has wierd taxons
            }
        }
        for (Taxon taxon : taxons) {
            if (taxon instanceof Subject) {
                Domain domain = (Domain) taxon.getParent();
                boolean subjectIsNew = domainsWithChildren.stream().noneMatch(t -> t.getDomain().getId().equals(taxon.getId()));
                if (subjectIsNew) {
                    domainsWithChildren.add(convertSubjectDomain((Subject) taxon, domain));
                }
            }
        return domainsWithChildren;
        }*/


        return taxonAndUserRequests;
    }
}
