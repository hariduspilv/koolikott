package ee.hm.dop.rest.useractions;

import com.google.common.collect.Lists;
import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.AttachedFile;
import ee.hm.dop.model.CustomerSupport;
import org.junit.Test;

import javax.ws.rs.core.Response;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CustomerSupportResourceTest extends ResourceIntegrationTestBase {

    public static final String SEND_MAIL = "admin/customerSupport";

    @Test
    public void admin_can_save_customer_support_request_and_send_mail() {
        login(USER_ADMIN);
        CustomerSupport customerSupport = make("test@test.ee", "test 666", "Testsõnum", " xz");
        CustomerSupport cs = doPost(SEND_MAIL, customerSupport, CustomerSupport.class);
        assertNotNull(cs.getMessage());
        assertNotNull(cs.getSubject());
        assertNotNull(cs.getEmail());
        assertNotNull(cs.getName());
    }

    @Test
    public void validate_image_files() {
        login(USER_ADMIN);

        CustomerSupport supportCorrect = make("     test@test.ee    ", "test 666", "Testsõnum", " Test    McTestface");
        supportCorrect.setFiles(Lists.newArrayList(makeFile("correct.jpg", "correctContent")));
        Response response = doPost(SEND_MAIL, supportCorrect);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        CustomerSupport supportIncorrect = make("     test@test.ee    ", "test 666", "Testsõnum", " Test    McTestface");
        supportIncorrect.setFiles(Lists.newArrayList(makeFile("incorrect.exe", "incorrectContent")));
        Response response2 = doPost(SEND_MAIL, supportIncorrect);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response2.getStatus());
        assertEquals("contains invalid files", Response.Status.BAD_REQUEST.getStatusCode(), response2.getStatus());
    }

    @Test
    public void email_and_name_are_trimmed() {
        login(USER_ADMIN);
        CustomerSupport cs = doPost(SEND_MAIL, make("     test@test.ee    ", "test 666", "Testsõnum", " Test    McTestface"), CustomerSupport.class);
        assertEquals("test@test.ee", cs.getEmail());
        assertEquals("Test McTestface", cs.getName());
    }

    @Test
    public void when_mandatory_field_is_missing_bad_request_is_thrown() {
        login(USER_ADMIN);
        Response response = doPost(SEND_MAIL, make(null, "test 666", "Testsõnum", " xz"));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        Response response2 = doPost(SEND_MAIL, make("test@test.ee", null, "Testsõnum", " xz"));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response2.getStatus());
        Response response3 = doPost(SEND_MAIL, make("test@test.ee", "test 666", null, " xz"));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response3.getStatus());
        Response response4 = doPost(SEND_MAIL, make("test@test.ee", "test 666", "Testsõnum", null));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response4.getStatus());
    }

    public CustomerSupport make(String email, String subject, String message, String name) {
        CustomerSupport customerSupport = new CustomerSupport();
        customerSupport.setEmail(email);
        customerSupport.setSubject(subject);
        customerSupport.setMessage(message);
        customerSupport.setName(name);
        return customerSupport;
    }

    public CustomerSupport makeWithFiles(String email, String subject, String message, String name, ArrayList<AttachedFile> files) {
        CustomerSupport customerSupport = new CustomerSupport();
        customerSupport.setEmail(email);
        customerSupport.setSubject(subject);
        customerSupport.setMessage(message);
        customerSupport.setName(name);
        customerSupport.setFiles(files);
        return customerSupport;
    }

    private AttachedFile makeFile(String name, String content) {
        AttachedFile attachedFile = new AttachedFile();
        attachedFile.setName(name);
        attachedFile.setContent(content);
        return attachedFile;
    }
}
