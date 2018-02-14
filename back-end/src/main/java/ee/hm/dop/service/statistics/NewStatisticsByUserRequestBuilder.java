package ee.hm.dop.service.statistics;

import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.newdto.DomainWithChildren;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NewStatisticsByUserRequestBuilder {

    @Inject
    private TaxonDao taxonDao;
    @Inject
    private NewStatisticsCommonRequestBuilder commonRequestBuilder;


    public List<DomainWithChildren> userPath(StatisticsFilterDto filter) {
        //there is only 1 user currently
        User user = filter.getUsers().get(0);
        List<Taxon> taxons = taxonDao.getUserTaxons(user);
        List<DomainWithChildren> domainsWithChildren = new ArrayList<>();
        for (Taxon taxon : taxons) {
            if (taxon instanceof Domain) {
                domainsWithChildren.add(commonRequestBuilder.convertRealDomain((Domain) taxon));
            } else if (!(taxon instanceof Subject)) {
                //todo log something, user has wierd taxons
            }
        }
        for (Taxon taxon : taxons) {
            if (taxon instanceof Subject) {
                Domain domain = (Domain) taxon.getParent();
                boolean subjectIsNew = domainsWithChildren.stream().noneMatch(t -> t.getDomain().getId().equals(taxon.getId()));
                if (subjectIsNew) {
                    domainsWithChildren.add(commonRequestBuilder.convertSubjectDomain((Subject) taxon, domain));
                }
            }
        }
        return combineSubjectPrivilegesUnderDomain(domainsWithChildren);
    }

    /**
     * if user has 2 subject under 1 empty domain, but does not have privileges for the domain itself,
     * then we merge subjects under empty domain
     */
    private List<DomainWithChildren> combineSubjectPrivilegesUnderDomain(List<DomainWithChildren> domainsWithChildren) {
        List<DomainWithChildren> uniqueDomains = new ArrayList<>();
        for (DomainWithChildren domain : domainsWithChildren) {
            Optional<DomainWithChildren> existingDomain = uniqueDomains.stream().filter(d -> d.getDomain().getId().equals(domain.getDomain().getId())).findAny();
            if (!existingDomain.isPresent()) {
                uniqueDomains.add(domain);
            } else {
                existingDomain.get().getSubjects().addAll(domain.getSubjects());
            }
        }
        return uniqueDomains;
    }
}
