package ee.hm.dop.utils.security;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.easymock.EasyMockRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.security.x509.X509Credential;

@RunWith(EasyMockRunner.class)
public class MetadataUtilsTest {

    @BeforeClass
    public static void initOpenSAML() throws ConfigurationException {
        DefaultBootstrap.bootstrap();
    }

    @Test
    public void getCredential() throws Exception {
        X509Credential credential = MetadataUtils.getCredential("reos_metadata.xml",
                "https://reos.taat.edu.ee/saml2/idp/metadata.php");
        assertNotNull(credential);
    }

    @Test
    public void getCredentialInputStreamNull() throws Exception {
        try {
            MetadataUtils.getCredential("notValid.xml", "https://reos.taat.edu.ee/saml2/idp/metadata.php");
            fail("Exception expected");
        } catch (Exception e) {
            // expected
        }
    }
}
