package ee.hm.dop.service;

import ee.hm.dop.dao.AuthenticatedUserDAO;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.security.KeyStoreUtils;
import ee.hm.dop.utils.EncryptionUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static ee.hm.dop.utils.ConfigurationProperties.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(EasyMockRunner.class)
public class AuthenticatedUserServiceTest {

    @TestSubject
    private AuthenticatedUserService authenticatedUserService = new AuthenticatedUserService();

    @Mock
    private AuthenticatedUserDAO authenticatedUserDAO;

    @Mock
    private Configuration configuration;

    @Test
    public void getSignedUserData() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setToken("uniqueToken");
        authenticatedUser.setHomeOrganization("htg.tartu.ee");
        authenticatedUser.setAffiliations("member,student");

        expect(authenticatedUserDAO.findAuthenticatedUserByToken("uniqueToken")).andReturn(authenticatedUser);
        expect(configuration.getString(KEYSTORE_FILENAME)).andReturn("test.keystore").anyTimes();
        expect(configuration.getString(KEYSTORE_PASSWORD)).andReturn("newKeyStorePass").anyTimes();
        expect(configuration.getString(KEYSTORE_SIGNING_ENTITY_ID)).andReturn("testAlias").anyTimes();
        expect(configuration.getString(KEYSTORE_SIGNING_ENTITY_PASSWORD)).andReturn("newKeyPass").anyTimes();

        replayAll();
        String signedUserData = authenticatedUserService.getSignedUserData("uniqueToken");

        assertNotNull(signedUserData);

        byte[] bytes = Base64.decodeBase64(signedUserData);
        String userData = EncryptionUtils.decrypt(bytes, KeyStoreUtils.getDOPSigningCredential(configuration).getPublicKey());
        verifyAll();

        JSONObject userDataObject = new JSONObject(userData);
        assertEquals("TAAT", userDataObject.getString("authProvider"));
        assertNotNull(userDataObject.getLong("createdAt"));
        JSONObject authenticationContext = userDataObject.getJSONObject("authCtx");
        assertEquals("member,student", authenticationContext.getString("roles"));
        assertEquals("htg.tartu.ee", authenticationContext.getString("schacHomeOrganization"));
    }

    private void replayAll(Object... mocks) {
        replay(configuration, authenticatedUserDAO);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(configuration, authenticatedUserDAO);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }
}
