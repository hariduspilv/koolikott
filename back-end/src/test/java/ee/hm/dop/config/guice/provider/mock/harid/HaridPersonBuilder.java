package ee.hm.dop.config.guice.provider.mock.harid;

import ee.hm.dop.config.guice.provider.mock.rs.client.Builder;
import ee.hm.dop.model.harid.HarIdUser;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HaridPersonBuilder extends Builder {

    private static final String AUTH_HEADER_HASH1 = "Bearer shdsajhfuh5484618";
    private static final String AUTH_HEADER_HASH2 = "Bearer 54fdsgffs4566fds51dsds4g";
    private static final String AUTH_HEADER_HASH3 = "Bearer 3453455sdfjbljkabsfb";
    private static HarIdUser person = null;

    @Override
    public Builder header(String name, Object value) {
        if (name.equals("Authorization")) {
            assertEquals("Authorization", name);
            assertTrue(AUTH_HEADER_HASH1.equals(value) || AUTH_HEADER_HASH2.equals(value) || AUTH_HEADER_HASH3.equals(value));
            if (AUTH_HEADER_HASH1.equals(value)) {
                person = person1();
            } else if (AUTH_HEADER_HASH2.equals(value)) {
                person = person2();
            } else if (AUTH_HEADER_HASH3.equals(value)) {
                person = person3();
            }
        } else if (name.equals("Content-Type")) {
            assertEquals("Content-Type", name);
            assertEquals("application/x-www-form-urlencoded", value);
        }
        return this;
    }

    @Override
    public Response get() {
        return new HaridResponse(person);
    }

    private HarIdUser person1() {
        return person("firstname1", "lastname1", "111111");
    }

    private HarIdUser person2() {
        return person("firstname2", "lastname2", "222222");
    }

    private HarIdUser person3() {
        return person("firstname3", "lastname3", "");
    }

    private HarIdUser person(String firstname1, String lastname1, String s) {
        HarIdUser person = new HarIdUser();
        person.setFirstName(firstname1);
        person.setLastName(lastname1);
        person.setIdCode(s);
        return person;
    }
}
