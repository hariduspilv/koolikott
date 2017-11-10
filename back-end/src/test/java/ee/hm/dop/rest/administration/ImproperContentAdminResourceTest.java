package ee.hm.dop.rest.administration;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.AdminLearningObject;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.*;

public class ImproperContentAdminResourceTest extends ResourceIntegrationTestBase {

    public static final String IMPROPER = "admin/improper";
    public static final String IMPROPER_COUNT = "admin/improper/count";

    @Test
    public void admin_can_get_improper_content() {
        login(USER_ADMIN);

        List<AdminLearningObject> improperContents = doGet(IMPROPER, genericType());
        assertTrue(CollectionUtils.isNotEmpty(improperContents));

        long uniqueLearningObjIdsCount = improperContents.stream()
                .map(AdminLearningObject::getId)
                .distinct()
                .count();

        long materialsCount = doGet(IMPROPER_COUNT, Long.class);
        assertEquals(uniqueLearningObjIdsCount, materialsCount);
    }

    @Test
    public void getImproper_returns_different_improper_based_on_user_privilege() {
        login(USER_MODERATOR);
        List<AdminLearningObject> improperMaterialsModerator = doGet(IMPROPER, genericType());
        assertTrue(CollectionUtils.isNotEmpty(improperMaterialsModerator));
        logout();

        login(USER_ADMIN);
        List<AdminLearningObject> improperMaterialsAdmin = doGet(IMPROPER, genericType());
        assertTrue(CollectionUtils.isNotEmpty(improperMaterialsAdmin));

        assertNotEquals("Admin improper materials list, Moderator improper materials list", improperMaterialsAdmin, improperMaterialsModerator);
    }

    @Test
    public void regular_user_is_not_allowed_to_get_improper_content() throws Exception {
        login(USER_SECOND);
        Response getBrokenResponse = doGet(IMPROPER);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getBrokenResponse.getStatus());
    }

    private GenericType<List<AdminLearningObject>> genericType() {
        return new GenericType<List<AdminLearningObject>>() {
        };
    }
}
