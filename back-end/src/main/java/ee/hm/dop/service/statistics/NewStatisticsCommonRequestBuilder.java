package ee.hm.dop.service.statistics;

import com.google.common.collect.Lists;
import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.service.reviewmanagement.newdto.DomainWithChildren;
import ee.hm.dop.service.reviewmanagement.newdto.SubjectWithChildren;
import org.apache.commons.collections.CollectionUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NewStatisticsCommonRequestBuilder {

    @Inject
    private TaxonDao taxonDao;

    public DomainWithChildren convertSubjectDomain(Subject subject, Domain domain) {
        DomainWithChildren domainDto = new DomainWithChildren();
        domainDto.setDomainIsUsed(false);
        domainDto.setEducationalContext((EducationalContext) domain.getParent());
        domainDto.setDomain(domain);
        domainDto.setSubjects(Lists.newArrayList(subjectDto(domain, subject)));
        return domainDto;
    }

    DomainWithChildren convertRealDomain(Domain domain) {
        DomainWithChildren domainDto = new DomainWithChildren();
        domainDto.setDomainIsUsed(true);
        domainDto.setEducationalContext((EducationalContext) domain.getParent());
        domainDto.setDomain(domain);
        if (CollectionUtils.isEmpty(domainDto.getSubjects())) {
            domainDto.setSubjects(new ArrayList<>());
            domainDto.setTaxonIds(taxonDao.getTaxonWithChildren(domain));
            return domainDto;
        }
        //if domain has subjects it is capped
        domainDto.setCapped(true);
        domainDto.setTaxonIds(Lists.newArrayList(domain.getId()));
        List<SubjectWithChildren> subjectDtos = domain.getSubjects().stream()
                .map(subject -> subjectDto(domain, subject))
                .collect(Collectors.toList());
        domainDto.setSubjects(subjectDtos);
        return domainDto;
    }

    private SubjectWithChildren subjectDto(Domain domain, Subject subject) {
        SubjectWithChildren subjectDto = new SubjectWithChildren();
        subjectDto.setEducationalContext((EducationalContext) domain.getParent());
        subjectDto.setDomain(domain);
        subjectDto.setSubject(subject);
        subjectDto.setTaxonIds(taxonDao.getTaxonWithChildren(subject));
        return subjectDto;
    }
}
