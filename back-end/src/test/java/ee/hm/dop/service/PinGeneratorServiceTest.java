package ee.hm.dop.service;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class PinGeneratorServiceTest {

    @Test
    public void pin_is_generated_as_positive_number_up_to_4_digits() {
        int randomNumber1 = PinGeneratorService.generatePin();
        int randomNumber2 = PinGeneratorService.generatePin();

        assertLenght1To4Digits(randomNumber1);
        assertLenght1To4Digits(randomNumber2);

        if (randomNumber1 == randomNumber2) {
            randomNumber2 = PinGeneratorService.generatePin();
        }
        assertNotEquals(randomNumber1, randomNumber2);
    }

    private void assertLenght1To4Digits(int randomNumber1) {
        int length1 = String.valueOf(randomNumber1).length();
        assertTrue(length1 >= 1 && length1 <= 4);
    }
}