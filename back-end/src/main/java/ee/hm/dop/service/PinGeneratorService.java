package ee.hm.dop.service;

import java.security.SecureRandom;

public class PinGeneratorService {

    public static int generatePin() {

        SecureRandom secureRandom = new SecureRandom();

        String number = String.format("%04d", secureRandom.nextInt(10000));

        return Integer.parseInt(number);

    }
}
