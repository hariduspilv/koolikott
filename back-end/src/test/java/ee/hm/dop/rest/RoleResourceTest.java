package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.ws.rs.core.GenericType;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestConstants;
import org.junit.Test;

/**
 * Created by mart on 30.11.16.
 */
public class RoleResourceTest extends ResourceIntegrationTestBase {
    @Test
    public void getRoles() {
        login(TestConstants.USER_ADMIN);

        List<String> allUsers = doGet("role", new GenericType<List<String>>() {
        });
        assertEquals(4, allUsers.size());
    }
}
