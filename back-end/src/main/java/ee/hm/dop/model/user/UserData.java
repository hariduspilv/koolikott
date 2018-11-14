package ee.hm.dop.model.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.model.ehis.Person;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import org.joda.time.DateTime;

import static org.joda.time.DateTime.now;

public class UserData {

    @JsonSerialize(using = DateTimeSerializer.class)
    private DateTime createdAt;
    private Person authCtx;

    public UserData(Person authCtx) {
        this.authCtx = authCtx;
        createdAt = now();
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public Person getAuthCtx() {
        return authCtx;
    }
}