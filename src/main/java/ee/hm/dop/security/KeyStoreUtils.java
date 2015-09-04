package ee.hm.dop.security;

import static java.lang.String.format;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import org.opensaml.xml.security.Criteria;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.KeyStoreCredentialResolver;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.x509.X509Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.hm.dop.utils.FileUtils;

public class KeyStoreUtils {

    private static final Logger logger = LoggerFactory.getLogger(KeyStoreUtils.class);

    public static KeyStore loadKeystore(String filename, String password) {
        KeyStore keyStore = null;
        FileInputStream inputStream = null;

        File file = FileUtils.getFile(filename);
        if (file == null) {
            throw new RuntimeException(format("Failed to load keystore in path: %s", filename));
        }

        try {
            inputStream = new FileInputStream(file);
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(inputStream, password.toCharArray());
        } catch (Exception e) {
            throw new RuntimeException(format("Failed to load keystore in path: %s", filename), e);
        } finally {
            closeQuietly(inputStream);
        }

        return keyStore;
    }

    public static Credential getSigningCredential(KeyStore keystore, String entityId, String entityPassword) {
        X509Credential credential = null;

        try {
            Map<String, String> passwords = new HashMap<>();
            passwords.put(entityId, entityPassword);
            KeyStoreCredentialResolver resolver = new KeyStoreCredentialResolver(keystore, passwords);

            Criteria criteria = new EntityIDCriteria(entityId);
            CriteriaSet criteriaSet = new CriteriaSet(criteria);

            credential = (X509Credential) resolver.resolveSingle(criteriaSet);
        } catch (Exception e) {
            logger.error("Error while getting signing credential.");
        }

        return credential;
    }
}
