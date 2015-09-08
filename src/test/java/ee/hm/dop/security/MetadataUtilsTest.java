package ee.hm.dop.security;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.security.x509.X509Credential;

public class MetadataUtilsTest {

    @BeforeClass
    public static void initOpenSAML() throws ConfigurationException {
        DefaultBootstrap.bootstrap();
    }

    @Test
    public void getCredential() throws Exception {
        X509Credential credential = MetadataUtils.getCredential("reos_metadata.xml");
        assertNotNull(credential);
    }
}
