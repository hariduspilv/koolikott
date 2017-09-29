package ee.hm.dop.utils.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.opensaml.xml.security.credential.Credential;

public class KeyStoreUtilsTest {

    @Test
    public void loadKeystore() throws KeyStoreException, CertificateEncodingException, InvalidNameException {
        String filename = "test.keystore";
        String password = "newKeyStorePass";
        String entityId = "testAlias";
        KeyStore keystore = KeyStoreUtils.loadKeystore(filename, password);

        assertNotNull(keystore);
        assertTrue(keystore.isKeyEntry(entityId));
        X509Certificate certificate = (X509Certificate) keystore.getCertificate(entityId);
        assertNotNull(certificate);

        Map<String, String> info = getCertificateInfo(certificate);
        assertEquals("EE", info.get("C"));
        assertEquals("TestState", info.get("ST"));
        assertEquals("TestCity", info.get("L"));
        assertEquals("TestCompany", info.get("O"));
        assertEquals("TestUnit", info.get("OU"));
        assertEquals("TestCommonName", info.get("CN"));
    }

    @Test
    public void getSigningCredential() {
        String filename = "test.keystore";
        String password = "newKeyStorePass";
        String entityId = "testAlias";
        String entityPassword = "newKeyPass";
        String testPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6t7qX8PhgvrUg2Tn9tLnZDet3L31V2xM8hraVp1B"
                + "GSuWRuCYL6M7C0+5JVJy2cFl4DFGUe3TYFRo8kuhdloWJfdmc8bFoT4/1Q5MAHpkIVAEDEZzJTb1MVjVsnR2lwm4dcqzBmll9"
                + "ZJcF7jU7CwhyZ48+YxD/9bL8JJsDlgaphfHKKmjbke2epRO9kQoB5ZU3zVcJma1bpgTZEQ5d6+8ZwukS+OPWyw2yshpVJUmOZ"
                + "Uu3iuFslJcbVBixI71f5cn/7L2HU98ukv6pGgqPVUUx/qOQnvHE1a16tTgF6uW5Mxe5tUHwXp8Sb7dOS06KwJWm8eboP7pqOW"
                + "W3WyWPjhCeQIDAQAB";
        String testPrivateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDq3upfw+GC+tSDZOf20udkN63cvfVXbEzy"
                + "GtpWnUEZK5ZG4JgvozsLT7klUnLZwWXgMUZR7dNgVGjyS6F2WhYl92ZzxsWhPj/VDkwAemQhUAQMRnMlNvUxWNWydHaXCbh1yr"
                + "MGaWX1klwXuNTsLCHJnjz5jEP/1svwkmwOWBqmF8coqaNuR7Z6lE72RCgHllTfNVwmZrVumBNkRDl3r7xnC6RL449bLDbKyGlU"
                + "lSY5lS7eK4WyUlxtUGLEjvV/lyf/svYdT3y6S/qkaCo9VRTH+o5Ce8cTVrXq1OAXq5bkzF7m1QfBenxJvt05LTorAlabx5ug/u"
                + "mo5ZbdbJY+OEJ5AgMBAAECggEBAIXwxfwC+xH09UhRPS+3KpouXooqEkXezyqqQaVvXHZ8MuEMp/1SuxNCTbWJBVJFRlHKB+4l"
                + "EIw41viGRPf3e/X+1oBj9MR1eXWJB7KmGYAd35EYhAXPB5kyrfttC0wrEPSrudiINssKwlYM4/AjYslV4jP117mIxxjUh6bOe7"
                + "QHd6GS6Xzz9wCaw7Xyk47Mb1+J4H0QQfDpfaeTQgvR79PQdbtKXhwERz7hbpYBAqF+hMDjrprO/EDb6kyYniNqOiDYwZMTuIPR"
                + "5D2hvmw6YJspLTjS38jD0tvu6MoKLhvWGI9psN+POFDEYsqAX4190zfc2uSn166MJq8bfHpL7zkCgYEA+mZjD6ckqNjDxWrrl3"
                + "scjsL/znl59eVGHe+QuF1K8//pIfIQNkGn6U9UvhIlmXijZHc+gvsBRTOWNMeOduF+SzGYAzQgLw2fq6YBL/ckgFFWReS3+1Gr"
                + "+q4fvv+FyuI1BIbitDS+0fBkAg1Y6SCZioQj+EoGtFdcVTuzO54DZUsCgYEA8B+ekx8JKWkWTlSu+84qB1jD+MjMQpcXhvMSUf"
                + "hLzC+Db28LkwgaLcKhC4BcEKJOClrQBraC5PMspMIdpE3mBv3lczFfHfL2M9hKbYNvI6N+s+R/VgbS8QzGabVJUX8hd6BU9bbY"
                + "IjDv3fy5FPTIRuVEInWfqr8sXcL4oltu0MsCgYBopHkphXQwi8XkrrYd6/kXQC6fHuz08gfHYuJb6cD1DzFiWkKkKfP98IU9mV"
                + "/VLzE5PwwEuNjjDpfrHqpOzBV3XxZj4FjC0TK1DP7aCLJcXaFsPBUXFh6E+FeT/jzveHEnnycGoDROj+N7aBCL/G+uD2Lo1CbR"
                + "HuEbTYOtmm9Y2QKBgBlDSlyyJDWeH8Gn+Sz22McjGKMhBRRwbwI8qo3DML5PtWVQ6ofSj2aHFohuPcFmQg2m1kIOi6Do0KaY4a"
                + "N6qpvLtIs1A4vUFwEHXXU+IcA/IEJu5NK5LxI7RNi0QP15AZ1jWezsCrs2KNZTE2nYAwYqcsupUl4VdOZ8b8otEnCXAoGBAJts"
                + "YYRNy0h6BiPeRvOHv7kbIg+bYILSOCaP6DcyO9K5U2KuA+KdKAS7IBB6mXaxRdewJRaRi7RNeqjqd3DDo9GNF0rg6ZOxFnj9n9"
                + "3iyBUvVJwwqhRmXy1evY7aXWDjbNSsfzlg/yZPctBeJCte4x0GaGN7klqk3LUIeSXEyWcX";
        KeyStore keystore = KeyStoreUtils.loadKeystore(filename, password);

        Credential credential = KeyStoreUtils.getSigningCredential(keystore, entityId, entityPassword);

        assertEquals(entityId, credential.getEntityId());
        assertEquals("X509Credential", credential.getCredentialType().getSimpleName());

        PublicKey publicKey = credential.getPublicKey();
        PrivateKey privateKey = credential.getPrivateKey();

        assertEquals(testPublicKey, Base64.encodeBase64String(publicKey.getEncoded()));
        assertEquals(testPrivateKey, Base64.encodeBase64String(privateKey.getEncoded()));
    }

    /**
     * Helper method to extract the Distinguished Name fields of a certificate
     * for testing purposes. For example: CN - Common Name, O - Organization...
     * 
     * @param certificate
     * @return Map of fields and values
     */
    private Map<String, String> getCertificateInfo(X509Certificate certificate) {
        X509Certificate x509certificate = certificate;
        X500Principal principal = x509certificate.getSubjectX500Principal();

        Map<String, String> info = new HashMap<>();
        try {
            LdapName ldapDN = new LdapName(principal.getName());
            for (Rdn rdn : ldapDN.getRdns()) {
                info.put(rdn.getType(), rdn.getValue().toString());
            }
        } catch (InvalidNameException e) {
            fail("Failed to read certificate info. ");
        }
        return info;
    }

}
