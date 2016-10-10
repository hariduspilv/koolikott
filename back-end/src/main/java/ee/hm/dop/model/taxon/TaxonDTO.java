package ee.hm.dop.model.taxon;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mart on 10.10.16.
 */
public class TaxonDTO {

    private Long id;

    private String name;

    private Set<TaxonDTO> children;

    private String level;

    public TaxonDTO(Taxon taxon) {
        id = taxon.getId();
        name = taxon.getName();
        level = "." + taxon.getClass().getSimpleName();
        children = new HashSet<>();

        Set<? extends Taxon> children = taxon.getChildren();

        if (children != null)
            for (Taxon child : children) {
                TaxonDTO taxonDTO = new TaxonDTO(child);
                this.children.add(taxonDTO);
            }
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<TaxonDTO> getChildren() {
        return children;
    }

    public void setChildren(Set<TaxonDTO> children) {
        this.children = children;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
