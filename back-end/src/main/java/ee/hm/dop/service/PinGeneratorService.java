package ee.hm.dop.service;

import org.apache.commons.lang3.RandomStringUtils;

public class PinGeneratorService {

    public static int generatePin() {

        int num;

        do {
            num = Integer.parseInt(RandomStringUtils.randomNumeric(4));

        } while (String.valueOf(num).length() != 4);

        return num;

    }
}
