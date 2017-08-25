package ee.hm.dop.service;

import static org.joda.time.DateTime.now;

import java.security.PrivateKey;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.dao.AuthenticatedUserDao;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.ehis.Person;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import ee.hm.dop.utils.security.KeyStoreUtils;
import ee.hm.dop.utils.EncryptionUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;

public class AuthenticatedUserService {

    @Inject
    private AuthenticatedUserDao authenticatedUserDao;

    @Inject
    private Configuration configuration;

    public AuthenticatedUser getAuthenticatedUserByToken(String token) {
        return authenticatedUserDao.findAuthenticatedUserByToken(token);
    }

    public String signUserData(AuthenticatedUser authenticatedUser) {
        UserData userData = new UserData(authenticatedUser.getPerson());
        ObjectMapper mapper = new ObjectMapper();

        String userDataStr;
        try {
            userDataStr = mapper.writeValueAsString(userData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        PrivateKey privateKey = KeyStoreUtils.getDOPSigningCredential(configuration).getPrivateKey();
        final byte[] cipherText = EncryptionUtils.encrypt(userDataStr, privateKey);

        return Base64.encodeBase64String(cipherText);
    }

    @SuppressWarnings("unused")
    private final class UserData {

        @JsonSerialize(using = DateTimeSerializer.class)
        private DateTime createdAt;
        private Person authCtx;

        UserData(Person authCtx) {
            this.authCtx = authCtx;
            createdAt = now();
        }

        public DateTime getCreatedAt() {
            return createdAt;
        }

        public Person getAuthCtx() {
            return authCtx;
        }
    }
}
