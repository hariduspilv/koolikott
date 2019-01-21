package ee.hm.dop.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import java.time.LocalDateTime;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

@Entity
public class Repository implements AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String baseURL;

    @Column

    private LocalDateTime lastSynchronization;

    @Column(nullable = false, name = "schemaName")
    private String schema;

    @Column
    private boolean contentIsEmbeddable;

    @Column(nullable = false)
    private String metadataPrefix;

    @OneToMany(fetch = EAGER, cascade = {MERGE, PERSIST})
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "repository")
    private List<RepositoryURL> repositoryURLs;

    @Column(nullable = false)
    private boolean used;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonSerialize(using = DateTimeSerializer.class)
    public LocalDateTime getLastSynchronization() {
        return lastSynchronization;
    }

    @JsonDeserialize(using = DateTimeDeserializer.class)
    public void setLastSynchronization(LocalDateTime lastSynchronization) {
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

    public boolean isContentIsEmbeddable() {
        return contentIsEmbeddable;
    }

    public void setContentIsEmbeddable(boolean contentIsEmbeddable) {
        this.contentIsEmbeddable = contentIsEmbeddable;
    }

    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    public void setMetadataPrefix(String metadataPrefix) {
        this.metadataPrefix = metadataPrefix;
    }

    public List<RepositoryURL> getRepositoryURLs() {
        return repositoryURLs;
    }

    public void setRepositoryURLs(List<RepositoryURL> repositoryURLs) {
        this.repositoryURLs = repositoryURLs;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).append(id) //
                .append(schema) //
                .append(baseURL) //
                .append(metadataPrefix) //
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
