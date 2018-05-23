package ee.hm.dop.service.login;

import java.math.BigInteger;
import java.security.SecureRandom;

public class TokenGenerator {

    private SecureRandom random = new SecureRandom();

    public String secureToken() {
        return new BigInteger(130, random).toString(32);
    }
}
