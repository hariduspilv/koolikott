package ee.hm.dop.model.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.model.ehis.Person;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

public class UserData {

    @JsonSerialize(using = DateTimeSerializer.class)
    private LocalDateTime createdAt;
    private Person authCtx;

    public UserData(Person authCtx) {
        this.authCtx = authCtx;
        createdAt = now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Person getAuthCtx() {
        return authCtx;
    }
}