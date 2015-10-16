package ee.hm.dop.service;

import ee.hm.dop.dao.AuthenticatedUserDAO;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.security.KeyStoreUtils;
import ee.hm.dop.utils.EncryptionUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.Configuration;
import org.json.JSONObject;

import javax.inject.Inject;
import java.security.PrivateKey;

public class AuthenticatedUserService {

    public static final String TAAT = "TAAT";

    @Inject
    private AuthenticatedUserDAO authenticatedUserDAO;

    @Inject
    private Configuration configuration;

    public AuthenticatedUser getAuthenticatedUserByToken(String token) {
        return authenticatedUserDAO.findAuthenticatedUserByToken(token);
    }

    public String getSignedUserData(String token) {
        AuthenticatedUser authenticatedUser = authenticatedUserDAO.findAuthenticatedUserByToken(token);
        if(authenticatedUser == null) {
            return null;
        }
        long currentUnixTime = System.currentTimeMillis() / 1000L;

        JSONObject authenticationContext = new JSONObject();
        authenticationContext.put("schacHomeOrganization", authenticatedUser.getHomeOrganization());
        authenticationContext.put("roles", authenticatedUser.getAffiliations());

        JSONObject userDataObject = new JSONObject();
        userDataObject.put("createdAt", currentUnixTime);
        userDataObject.put("authProvider", TAAT);
        userDataObject.put("authCtx", authenticationContext);

        PrivateKey privateKey = KeyStoreUtils.getDOPSigningCredential(configuration).getPrivateKey();
        final byte[] cipherText = EncryptionUtils.encrypt(userDataObject.toString(), privateKey);

        return Base64.encodeBase64String(cipherText);
    }
}
