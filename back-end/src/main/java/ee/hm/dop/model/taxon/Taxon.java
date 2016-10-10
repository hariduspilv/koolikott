package ee.hm.dop.model.taxon;

import static javax.persistence.InheritanceType.JOINED;

import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.NoClass;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Cacheable
@DiscriminatorColumn(name = "level")
@Inheritance(strategy = JOINED)
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "level", defaultImpl = NoClass.class)
public abstract class Taxon {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, insertable = false)
    private String name;

    @Transient
    private Set<? extends Taxon> children;

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

    @JsonIgnore
    public abstract Taxon getParent();

    public boolean containsTaxon(Taxon taxon) {
        if (this.equals(taxon)) {
            return true;
        } else if (getParent() != null) {
            return getParent().containsTaxon(taxon);
        }

        return false;
    }

    public Set<? extends Taxon> getChildren() {
        if (this instanceof EducationalContext) {
            return ((EducationalContext) this).getDomains();
        } else if (this instanceof Domain) {
            if (!((Domain) this).getSpecializations().isEmpty()) {
                return ((Domain) this).getSpecializations();
            } else if (!((Domain) this).getSubjects().isEmpty()) {
                return ((Domain) this).getSubjects();
            } else if (!((Domain) this).getTopics().isEmpty()) {
                return ((Domain) this).getTopics();
            }
        } else if (this instanceof Specialization) {
            return ((Specialization) this).getModules();
        } else if (this instanceof Module) {
            return ((Module) this).getTopics();
        } else if (this instanceof Subject) {
            return ((Subject) this).getTopics();
        } else if (this instanceof Topic) {
            return ((Topic) this).getSubtopics();
        }

        return null;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 37) //
                .append(name) //
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

        return new EqualsBuilder().append(name, other.name) //
                .isEquals();
    }
}
