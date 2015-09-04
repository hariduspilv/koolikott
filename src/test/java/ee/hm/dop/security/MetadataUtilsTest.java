package ee.hm.dop.security;

import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;
import org.opensaml.xml.security.x509.X509Credential;

public class MetadataUtilsTest {

    @Test
    @Ignore
    public void getCredential() throws Exception {
        X509Credential credential = MetadataUtils.getCerdential("reos_metadata.xml");
        assertNotNull(credential);
    }

}
