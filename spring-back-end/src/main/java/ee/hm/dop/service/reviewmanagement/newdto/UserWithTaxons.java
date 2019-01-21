package ee.hm.dop.service.reviewmanagement.newdto;

import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Taxon;

import java.util.List;

public class UserWithTaxons {

    private User user;
    private List<Taxon> taxons;

    public boolean hasTaxon(Taxon taxon) {
        return taxons.stream().anyMatch(t -> t.getId().equals(taxon.getId()));
    }

    public UserWithTaxons() {
    }

    public UserWithTaxons(User user, List<Taxon> taxons) {
        this.user = user;
        this.taxons = taxons;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Taxon> getTaxons() {
        return taxons;
    }

    public void setTaxons(List<Taxon> taxons) {
        this.taxons = taxons;
    }
}
