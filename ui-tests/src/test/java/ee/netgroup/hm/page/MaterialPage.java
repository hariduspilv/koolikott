package ee.netgroup.hm.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import ee.netgroup.hm.components.Comments;
import ee.netgroup.hm.components.ConfirmationPopup;
import ee.netgroup.hm.components.EditMaterialPopUp;
import ee.netgroup.hm.components.ReportImproperPopUp;
import ee.netgroup.hm.helpers.Arrays;
import ee.netgroup.hm.helpers.Constants;
import ee.netgroup.hm.helpers.Helpers;

public class MaterialPage extends Page{
	
	private By publisherName = By.xpath("//span[@data-ng-repeat='publisher in material.publishers']");
	private By selectMaterialBox = By.xpath("//div[@class='card-cover  audiotrack']");
	private By materialType = By.cssSelector("p [data-translate=AUDIO]");
	private By materialDescription = By.xpath("//div[@data-ng-bind-html='getCorrectLanguageString(material.descriptions)']");
	private By editMaterial = By.xpath("//button[@data-ng-click='edit()']");
	private By creatorName = By.xpath("//p[@data-ng-if='isNullOrZeroLength(material.authors)']");
	private By deleteMaterial = By.xpath("//button[@data-ng-click='confirmMaterialDeletion()']");
	private By bannerRestoreButton = By.xpath("//button[@aria-label='Taasta']");
	private By likeIcon = By.xpath("//div[@data-ng-click='$ctrl.like()']");
	private By isLiked = By.xpath("//span[@data-ng-bind='$ctrl.rating.likes']");
	private By selectedStar = By.xpath("//md-icon[@data-ng-if='$ctrl.hasFavorited']");
	private By unselectedStar = By.xpath("//div[@class='md-icon-button md-button favorite']");
	private By tagRow = By.xpath("(//input[starts-with(@id, 'fl-input-')])");
	private By showMoreButton = By.xpath("//button[@ng-click='$ctrl.showMore()']");
	private By addedTag = By.xpath("//a[@data-ng-click='$ctrl.getTagSearchURL($event, $chip.tag)']");
	private By errorBanner = By.id("error-message-heading");
	private By reportMaterial = By.xpath("//span[contains(text(), 'Teavita katkisest materjalist')]");
	private By doneButton = By.xpath("//md-icon[contains(text(), 'done')]");
	private By restoreButton = By.xpath("//button[@data-ng-click='button.onClick()']");
	private By restoreBrokenMaterialButton = By.xpath("//button[2][@data-ng-click='button.onClick()']");
	private By insertTag = By.xpath("(//input[starts-with(@id, 'fl-input-')])");
	private String newTag = Helpers.generateNewTag();
	private By reportTagButton = By.xpath("//button[@ng-click='$ctrl.reportTag($event)']");
	private By improperContent = By.xpath("//span[@data-translate='REPORT_IMPROPER']");
	
	
	public String getPublisherName() {
		return getDriver().findElement(publisherName).getText();
	}

	public String getMaterialType() {
		Helpers.waitForVisibility(selectMaterialBox);
		Helpers.moveToElement(selectMaterialBox);
		return getDriver().findElement(materialType).getText();
	}

	public String getMaterialDescription() {
		return getDriver().findElement(materialDescription).getText();
	}

	public MaterialPage clickActionsMenu() {
		Helpers.waitForVisibility(Constants.actionsMenu);
		getDriver().findElement(Constants.actionsMenu).click();
		return this;
	}

	public EditMaterialPopUp clickEditMaterial() {
		Helpers.waitForClickable(editMaterial);
		getDriver().findElement(editMaterial).sendKeys(Keys.ENTER);
		return new EditMaterialPopUp();
	}

	public String getCreatorName() {
		return getDriver().findElement(creatorName).getText();
	}

	public ConfirmationPopup clickDeleteMaterial() {
		Helpers.waitForClickable(deleteMaterial);
		getDriver().findElement(deleteMaterial).click();
		return new ConfirmationPopup();
	}

	public boolean isMaterialDeletedBannerVisible() {
		Helpers.waitForMilliseconds(1000);
		return getDriver().findElement(bannerRestoreButton).isDisplayed();
	}

	public MaterialPage likeMaterial() {
		getDriver().findElement(likeIcon).click();
		return this;
	}

	public String getLikesNumber() {
		return getDriver().findElement(isLiked).getText();
	}

	public boolean starIsSelected() {
		Helpers.waitForVisibility(selectedStar);
		return getDriver().findElement(selectedStar).isDisplayed();
	}

	public MaterialPage unselectStar() {
		Helpers.waitForVisibility(selectedStar);
		getDriver().findElement(selectedStar).click();
		return this;
	}

	public boolean starIsUnselected() {
		return getDriver().findElement(unselectedStar).isDisplayed();
	}

	public MaterialPage insertTags() {
		for (int i = 0; i < 13; i++) {
			getDriver().findElement(tagRow).sendKeys(Helpers.randomElement(Arrays.tagsArray) + "");
			getDriver().findElement(tagRow).sendKeys(Keys.ENTER);
			Helpers.waitForMilliseconds(1000);
		}
		return this;
	}

	public boolean showMoreButtonIsDisplayed() {
		return getDriver().findElement(showMoreButton).isDisplayed();
	}

	public int getTagsCount() {
		return getDriver().findElements(addedTag).size();
	}

	public String getUnreviewedBannerText() {
		return getDriver().findElement(errorBanner).getText();
	}

	public ConfirmationPopup clickReportBrokenMaterial() {
		Helpers.waitForClickable(reportMaterial);
		Helpers.waitForMilliseconds(1000);
		getDriver().findElement(reportMaterial).click();
		return new ConfirmationPopup();
	}

	public String getNotificationIsSentText() {
		return getDriver().findElement(Constants.toastText).getText();
	}

	public MaterialPage restoreMaterial() {
		Helpers.waitForClickable(restoreButton);
		getDriver().findElement(restoreButton).click();
		Helpers.waitForMilliseconds(3000);
		return this;
	}
	
	public MaterialPage restoreBrokenMaterial() {
		Helpers.waitForClickable(restoreBrokenMaterialButton);
		getDriver().findElement(restoreBrokenMaterialButton).click();
		Helpers.waitForMilliseconds(3000);
		return this;
	}

	public boolean isErrorBannerHidden() {
		Helpers.waitForMilliseconds(1000);
		return getDriver().findElements(errorBanner).size() < 1;
	}

	public MaterialPage markContentAsNotImproper() {
		Helpers.waitForClickable(doneButton);
		getDriver().findElement(doneButton).click();
		return this;
	}

	public MaterialPage addNewTag() {
		Helpers.waitForVisibility(insertTag);
		getDriver().findElement(insertTag).sendKeys(newTag);
		Helpers.waitForMilliseconds(1000);
		getDriver().findElement(insertTag).sendKeys(Keys.ENTER);
		Helpers.waitForMilliseconds(2000);
		return this;
	}

	public ReportImproperPopUp reportImproperTag() {
		getDriver().findElement(reportTagButton).click();
		return new ReportImproperPopUp();
	}

	public ReportImproperPopUp clickReportImproperContent() {
		Helpers.waitForClickable(improperContent);
		getDriver().findElement(improperContent).click();
		return new ReportImproperPopUp();
	}

	public MaterialPage showMaterialComments() {
		return Comments.showMaterialComments();
	}

	public ReportImproperPopUp reportImproperComment() {
		return Comments.reportImproperComment();
	}



}
