package ee.hm.dop.config;

import ee.hm.dop.utils.security.MetadataUtils;
import org.apache.commons.configuration2.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.signature.SignatureValidator;
import org.springframework.context.annotation.Bean;

import javax.inject.Inject;

import static ee.hm.dop.utils.ConfigurationProperties.TAAT_METADATA_ENTITY_ID;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_METADATA_FILEPATH;

@org.springframework.context.annotation.Configuration
public class SignatureValidatorConfig {

    @Inject
    private Configuration configuration;

    @Bean
    public SignatureValidator signatureValidator() {
        return new SignatureValidator(getCredential());
    }

    private X509Credential getCredential() {
        try {
            DefaultBootstrap.bootstrap();
            return MetadataUtils.getCredential(configuration.getString(TAAT_METADATA_FILEPATH),
                    configuration.getString(TAAT_METADATA_ENTITY_ID));
        } catch (Exception e) {
            throw new RuntimeException("Error getting credential.", e);
        }
    }
}
