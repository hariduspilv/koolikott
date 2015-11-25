package ee.hm.dop.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class WaramuTaxonMapping {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "taxon")
    private Taxon taxon;

    @Column(nullable = false, insertable = false)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Taxon getTaxon() {
        return taxon;
    }

    public void setTaxon(Taxon taxon) {
        this.taxon = taxon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
