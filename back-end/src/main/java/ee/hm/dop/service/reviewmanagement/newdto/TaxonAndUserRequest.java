package ee.hm.dop.service.reviewmanagement.newdto;

import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Subject;

import java.util.List;

public class TaxonAndUserRequest {

    private EducationalContext educationalContext;
    private Domain domain;
    private Subject subject;
    private List<User> users;
    private boolean noResults;
    private DomainWithChildren domainWithChildren;

    public boolean isNoResults() {
        return noResults;
    }

    public void setNoResults(boolean noResults) {
        this.noResults = noResults;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public EducationalContext getEducationalContext() {
        return educationalContext;
    }

    public void setEducationalContext(EducationalContext educationalContext) {
        this.educationalContext = educationalContext;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public DomainWithChildren getDomainWithChildren() {
        return domainWithChildren;
    }

    public void setDomainWithChildren(DomainWithChildren domainWithChildren) {
        this.domainWithChildren = domainWithChildren;
    }
}
