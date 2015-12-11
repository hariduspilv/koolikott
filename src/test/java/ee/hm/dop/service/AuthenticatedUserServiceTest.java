package ee.hm.dop.service;

import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_FILENAME;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_PASSWORD;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_SIGNING_ENTITY_ID;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_SIGNING_ENTITY_PASSWORD;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.security.KeyStoreUtils;
import ee.hm.dop.utils.EncryptionUtils;

@RunWith(EasyMockRunner.class)
public class AuthenticatedUserServiceTest {

    @TestSubject
    private AuthenticatedUserService authenticatedUserService = new AuthenticatedUserService();

    @Mock
    private Configuration configuration;

    @Test
    public void getSignedUserData() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setToken("uniqueToken");
        authenticatedUser.setHomeOrganization("htg.tartu.ee");
        authenticatedUser.setAffiliations("member,student");

        expect(configuration.getString(KEYSTORE_FILENAME)).andReturn("test.keystore").anyTimes();
        expect(configuration.getString(KEYSTORE_PASSWORD)).andReturn("newKeyStorePass").anyTimes();
        expect(configuration.getString(KEYSTORE_SIGNING_ENTITY_ID)).andReturn("testAlias").anyTimes();
        expect(configuration.getString(KEYSTORE_SIGNING_ENTITY_PASSWORD)).andReturn("newKeyPass").anyTimes();

        replayAll();
        String signedUserData = authenticatedUserService.signUserData(authenticatedUser);

        assertNotNull(signedUserData);

        byte[] bytes = Base64.decodeBase64(signedUserData);
        String userData = EncryptionUtils.decrypt(bytes, KeyStoreUtils.getDOPSigningCredential(configuration)
                .getPublicKey());
        verifyAll();

        JSONObject userDataObject = new JSONObject(userData);
        assertEquals("TAAT", userDataObject.getString("authProvider"));

        DateTime dateTime = new DateTime(userDataObject.getString("createdAt"));
        assertTrue(dateTime.isBefore(DateTime.now()) && dateTime.isAfter(DateTime.now().getMillis() - 5000));

        JSONObject authenticationContext = userDataObject.getJSONObject("authCtx");
        assertEquals("member,student", authenticationContext.getString("roles"));
        assertEquals("htg.tartu.ee", authenticationContext.getString("schacHomeOrganization"));
    }

    private void replayAll(Object... mocks) {
        replay(configuration);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(configuration);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }
}
