package ee.hm.dop.model;

import static javax.persistence.FetchType.LAZY;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@MappedSuperclass
@JsonDeserialize(as=OriginalPicture.class)
public abstract class Picture {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String name;

    @Basic(fetch = LAZY, optional = false)
    @Lob
    @JsonIgnore
    private byte[] data;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
