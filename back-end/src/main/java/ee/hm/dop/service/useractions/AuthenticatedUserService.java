package ee.hm.dop.service.useractions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.hm.dop.dao.AuthenticatedUserDao;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.user.UserData;
import ee.hm.dop.model.user.UserSession;
import ee.hm.dop.service.login.SessionUtil;
import ee.hm.dop.service.login.TokenGenerator;
import ee.hm.dop.utils.EncryptionUtils;
import ee.hm.dop.utils.exceptions.DuplicateTokenException;
import ee.hm.dop.utils.security.KeyStoreUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration2.Configuration;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.security.PrivateKey;

public class AuthenticatedUserService {

    @Inject
    private AuthenticatedUserDao authenticatedUserDao;
    @Inject
    private Configuration configuration;

    public AuthenticatedUser getAuthenticatedUserByToken(String token) {
        return authenticatedUserDao.findAuthenticatedUserByToken(token);
    }

    public String signUserData(AuthenticatedUser authenticatedUser) {
        ObjectMapper mapper = new ObjectMapper();
        String userDataStr = tryToMap(new UserData(authenticatedUser.getPerson()), mapper);

        PrivateKey privateKey = KeyStoreUtils.getDOPSigningCredential(configuration).getPrivateKey();
        final byte[] cipherText = EncryptionUtils.encrypt(userDataStr, privateKey);

        return Base64.encodeBase64String(cipherText);
    }

    private String tryToMap(UserData userData, ObjectMapper mapper) {
        try {
            return mapper.writeValueAsString(userData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
