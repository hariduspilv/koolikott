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
import static org.junit.Assert.*;

public class ImproperContentResourceTest extends ResourceIntegrationTestBase {

    public static final String IMPROPERS = "impropers";
    public static final String IMPROPER_MATERIALS = "admin/improper/material";
    public static final String IMPROPER_MATERIALS_COUNT = "admin/improper/material/count";
    public static final String IMPROPER_PORTFOLIOS = "admin/improper/portfolio";
    public static final String IMPROPER_PORTFOLIOS_COUNT = "admin/improper/portfolio/count";
    public static final String GET_IMPROPERS_BY_ID = "impropers/%s";

    @Test
    public void setImproperNoData() {
        login(USER_SECOND);
        Response response = doPut(IMPROPERS, new ImproperContent());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void can_not_set_material_that_does_not_exist_to_improper() {
        login(USER_SECOND);
        Response response = doPut(IMPROPERS, improperMaterialContent(NOT_EXISTS_ID));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void user_can_set_material_improper_with_reporting_reason() throws Exception {
        login(USER_SECOND);
        Response response = doPut(IMPROPERS, improperMaterialContent(MATERIAL_13));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        ImproperContent improperContent = doPut(IMPROPERS, improperMaterialContent(MATERIAL_13), ImproperContent.class);
        assertTrue("Improper material has reporting reasons", CollectionUtils.isNotEmpty(improperContent.getReportingReasons()));
        Material improperMaterial = getMaterial(MATERIAL_13);
        assertTrue("Material is improper", improperMaterial.getImproper() > 0);
    }

    @Test
    public void getImpropers() {
        login(USER_ADMIN);
        List<ImproperContent> improperContents = doGet(IMPROPERS, genericType());
        assertTrue(CollectionUtils.isNotEmpty(improperContents));
    }

    @Test
    public void getImproperMaterials() {
        login(USER_ADMIN);

        List<ImproperContent> improperContents = doGet(IMPROPER_MATERIALS, genericType());
        assertTrue(CollectionUtils.isNotEmpty(improperContents));

        long uniqueLearningObjIdsCount = improperContents.stream()
                .map(ImproperContent::getLearningObject)
                .map(LearningObject::getId)
                .distinct()
                .count();

        long materialsCount = doGet(IMPROPER_MATERIALS_COUNT, Long.class);
        assertEquals(uniqueLearningObjIdsCount, materialsCount);
    }

    @Test
    public void getImproperPortfolios() {
        login(USER_ADMIN);

        List<ImproperContent> improperContents = doGet(IMPROPER_PORTFOLIOS, genericType());
        assertTrue(CollectionUtils.isNotEmpty(improperContents));
        assertEquals(3, improperContents.size());

        long portfoliosCount = doGet(IMPROPER_PORTFOLIOS_COUNT, Long.class);
        assertEquals(improperContents.size(), portfoliosCount);
    }

    @Test
    public void getImproperByLearningObject() {
        login(USER_SECOND);

        List<ImproperContent> improperContents = doGet(format(GET_IMPROPERS_BY_ID, PORTFOLIO_3), genericType());
        assertTrue(CollectionUtils.isNotEmpty(improperContents));
        assertEquals(1, improperContents.size());
        assertEquals(new Long(5), improperContents.get(0).getId());
    }

    @Test
    public void getImproperMaterials_returns_different_improper_materials_based_on_user_privilege() {
        login(USER_MODERATOR);
        List<ImproperContent> improperMaterialsModerator = doGet(IMPROPER_MATERIALS, genericType());
        logout();

        login(USER_ADMIN);
        List<ImproperContent> improperMaterialsAdmin = doGet(IMPROPER_MATERIALS, genericType());

        assertNotEquals("Admin improper materials list, Moderator improper materials list", improperMaterialsAdmin, improperMaterialsModerator);
    }

    @Test
    public void getImproperPortfolios_returns_different_improper_portfolios_based_on_user_privilege() {
        login(USER_MODERATOR);
        List<ImproperContent> improperPortfoliosModerator = doGet(IMPROPER_PORTFOLIOS, genericType());
        logout();

        login(USER_ADMIN);
        List<ImproperContent> improperPortfoliosAdmin = doGet(IMPROPER_PORTFOLIOS, genericType());

        assertNotEquals("Admin improper portfolios list, Moderator improper portfolios list", improperPortfoliosAdmin, improperPortfoliosModerator);
    }

    private GenericType<List<ImproperContent>> genericType() {
        return new GenericType<List<ImproperContent>>() {
        };
    }

    private ImproperContent improperMaterialContent(Long id) {
        ImproperContent improperContent = new ImproperContent();
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
