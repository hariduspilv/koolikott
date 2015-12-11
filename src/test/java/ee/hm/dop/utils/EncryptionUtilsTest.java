package ee.hm.dop.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

import org.junit.Before;
import org.junit.Test;
import org.opensaml.xml.security.credential.Credential;

import ee.hm.dop.security.KeyStoreUtils;

public class EncryptionUtilsTest {

    private Credential credential;

    @Before
    public void getCredential() throws NoSuchAlgorithmException {
        String filename = "test.keystore";
        String password = "newKeyStorePass";
        String entityId = "testAlias";
        String entityPassword = "newKeyPass";

        KeyStore keystore = KeyStoreUtils.loadKeystore(filename, password);
        credential = KeyStoreUtils.getSigningCredential(keystore, entityId, entityPassword);
    }

    @Test
    public void encryptAndDecrypt() throws NoSuchAlgorithmException {
        String dataToEncrypt = "{\"createdAt\":”2012-04-23T18:25:43.511Z”,\"authProvider\":\"TAAT\",\"authCtx\":{\"roles\":\"member,student\",\"schacHomeOrganization\":\"htg.tartu.ee\"}}";

        byte[] bytes = EncryptionUtils.encrypt(dataToEncrypt, credential.getPrivateKey());
        assertNotNull(bytes);

        String originalData = EncryptionUtils.decrypt(bytes, credential.getPublicKey());
        assertNotNull(originalData);

        assertEquals(dataToEncrypt, originalData);
    }

    @Test
    public void encrypt() throws NoSuchAlgorithmException {
        String dataToEncrypt = "{\"createdAt\":”2012-04-23T18:25:43.511Z”,\"authProvider\":\"TAAT\",\"authCtx\":{\"roles\":\"member,student\",\"schacHomeOrganization\":\"htg.tartu.ee\"}}";

        byte[] bytes = EncryptionUtils.encrypt(dataToEncrypt, credential.getPrivateKey());
        assertNotNull(bytes);
    }

    @Test
    public void decrypt() throws NoSuchAlgorithmException {
        byte[] bytes = { 43, -31, 98, 5, 31, -21, 4, -54, -86, 42, -38, -32, 119, -2, -28, -3, 109, -65, 1, 112, -120,
                49, 18, -74, 57, 85, -79, -125, -82, -113, 97, 35, -91, -122, -68, 82, -30, 2, -53, -18, 28, -24, -34,
                32, 82, -65, 119, 78, -21, 23, -111, -49, -11, -124, -10, 63, -38, -80, 69, -83, -10, 85, 22, -75, 80,
                27, -64, -63, 102, -2, -76, -50, 89, 91, -8, 36, 107, -61, -93, -1, 56, 62, -127, 30, -117, 114, -128,
                -41, -113, 20, 50, -112, -67, -74, 97, -105, 43, -123, -39, -4, 46, -60, -22, 24, -4, 3, 56, -116, 93,
                -22, -77, 48, -43, -125, -118, 73, 65, -76, 34, -14, 14, 25, 47, 44, -73, 62, -50, 33, -124, 112, -51,
                -88, -83, 27, -94, -20, 28, 56, 67, 32, 14, 0, 84, 17, -30, 68, -1, -97, 17, 56, -118, 99, 65, -86, 4,
                -68, 110, -35, 28, -109, -33, 86, -114, 10, 25, 56, -76, 120, 26, 93, -39, 25, 76, -57, -72, -45, 49,
                112, -124, 69, -115, 10, -41, 97, 18, 123, -100, 108, 109, 50, 28, -126, 35, 105, -54, -44, -102, -7,
                -15, 88, -116, 92, -80, 6, -28, 90, -90, 11, -112, 125, 12, 39, -8, -14, -13, -22, -66, -80, 118, -41,
                3, -90, -31, -95, 46, -53, 28, -116, -64, 80, 65, 71, -78, -3, 76, 89, 36, 21, 101, 52, -40, 32, 81,
                -80, 88, -92, -125, -33, -25, 125, -61, 13, 119, -99, -4, -23 };

        String originalData = EncryptionUtils.decrypt(bytes, credential.getPublicKey());
        assertNotNull(originalData);
    }
}
