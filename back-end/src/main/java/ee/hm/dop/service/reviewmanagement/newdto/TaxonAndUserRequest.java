package ee.hm.dop.service.reviewmanagement.newdto;

import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Taxon;

import java.util.List;

public class TaxonAndUserRequest {

    private Taxon taxon;
    private List<User> users;
    private DomainWithChildren domain;
    private SubjectWithChildren subject;

    public Taxon getTaxon() {
        return taxon;
    }

    public void setTaxon(Taxon taxon) {
        this.taxon = taxon;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public DomainWithChildren getDomain() {
        return domain;
    }

    public void setDomain(DomainWithChildren domain) {
        this.domain = domain;
    }

    public SubjectWithChildren getSubject() {
        return subject;
    }

    public void setSubject(SubjectWithChildren subject) {
        this.subject = subject;
    }
}
