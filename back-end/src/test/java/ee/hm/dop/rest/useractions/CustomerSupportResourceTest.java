package ee.hm.dop.rest.useractions;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.CustomerSupport;
import jdk.net.SocketFlow;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

public class CustomerSupportResourceTest extends ResourceIntegrationTestBase {

    public static final String SEND_MAIL = "admin/customerSupport";

    @Test
    public void admin_can_save_customer_support_request_and_send_mail() {
        login(USER_ADMIN);
        CustomerSupport customerSupport = new CustomerSupport();
        customerSupport.setEmail("juust@gmail.com");
        customerSupport.setSubject("test 666");
        customerSupport.setMessage("Testsõnum");
        customerSupport.setName("Tester");
        CustomerSupport cs = doPost(SEND_MAIL, customerSupport, CustomerSupport.class);
        assertNotNull(cs.getMessage());
        assertNotNull(cs.getSubject());
        assertNotNull(cs.getEmail());
        assertNotNull(cs.getName());
    }
}
