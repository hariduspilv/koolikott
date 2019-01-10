package ee.hm.dop.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PinGeneratorServiceTest {

    @Test
    public void checkPinLengthAndUniqueness() {

        int randomNumber1 = PinGeneratorService.generatePin();
        int randomNumber2 = PinGeneratorService.generatePin();

        String temp1 = String.valueOf(randomNumber1).substring(0,1);
        String temp2 = String.valueOf(randomNumber2).substring(0,1);

        if (temp1.substring(0,1).equalsIgnoreCase("0")){
            assertEquals(4, String.valueOf(randomNumber1).length());
        }

        if (temp2.substring(0,1).equalsIgnoreCase("0")){
            assertEquals(4, String.valueOf(randomNumber2).length());
        }

        assertNotEquals(randomNumber1, randomNumber2);
    }
}