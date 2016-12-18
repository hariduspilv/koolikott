package ee.hm.dop.model;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.rest.jackson.map.TargetGroupDeserializer;
import ee.hm.dop.rest.jackson.map.TargetGroupSerializer;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.EnumUtils;

import javax.persistence.*;

@Entity
@JsonDeserialize(using = TargetGroupDeserializer.class)
@JsonSerialize(using = TargetGroupSerializer.class)
public class TargetGroup {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String label;


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
        if (EnumUtils.isValidEnum(TargetGroupEnum.class, name)) {
            this.name = name;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

        if (!(obj instanceof TargetGroup)) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        TargetGroup other = (TargetGroup) obj;

        return new EqualsBuilder().append(name, other.name)
                .isEquals();
    }
}
