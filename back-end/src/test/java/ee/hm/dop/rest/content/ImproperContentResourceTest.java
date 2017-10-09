package ee.hm.dop.rest.content;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.*;

public class ImproperContentResourceTest extends ResourceIntegrationTestBase {

    public static final String IMPROPERS = "impropers";
    public static final String IMPROPER_MATERIALS = "admin/improper/material";
    public static final String IMPROPER_MATERIALS_COUNT = "admin/improper/material/count";
    public static final String IMPROPER_PORTFOLIOS = "admin/improper/portfolio";
    public static final String IMPROPER_PORTFOLIOS_COUNT = "admin/improper/portfolio/count";
    public static final String GET_IMPROPERS_BY_ID = "impropers/%s";
    public static final String REVIEW_IMPROPERS_BY_ID = "impropers?learningObject=%s";

    @Test
    public void setImproperNoData() {
        login(USER_SECOND);

        Response response = doPut(IMPROPERS, new ImproperContent());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void setImproperNotExistemLearningObject() {
        login(USER_SECOND);

        Response response = doPut(IMPROPERS, improperMaterialContent(34534534L));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Ignore
    @Test
    public void setImproper() {
        login(USER_ADMIN);

        ImproperContent newImproperContent = doPut(IMPROPERS, improperMaterialContent(MATERIAL_1), ImproperContent.class);

        assertNotNull(newImproperContent);
        assertNotNull(newImproperContent.getId());
        assertEquals(MATERIAL_1, newImproperContent.getLearningObject().getId());

        Response response = doDelete(format(REVIEW_IMPROPERS_BY_ID, newImproperContent.getId()));
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void getImpropers() {
        login(USER_ADMIN);

        List<ImproperContent> improperContents = doGet(IMPROPERS, genericType());

        assertNotNull(improperContents.size());
        assertTrue(CollectionUtils.isNotEmpty(improperContents));
    }

    @Test
    public void getImproperMaterials() {
        login(USER_ADMIN);

        List<ImproperContent> improperContents = doGet(IMPROPER_MATERIALS, genericType());

        assertNotNull(improperContents.size());
        long uniqueLearningObjIdsCount = improperContents.stream()
                .map(ImproperContent::getLearningObject)
                .map(LearningObject::getId)
                .distinct()
                .count();
        assertTrue(CollectionUtils.isNotEmpty(improperContents));

        long materialsCount = doGet(IMPROPER_MATERIALS_COUNT, Long.class);
        assertEquals(uniqueLearningObjIdsCount, materialsCount);
    }

    @Test
    public void getImproperPortfolios() {
        login(USER_ADMIN);

        List<ImproperContent> improperContents = doGet(IMPROPER_PORTFOLIOS, genericType());

        assertNotNull(improperContents.size());
        assertEquals(2, improperContents.size());

        long portfoliosCount = doGet(IMPROPER_PORTFOLIOS_COUNT, Long.class);
        assertEquals(improperContents.size(), portfoliosCount);
    }

    @Test
    public void getImproperByLearningObject() {
        login(USER_SECOND);

        List<ImproperContent> improperContents = doGet(format(GET_IMPROPERS_BY_ID, PORTFOLIO_3), genericType());

        assertNotNull(improperContents.size());
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

    private ImproperContent improperMaterialContent(long id) {
        ImproperContent improperContent = new ImproperContent();
        improperContent.setLearningObject(materialWithId(id));
        return improperContent;
    }
}
