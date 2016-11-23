package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.rest.jackson.map.SizeSerializer;

import javax.persistence.*;

@Entity
@Table(name="Thumbnail")
public class Thumbnail extends Picture {

    @Enumerated(EnumType.STRING)
    private Size size;

    public Thumbnail() {}

    @JsonProperty
    @JsonSerialize(using = SizeSerializer.class)
    public Size getSize() {
        return size;
    }

    @JsonIgnore
    public void setSize(Size size) {
        this.size = size;
    }
}
