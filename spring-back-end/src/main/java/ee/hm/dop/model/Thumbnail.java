package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.hm.dop.model.enums.Size;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "Thumbnail")
public class Thumbnail extends Picture {

    @Enumerated(EnumType.STRING)
    private Size size;

    @JsonProperty
    public Size getSize() {
        return size;
    }

    @JsonIgnore
    public void setSize(Size size) {
        this.size = size;
    }
}
