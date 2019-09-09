package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Faq;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FaqResourceTest extends ResourceIntegrationTestBase {

    public static final String SAVE_FAQ = "faq";
    public static final String DELETE_FAQ = "faq/delete";

    @Test
    public void all_users_can_get_faqs() {
        login(USER_ADMIN);
        Faq faq1 = make("Test?", "Test Eng?", "Test Rus?", "Jah", "Yes", "Da");
        Faq faq2 = make("Test2?", "Test2 Eng?", "Test2 Rus?", "Jah 2", "Yes 2", "Da 2");
        doPost(SAVE_FAQ, faq1);
        doPost(SAVE_FAQ, faq2);

        login(USER_PEETER);
        List<Faq> faqList = doGet(SAVE_FAQ, new GenericType<List<Faq>>() {});
        assertTrue(CollectionUtils.isNotEmpty(faqList));

    }

    @Test
    public void admin_can_save_and_update_faq() {
        login(USER_ADMIN);
        Faq faq = make("Test?", "Test Eng?", "Test Rus?", "Jah", "Yes", "Da");
        Faq savedFaq = doPost(SAVE_FAQ, faq, Faq.class);
        validate(savedFaq);
        assertEquals("Yes", savedFaq.getAnswerEng());
        assertEquals("Test?", savedFaq.getQuestionEst());
        savedFaq.setAnswerEng("Yes Again");
        Faq updatedFaq = doPost(SAVE_FAQ, savedFaq, Faq.class);
        assertEquals("Yes Again", updatedFaq.getAnswerEng());
        assertEquals("Test?", updatedFaq.getQuestionEst());
    }

    @Test
    public void admin_can_delete_faq() {
        login(USER_ADMIN);
        Faq faq = make("Test?", "Test Eng?", "Test Rus?", "Jah", "Yes", "Da");
        Faq savedFaq = doPost(SAVE_FAQ, faq, Faq.class);
        validate(savedFaq);
        Response response = doPost(DELETE_FAQ, savedFaq);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void user_or_moderator_can_not_create_or_update_faq_name() {
        login(USER_PEETER);
        Faq faq = make("Test?", "Test Eng?", "Test Rus?", "Jah", "Yes", "Da");
        Response response = doPost(SAVE_FAQ, faq);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void moderator_can_not_create_or_update_faq() {
        login(USER_MODERATOR);
        Faq faq = make("Test?", "Test Eng?", "Test Rus?", "Jah", "Yes", "Da");
        Response response2 = doPost(SAVE_FAQ, faq);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response2.getStatus());
    }

    @Ignore
    @Test
    public void when_any_question_or_answer_is_blank_or_null_bad_request_is_thrown() {
        login(USER_ADMIN);
        Response response = doPost(SAVE_FAQ, make("", "Test Eng?", "Test Rus?", "Jah", "Yes", "Da"));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        Response response2 = doPost(SAVE_FAQ, make("Test?", null, "Test Rus?", "Jah", "Yes", "Da"));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response2.getStatus());
        Response response3 = doPost(SAVE_FAQ, make("Test?", "Test Eng?", "", "Jah", "Yes", "Da"));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response3.getStatus());
        Response response4 = doPost(SAVE_FAQ, make("Test?", "Test Eng?", "Test Rus?", null, "Yes", "Da"));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response4.getStatus());
        Response response5 = doPost(SAVE_FAQ, make("Test?", "Test Eng?", "Test Rus?", "Jah", "", "Da"));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response5.getStatus());
        Response response6 = doPost(SAVE_FAQ, make("Test?", "Test Eng?", "Test Rus?", "Jah", "Yes", null));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response6.getStatus());
    }

   public Faq make(String questionEst, String questionEng, String questionRus, String answerEst, String answerEng, String answerRus) {
       Faq faq = new Faq();
       faq.setQuestionEst(questionEst);
       faq.setQuestionEng(questionEng);
       faq.setQuestionRus(questionRus);
       faq.setAnswerEst(answerEst);
       faq.setAnswerEng(answerEng);
       faq.setAnswerRus(answerRus);
       return faq;
   }

    public void validate(Faq savedFaq) {
        assertNotNull(savedFaq.getQuestionEst());
        assertNotNull(savedFaq.getQuestionEng());
        assertNotNull(savedFaq.getQuestionRus());
        assertNotNull(savedFaq.getAnswerEst());
        assertNotNull(savedFaq.getAnswerEng());
        assertNotNull(savedFaq.getAnswerRus());
    }
}
