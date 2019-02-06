package ee.netgroup.htm.util;

import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.openqa.selenium.OutputType.FILE;

public class ScreenshotUtils {

    public static BufferedImage captureElementScreenshotAsBufferedImage(WebElement webElement) {
        BufferedImage eleScreenshot = null;

        // Get entire page screenshot

        try {
            BufferedImage fullImg = ImageIO.read(((TakesScreenshot) getWebDriver()).getScreenshotAs(FILE));
            // Get the location of element on the page
            Point point = webElement.getLocation();

            // Get width and height of the element
            int eleWidth = webElement.getSize().getWidth();
            int eleHeight = webElement.getSize().getHeight();

            // Crop the entire page screenshot to get only element screenshot
            eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(),
                    eleWidth, eleHeight);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return eleScreenshot;
    }
}
