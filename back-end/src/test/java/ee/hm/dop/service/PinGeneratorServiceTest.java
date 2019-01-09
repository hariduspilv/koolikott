package ee.hm.dop.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PinGeneratorServiceTest {

    private PinGeneratorService pinGeneratorService = new PinGeneratorService();

    @Test
    public void checkRandomness() {

        int randomNumber1 = pinGeneratorService.generatePin();
        int randomNumber2 = pinGeneratorService.generatePin();

        assertEquals(4, String.valueOf(randomNumber1).length());
        assertEquals(4, String.valueOf(randomNumber2).length());

        assertNotEquals(randomNumber1, randomNumber2);

    }
}