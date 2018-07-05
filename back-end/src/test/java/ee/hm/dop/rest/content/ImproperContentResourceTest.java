package ee.hm.dop.rest.content;

import com.google.common.collect.Lists;
import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.ReportingReason;
import ee.hm.dop.model.enums.ReportingReasonEnum;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static ee.hm.dop.model.enums.ReportingReasonEnum.LO_CONTENT;
import static ee.hm.dop.model.enums.ReportingReasonEnum.LO_FORM;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ImproperContentResourceTest extends ResourceIntegrationTestBase {

    public static final String IMPROPERS = "impropers";
    public static final String GET_IMPROPERS_BY_ID = "admin/improper/%s";

    @Test
    public void setImproperNoData() {
        login(USER_SECOND);
        Response response = doPut(IMPROPERS, new ImproperContent());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void can_not_set_material_that_does_not_exist_to_improper() {
        login(USER_SECOND);
        Response response = doPut(IMPROPERS, improperMaterialContent(NOT_EXISTS_ID));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void user_can_set_material_improper_with_reporting_reason() throws Exception {
        login(USER_SECOND);
        ImproperContentDto json = improperMaterialContent(MATERIAL_13);
        Response response = doPut(IMPROPERS, json);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        ImproperContent improperContent = doPut(IMPROPERS, json, ImproperContent.class);
        assertTrue("Improper material has reporting reasons", CollectionUtils.isNotEmpty(improperContent.getReportingReasons()));
        Material improperMaterial = getMaterial(MATERIAL_13);
        assertTrue("Material is improper", improperMaterial.getImproper() > 0);
    }

    @Test
    public void getImproperByLearningObject() {
        login(USER_ADMIN);

        List<ImproperContent> improperContents = doGet(format(GET_IMPROPERS_BY_ID, PORTFOLIO_3), genericType());
        assertTrue(CollectionUtils.isNotEmpty(improperContents));
        assertEquals(1, improperContents.size());
        assertEquals(Long.valueOf(5), improperContents.get(0).getId());
    }

    public static class ImproperContentDto{
        private LearningObject learningObject;
        private String reportingText;
        private List<ReportingReason> reportingReasons;

        public LearningObject getLearningObject() {
            return learningObject;
        }

        public void setLearningObject(LearningObject learningObject) {
            this.learningObject = learningObject;
        }

        public String getReportingText() {
            return reportingText;
        }

        public void setReportingText(String reportingText) {
            this.reportingText = reportingText;
        }

        public List<ReportingReason> getReportingReasons() {
            return reportingReasons;
        }

        public void setReportingReasons(List<ReportingReason> reportingReasons) {
            this.reportingReasons = reportingReasons;
        }
    }

    private GenericType<List<ImproperContent>> genericType() {
        return new GenericType<List<ImproperContent>>() {
        };
    }

    private ImproperContentDto improperMaterialContent(Long id) {
        ImproperContentDto improperContent = new ImproperContentDto();
        improperContent.setLearningObject(materialWithId(id));
        improperContent.setReportingReasons(Lists.newArrayList(reason(LO_CONTENT), reason(LO_FORM)));
        return improperContent;
    }

    private ReportingReason reason(ReportingReasonEnum content) {
        ReportingReason reason = new ReportingReason();
        reason.setReason(content);
        return reason;
    }
}
