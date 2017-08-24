package ee.hm.dop.guice.provider;

import static ee.hm.dop.utils.ConfigurationProperties.TAAT_METADATA_ENTITY_ID;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_METADATA_FILEPATH;

import javax.inject.Inject;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import ee.hm.dop.utils.security.MetadataUtils;
import org.apache.commons.configuration.Configuration;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.signature.SignatureValidator;

/**
 * Guice provider of SignatureValidator
 */
@Singleton
public class SignatureValidatorProvider implements Provider<SignatureValidator> {

    @Inject
    private Configuration configuration;

    @Override
    public synchronized SignatureValidator get() {
        return new SignatureValidator(getCredential());
    }

    private X509Credential getCredential() {
        try {
            return MetadataUtils.getCredential(configuration.getString(TAAT_METADATA_FILEPATH),
                    configuration.getString(TAAT_METADATA_ENTITY_ID));
        } catch (Exception e) {
            throw new RuntimeException("Error getting credential.", e);
        }
    }
}
