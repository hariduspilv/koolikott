package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.NoClass;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type", defaultImpl = NoClass.class)
public interface Searchable {

    Long getId();

    @JsonIgnore
    default String getType() {
        return getClass().getSimpleName().toLowerCase();
    }
}
