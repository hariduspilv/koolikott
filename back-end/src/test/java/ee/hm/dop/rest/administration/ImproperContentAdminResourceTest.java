package ee.hm.dop.rest.administration;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.List;

import static org.junit.Assert.*;

public class ImproperContentAdminResourceTest extends ResourceIntegrationTestBase {

    public static final String IMPROPER_MATERIALS = "admin/improper/material";
    public static final String IMPROPER_MATERIALS_COUNT = "admin/improper/material/count";
    public static final String IMPROPER_PORTFOLIOS = "admin/improper/portfolio";
    public static final String IMPROPER_PORTFOLIOS_COUNT = "admin/improper/portfolio/count";

    @Test
    public void admin_can_get_improper_materials() {
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
    public void admin_can_get_improper_portfolios() {
        login(USER_ADMIN);

        List<ImproperContent> improperContents = doGet(IMPROPER_PORTFOLIOS, genericType());
        assertTrue(CollectionUtils.isNotEmpty(improperContents));
        assertEquals(3, improperContents.size());

        long portfoliosCount = doGet(IMPROPER_PORTFOLIOS_COUNT, Long.class);
        assertEquals(improperContents.size(), portfoliosCount);
    }

    @Test
    public void getImproperMaterials_returns_different_improper_materials_based_on_user_privilege() {
        login(USER_MODERATOR);
        List<ImproperContent> improperMaterialsModerator = doGet(IMPROPER_MATERIALS, genericType());
        assertTrue(CollectionUtils.isNotEmpty(improperMaterialsModerator));
        logout();

        login(USER_ADMIN);
        List<ImproperContent> improperMaterialsAdmin = doGet(IMPROPER_MATERIALS, genericType());
        assertTrue(CollectionUtils.isNotEmpty(improperMaterialsAdmin));

        assertNotEquals("Admin improper materials list, Moderator improper materials list", improperMaterialsAdmin, improperMaterialsModerator);
    }

    @Test
    public void getImproperPortfolios_returns_different_improper_portfolios_based_on_user_privilege() {
        login(USER_MODERATOR);
        List<ImproperContent> improperPortfoliosModerator = doGet(IMPROPER_PORTFOLIOS, genericType());
        assertTrue(CollectionUtils.isNotEmpty(improperPortfoliosModerator));
        logout();

        login(USER_ADMIN);
        List<ImproperContent> improperPortfoliosAdmin = doGet(IMPROPER_PORTFOLIOS, genericType());
        assertTrue(CollectionUtils.isNotEmpty(improperPortfoliosAdmin));

        assertNotEquals("Admin improper portfolios list, Moderator improper portfolios list", improperPortfoliosAdmin, improperPortfoliosModerator);
    }

    private GenericType<List<ImproperContent>> genericType() {
        return new GenericType<List<ImproperContent>>() {
        };
    }
}
