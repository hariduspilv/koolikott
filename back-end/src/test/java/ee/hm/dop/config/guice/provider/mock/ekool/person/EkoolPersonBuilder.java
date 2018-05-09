package ee.hm.dop.config.guice.provider.mock.ekool.person;

import ee.hm.dop.config.guice.provider.mock.ekool.EkoolResponse;
import ee.hm.dop.config.guice.provider.mock.rs.client.Builder;
import ee.hm.dop.model.ekool.Person;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EkoolPersonBuilder extends Builder {

    private static final String AUTH_HEADER_HASH1 = "Bearer shdsajhfuh5484618";
    private static final String AUTH_HEADER_HASH2 = "Bearer 54fdsgffs4566fds51dsds4g";
    private static Person person = null;

    @Override
    public Builder header(String name, Object value) {
        if (name.equals("Authorization")) {
            assertEquals("Authorization", name);
            assertTrue(AUTH_HEADER_HASH1.equals(value) || AUTH_HEADER_HASH2.equals(value));
            if (AUTH_HEADER_HASH1.equals(value)){
                person = person1();
            } else if (AUTH_HEADER_HASH2.equals(value)){
                person = person2();
            }
        } else if (name.equals("Content-Type")){
            assertEquals("Content-Type", name);
            assertEquals("application/x-www-form-urlencoded", value);
        }
        return this;
    }

    @Override
    public Response get() {
        return new EkoolResponse(person);
    }

    private Person person1() {
        return person("firstname1", "lastname1", "111111");
    }

    private Person person2() {
        return person("firstname2", "lastname2", "222222");
    }

    private Person person(String firstname1, String lastname1, String s) {
        Person person = new Person();
        person.setFirstName(firstname1);
        person.setLastName(lastname1);
        person.setIdCode(s);
        return person;
    }
}
