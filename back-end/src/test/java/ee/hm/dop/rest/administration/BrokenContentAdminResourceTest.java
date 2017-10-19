package ee.hm.dop.rest.administration;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
<<<<<<< HEAD
import ee.hm.dop.common.test.TestConstants;
=======
>>>>>>> new-develop
import ee.hm.dop.model.BrokenContent;
import ee.hm.dop.model.Material;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
<<<<<<< HEAD

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
=======
import java.util.List;

import static org.junit.Assert.*;
>>>>>>> new-develop

public class BrokenContentAdminResourceTest extends ResourceIntegrationTestBase {

    private static final String MATERIAL_SET_BROKEN = "material/setBroken";
    private static final String MATERIAL_GET_BROKEN = "admin/brokenContent/getBroken";
    private static final String MATERIAL_GET_BROKEN_COUNT = "admin/brokenContent/getBroken/count";
    private static final String MATERIAL_SET_NOT_BROKEN = "admin/brokenContent/setNotBroken";
<<<<<<< HEAD
    private static final String MATERIAL_IS_BROKEN = "admin/brokenContent/isBroken";

    @Test
    public void setNotBroken_sets_material_unbroken() {
        login(TestConstants.USER_ADMIN);
        setMaterialBroken(TestConstants.MATERIAL_5);
        assertTrue("Material is broken", getMaterial(TestConstants.MATERIAL_5).getBroken() > 0);

        doPost(MATERIAL_SET_NOT_BROKEN, getMaterial(TestConstants.MATERIAL_5));
        assertTrue("Material is not broken", getMaterial(TestConstants.MATERIAL_5).getBroken() == 0);
    }

    @Test
    public void setNotBroken_sets_material_unbroken_and_material_is_reviewed() {
        login(TestConstants.USER_ADMIN);
        setMaterialBroken(TestConstants.MATERIAL_6);
        assertTrue("Material is broken", getMaterial(TestConstants.MATERIAL_6).getBroken() > 0);

        Material materialBroken = getMaterial(TestConstants.MATERIAL_6);
        assertTrue(materialBroken.getUnReviewed() > 0);

        doPost(MATERIAL_SET_NOT_BROKEN, getMaterial(TestConstants.MATERIAL_6));
        assertTrue("Material is not broken", getMaterial(TestConstants.MATERIAL_6).getBroken() == 0);

        Material materialNotBroken = getMaterial(TestConstants.MATERIAL_6);
        assertTrue(materialNotBroken.getUnReviewed() == 0);
    }

    @Test
    public void isBroken_returns_if_material_is_broken() {
        login(TestConstants.USER_ADMIN);
        setMaterialBroken(TestConstants.MATERIAL_5);

        Response isBrokenResponseAdmin = doGet(MATERIAL_IS_BROKEN + "?materialId=" + TestConstants.MATERIAL_5);
=======
    private static final String MATERIAL_IS_BROKEN = "admin/brokenContent/isBroken?materialId=";

    @Test
    public void isBroken_returns_if_material_is_broken() {
        login(USER_ADMIN);
        setMaterialBroken(MATERIAL_5);

        Response isBrokenResponseAdmin = doGet("admin/brokenContent/isBroken" + "?materialId=" + MATERIAL_5);
>>>>>>> new-develop
        assertEquals(Response.Status.OK.getStatusCode(), isBrokenResponseAdmin.getStatus());
        assertEquals(isBrokenResponseAdmin.readEntity(Boolean.class), true);
    }

    @Test
    public void getBroken_returns_broken_materials_to_admin() {
<<<<<<< HEAD
        login(TestConstants.USER_ADMIN);

        Material material = getMaterial(TestConstants.MATERIAL_5);
=======
        login(USER_ADMIN);

        Material material = getMaterial(MATERIAL_5);
>>>>>>> new-develop
        Response response = doPost(MATERIAL_SET_BROKEN, material);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Response getBrokenResponseAdmin = doGet(MATERIAL_GET_BROKEN, MediaType.APPLICATION_JSON_TYPE);
        List<BrokenContent> brokenMaterials = getBrokenResponseAdmin.readEntity(list());
<<<<<<< HEAD
        assertTrue(brokenMaterials.stream().map(BrokenContent::getMaterial).anyMatch(m -> m.getId().equals(TestConstants.MATERIAL_5)));
=======
        assertTrue(brokenMaterials.stream().map(BrokenContent::getMaterial).anyMatch(m -> m.getId().equals(MATERIAL_5)));
>>>>>>> new-develop

        long brokenMaterialsCount = doGet(MATERIAL_GET_BROKEN_COUNT, Long.class);
        assertEquals("Broken materials count", brokenMaterials.size(), brokenMaterialsCount);
    }

    @Test
<<<<<<< HEAD
    public void regular_user_is_not_allowed_to_check_if_material_is_broken() throws Exception {
        login(TestConstants.USER_SECOND);
        Response isBrokenResponse = doGet(MATERIAL_IS_BROKEN + "?materialId=" + TestConstants.MATERIAL_5);
=======
    public void getBroken_returns_different_broken_materials_based_on_user_privilege() {
        login(USER_MODERATOR);
        List<BrokenContent> brokenMaterialsModerator = doGet(MATERIAL_GET_BROKEN, list());
        logout();

        login(USER_ADMIN);
        List<BrokenContent> brokenMaterialsAdmin = doGet(MATERIAL_GET_BROKEN, list());

        assertNotEquals("Admin broken materials list, Moderator broken materials list", brokenMaterialsAdmin, brokenMaterialsModerator);
    }

    @Test
    public void regular_user_is_not_allowed_to_check_if_material_is_broken() throws Exception {
        login(USER_SECOND);
        Response isBrokenResponse = doGet(MATERIAL_IS_BROKEN + MATERIAL_5);
>>>>>>> new-develop
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), isBrokenResponse.getStatus());
    }

    @Test
    public void regular_user_is_not_allowed_to_set_material_not_broken() throws Exception {
<<<<<<< HEAD
        login(TestConstants.USER_SECOND);
        Response response = doPost(MATERIAL_SET_NOT_BROKEN, getMaterial(TestConstants.MATERIAL_5));
=======
        login(USER_SECOND);
        Response response = doPost(MATERIAL_SET_NOT_BROKEN, getMaterial(MATERIAL_5));
>>>>>>> new-develop
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void regular_user_is_not_allowed_to_get_broken_materials() throws Exception {
<<<<<<< HEAD
        login(TestConstants.USER_SECOND);
=======
        login(USER_SECOND);
>>>>>>> new-develop
        Response getBrokenResponse = doGet(MATERIAL_GET_BROKEN);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getBrokenResponse.getStatus());
    }

    private void setMaterialBroken(Long materialId) {
        doPost(MATERIAL_SET_BROKEN, getMaterial(materialId));
    }

    private GenericType<List<BrokenContent>> list() {
        return new GenericType<List<BrokenContent>>() {
        };
    }
}
