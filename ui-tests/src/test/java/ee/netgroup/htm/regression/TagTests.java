package ee.netgroup.htm.regression;

import ee.netgroup.htm.BaseTest;
import ee.netgroup.htm.framework.listeners.SeleniumTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static ee.netgroup.htm.components.FabButton.getFabButton;
import static ee.netgroup.htm.page.LandingPage.goToLandingPage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Listeners({SeleniumTestListener.class})
public class TagTests extends BaseTest {

    @Test
    public void material_should_inherit_opened_portfolio_tags() {
        goToLandingPage().chooseUserType("User");

        getFabButton()
                .clickAddPortfolio()
                .createDefaultPortfolio()
                .addTag("TAG")
                .addTag("GOMBA")
                .clickSaveAndExitConfirmationControl();

        int numberOfTags = getFabButton()
                .clickAddMaterial()
                .setTitle("Default Material")
                .setRandomHyperLink()
                .selectDefaultLicenseType()
                .setIsSelfAuthored()
                .clickCreateMaterial()
                .getNumberOfTags();

        assertThat(numberOfTags, is(2));
    }

}
