package ee.netgroup.htm.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class EkoolikottApi {

    private static final String DEV_REST_URL = "https://oxygen.netgroupdigital.com/rest/dev/login/";
    private static final String USER_REST_URL = "https://oxygen.netgroupdigital.com/rest/user";
    private static final String ADMIN_ID_CODE = "89898989898";
    private static final String ADMIN_USERNAME = "admin.admin";

    private static Logger logger = LoggerFactory.getLogger(EkoolikottApi.class);

    private static void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.baseURI = "https://oxygen.netgroupdigital.com/rest";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setAccept(JSON)
                .setContentType(JSON)
                .build();
    }

    public static DevLoginResponse setUserRole(String userIdCode, UserRole userRole) {
        setUp();
        User devLoginUserData = getDevLoginResponse(userIdCode).getAuthenticatedUserDto().getUserDto();
        if (!userRole.equals(devLoginUserData.getRole())) {
            logger.info("Different role than specified. Setting role " + userRole + " for user " + userIdCode);
            devLoginUserData.setRole(userRole);
            setNewUserData(devLoginUserData);
        } else {
            logger.info("User's role is as specified.");
        }
        DevLoginResponse updatedUserData = getDevLoginResponse(userIdCode);
        if(updatedUserData.getAuthenticatedUserDto().getUserDto().getRole().equals(userRole)){
            return updatedUserData;
        }else {
            try {
                throw new Exception();
            } catch (Exception ignored) {
                logger.error("User role update failed.");
            }
            return null;
        }
    }

    private static void setNewUserData(User newUserData) {
        given()
                .header("username", ADMIN_USERNAME)
                .header("authentication", getAdminAuthenticationToken())
                .body(newUserData)
                .when()
                .post(USER_REST_URL)
                .then()
                .statusCode(200);
    }

    private static String getAdminAuthenticationToken() {
        return getDevLoginResponse(ADMIN_ID_CODE).getAuthenticatedUserDto().getToken();
    }

    private static DevLoginResponse getDevLoginResponse(String personalCode) {
        Response response = given()
                .contentType(JSON)
                .get(DEV_REST_URL + personalCode);
        response.then().statusCode(200);
        return response
                .as(DevLoginResponse.class);
    }
}
