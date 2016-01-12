package ee.hm.dop.model;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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

    @Column
    private boolean isEstonianPublisher;

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

    public boolean isEstonianPublisher() {
        return isEstonianPublisher;
    }

    public void setIsEstonianPublisher(boolean isEstonianPublisher) {
        this.isEstonianPublisher = isEstonianPublisher;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).append(id) //
                .append(schema) //
                .append(baseURL) //
                .build();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(859, 937) //
                .append(baseURL) //
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Repository)) {
            return false;
        }

        Repository other = (Repository) obj;

        // Have to use get() because of lazyInitialization.
        return new EqualsBuilder().append(baseURL, other.getBaseURL()).isEquals();
    }
}
