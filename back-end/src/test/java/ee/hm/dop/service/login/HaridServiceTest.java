package ee.hm.dop.service.login;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.enums.LoginFrom;
import ee.hm.dop.model.harid.HarIdUser;
import ee.hm.dop.service.login.dto.UserStatus;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.configuration2.Configuration;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static ee.hm.dop.utils.ConfigurationProperties.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
@Ignore
@RunWith(EasyMockRunner.class)
public class HaridServiceTest {

    @TestSubject
    private HaridService haridService = new HaridService();
    @Mock
    private Configuration configuration;
    @Mock
    private Client client;
    @Mock
    private WebTarget target;
    @Mock
    private Invocation.Builder builder;
    @Mock
    private LoginService loginService;


    @Before
    public void setUp() throws Exception {

        String clientString = "clientsecret";
        haridService.postConstruct(clientString);
    }

    @Test
    public void authenticate() {
        String token = "123456";

        String generalDataUrl = "https://www.example.com/generaldata";
        String clientID = "test-client-id";
        String clientSecret = "clientsecret";

        HarIdUser harIdUser = new HarIdUser();
        harIdUser.setIdCode("38207146522");
        harIdUser.setFirstName("firstName");
        harIdUser.setLastName("lastName");

        Response response = createMock(Response.class);
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        UserStatus userStatus = UserStatus.loggedIn(authenticatedUser);

        expect(configuration.getBoolean(HARID_EXTRA_LOGGING)).andReturn(false);
        expect(configuration.getString(HARID_URL_GENERALDATA)).andReturn(generalDataUrl);
        expect(configuration.getString(HARID_CLIENT_ID)).andReturn(clientID);

        expect(client.target(generalDataUrl)).andReturn(target);
        expect(target.queryParam("token", token)).andReturn(target);
        expect(target.queryParam("client_id", clientID)).andReturn(target);
        expect(target.queryParam("signature", HmacUtils.hmacSha1Hex(clientSecret, token))).andReturn(target);
        expect(target.request()).andReturn(builder);
        expect(builder.accept(MediaType.APPLICATION_JSON_TYPE)).andReturn(builder);
        expect(builder.get()).andReturn(response);

        expect(response.readEntity(HarIdUser.class)).andReturn(harIdUser);
        expect(loginService.login(harIdUser.getIdCode(), harIdUser.getFirstName(), harIdUser.getLastName(), LoginFrom.HAR_ID)).andReturn(userStatus);

        replayAll(response);

//        UserStatus returnedAuthenticatedUser = haridService.authenticate(token);

        verifyAll(response);

//        assertNotNull(returnedAuthenticatedUser);
//        assertSame(userStatus, returnedAuthenticatedUser);
    }

    private void verifyAll(Object... mocks) {
        replay(configuration, loginService, client, target, builder);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void replayAll(Object... mocks) {
        replay(configuration, loginService, client, target, builder);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }
}