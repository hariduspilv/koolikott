package ee.hm.dop.service;

import java.security.SecureRandom;

public class PinGeneratorService {

    public static String generatePin() {
        SecureRandom secureRandom = new SecureRandom();
        return String.format("%04d", secureRandom.nextInt(10000));
    }
}
