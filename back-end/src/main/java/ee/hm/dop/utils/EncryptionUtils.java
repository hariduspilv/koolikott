package ee.hm.dop.utils;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class EncryptionUtils {

    public static final String ALGORITHM = "RSA";

    public static byte[] encrypt(String text, PrivateKey key) {
        byte[] cipherText = null;
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    public static String decrypt(byte[] text, PublicKey key) {
        String decryptedText = null;
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] bytes = cipher.doFinal(text);
            decryptedText = new String(bytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return decryptedText;
    }
}
