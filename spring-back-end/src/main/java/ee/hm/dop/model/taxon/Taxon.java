package ee.hm.dop.model.taxon;

import static javax.persistence.InheritanceType.JOINED;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.NoClass;
import ee.hm.dop.model.AbstractEntity;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Cacheable
@DiscriminatorColumn(name = "level")
@Inheritance(strategy = JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "level", defaultImpl = NoClass.class)
public abstract class Taxon implements AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false, insertable = false)
    protected String name;

    @JsonIgnore
    @Column(nullable = false, insertable = false)
    private String nameLowercase;

    @JsonIgnore
    @Column(nullable = false, insertable = false)
    private boolean used;

    @Column(nullable = false, name = "level")
    private String taxonLevel;

    @Transient
    private Long parentId;

    @Transient
    private String parentLevel;

    @Transient
    private String level;

    @Column(nullable = false)
    protected String translationKey;

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

    public Long getParentId() {
        if (getParent() != null) {
            return getParent().getId();
        }
        return null;
    }

    public String getParentLevel() {
        if (getParent() != null) {
            return "." + getParent().getClass().getSimpleName();
        }
        return null;
    }

    public String getLevel() {
        return "." + this.getClass().getSimpleName();
    }

    public String getNameLowercase() {
        return nameLowercase;
    }

    public void setNameLowercase(String nameLowercase) {
        this.nameLowercase = nameLowercase;
    }

    @JsonIgnore
    public abstract Taxon getParent();

    @JsonIgnore
    public abstract String getSolrLevel();

    @JsonIgnore
    public abstract Set<? extends Taxon> getChildren();

    @JsonIgnore
    public List<Taxon> getChildrenList() {
        return new ArrayList<>(getChildren());
    }

    public boolean containsTaxon(Taxon taxon) {
        if (this.equals(taxon)) {
            return true;
        } else if (getParent() != null) {
            return getParent().containsTaxon(taxon);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 37) //
                .append(name) //
                .append(taxonLevel) //
                .append(getClass()) //
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Taxon)) {
            return false;
        }

        // Validate @UniqueConstraint(columnNames = { "name", "level" })
        if (getClass() != obj.getClass()) {
            return false;
        }

        Taxon other = (Taxon) obj;

        return new EqualsBuilder().append(name, other.name)
                .append(taxonLevel, other.taxonLevel)
                .isEquals();
    }

    public String getTaxonLevel() {
        return taxonLevel;
    }

    public void setTaxonLevel(String taxonLevel) {
        this.taxonLevel = taxonLevel;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
