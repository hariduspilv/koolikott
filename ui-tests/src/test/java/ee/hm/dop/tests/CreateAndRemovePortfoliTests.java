package ee.hm.dop.tests;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import static ee.hm.dop.page.LandingPage.goToLandingPage;
import org.junit.Test;
import org.junit.runners.MethodSorters;


    @FixMethodOrder(MethodSorters.NAME_ASCENDING)


public class CreateAndRemovePortfoliTests {
    	
    	@Test
    	public void createPortfolio() {

    		String portfolioIsCreatedAlertText = goToLandingPage()
    				.chooseUserType("SmallPublisher")
    				.getFabButton()
    				.clickAddPortfolio()
    				.insertPortfolioTitle()
    				.selectEducationalContext()
    				.selectSubjectArea()
    				.selectAgeGroup()
    				.uploadPhoto()
    				.addDescription()
    				.clickCreatePortfolioButton()
    				.getSuccessAlertText();

    		Assert.assertEquals("Kogumik loodud! Täienda seda võtmesõnadega ja jätkake alloleva tööriistariba abil peatükkide, alampeatükkide, sisutekstide ja materjalide lisamisega.", portfolioIsCreatedAlertText);

    	}
    	
    	@Test
    	public void createPortfolioAndThisCopy() {

    		boolean portfolioCopyWasMade = goToLandingPage()
    				.chooseUserType("User")
    				.getFabButton()
    				.clickAddPortfolio()
    				.insertPortfolioTitle()
    				.selectEducationalContext()
    				.selectSubjectArea()
    				.uploadPhoto()
    				.selectAgeGroup()
    				.addDescription()
    				.clickCreatePortfolioButton()
    				.clickExitAndSave()
    				.getPrivacyConfirmationPopup()
    				.makePortfolioPublic()
    				.moveCursorToCopyPortfolio()
    				.clickCopyPortfolio()
    				.insertSpecificPortfolioTitle("(Copy)")
    				.savePortfolioCopy()
    				.clickExitAndSave()
    				.getPrivacyConfirmationPopup()
    				.makePortfolioPublic()
    				.wordCopyIsAddedToPortfolioTitle();
    		
    		assertTrue(portfolioCopyWasMade);

    	}
    	
    	@Test
    	public void removePortfolio() {

    		String deletedPortfolioToast = goToLandingPage()
    				.chooseUserType("User")
    				.openPortfolio()
    				.clickActionsMenu()
    				.clickRemovePortfolio()
    				.confirmImproperContent()
    				.getLandingPage()
    				.isPortfolioDeletedToastVisible();

    		Assert.assertEquals("Kogumik kustutatud", deletedPortfolioToast);

    	}

}
