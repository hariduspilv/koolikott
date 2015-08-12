package ee.hm.dop.model;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;

/**
 * Created by mart.laus on 22.07.2015.
 */
@Entity
public class Repository {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String baseURL;

    @Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastSynchronization;

    @Column(nullable = false, name = "schemaName")
    private String schema;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "repository")
    private List<Material> materials;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonSerialize(using = DateTimeSerializer.class)
    public DateTime getLastSynchronization() {
        return lastSynchronization;
    }

    @JsonDeserialize(using = DateTimeDeserializer.class)
    public void setLastSynchronization(DateTime lastSynchronization) {
        this.lastSynchronization = lastSynchronization;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).append(id) //
                .append(schema) //
                .append(baseURL) //
                .build();
    }
}
