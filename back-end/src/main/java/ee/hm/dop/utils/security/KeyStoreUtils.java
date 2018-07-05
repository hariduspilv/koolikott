package ee.hm.dop.utils.security;

import ee.hm.dop.utils.DOPFileUtils;
import org.apache.commons.configuration.Configuration;
import org.opensaml.xml.security.Criteria;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.KeyStoreCredentialResolver;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_FILENAME;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_PASSWORD;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_SIGNING_ENTITY_ID;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_SIGNING_ENTITY_PASSWORD;
import static java.lang.String.format;

public class KeyStoreUtils {

    private static final Logger logger = LoggerFactory.getLogger(KeyStoreUtils.class);

    private static KeyStore DOPkeyStore;

    public static KeyStore loadKeystore(String filename, String password) {

        try (InputStream inputStream = DOPFileUtils.getFileAsStream(filename)) {
            if (inputStream == null) {
                throw new RuntimeException(format("Failed to load keystore in path: %s", filename));
            }

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(inputStream, password.toCharArray());
            return keyStore;
        } catch (Exception e) {
            throw new RuntimeException(format("Failed to load keystore in path: %s", filename), e);
        }
    }

    public static Credential getSigningCredential(KeyStore keystore, String entityId, String entityPassword) {
        try {
            Map<String, String> passwords = new HashMap<>();
            passwords.put(entityId, entityPassword);
            KeyStoreCredentialResolver resolver = new KeyStoreCredentialResolver(keystore, passwords);

            Criteria criteria = new EntityIDCriteria(entityId);
            CriteriaSet criteriaSet = new CriteriaSet(criteria);

            return resolver.resolveSingle(criteriaSet);
        } catch (Exception e) {
            logger.error("Error while getting signing credential.");
            return null;
        }

    }

    public static void setKeyStore(KeyStore keyStore) {
        KeyStoreUtils.DOPkeyStore = keyStore;
    }

    private static KeyStore getDOPKeyStore(Configuration configuration) {
        if (DOPkeyStore == null) {
            String filename = configuration.getString(KEYSTORE_FILENAME);
            String password = configuration.getString(KEYSTORE_PASSWORD);
            DOPkeyStore = KeyStoreUtils.loadKeystore(filename, password);
        }
        return DOPkeyStore;
    }

    public static Credential getDOPSigningCredential(Configuration configuration) {
        String entityId = configuration.getString(KEYSTORE_SIGNING_ENTITY_ID);
        String entityPassword = configuration.getString(KEYSTORE_SIGNING_ENTITY_PASSWORD);
        return KeyStoreUtils.getSigningCredential(getDOPKeyStore(configuration), entityId, entityPassword);
    }
}
