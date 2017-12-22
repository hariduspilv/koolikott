package ee.netgroup.hm.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import ee.netgroup.hm.components.MaterialModal;
import ee.netgroup.hm.components.Comments;
import ee.netgroup.hm.components.ConfirmationModal;
import ee.netgroup.hm.components.ReportImproperModal;
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
	private By likeIcon = By.xpath("//div[@data-ng-click='$ctrl.like()']");
	private By isLiked = By.xpath("//span[@data-ng-bind='$ctrl.rating.likes']");
	private By selectedStar = By.xpath("//md-icon[@data-ng-if='$ctrl.hasFavorited']");
	private By unselectedStar = By.xpath("//div[@class='md-icon-button md-button favorite']");
	private By tagRow = By.xpath("(//input[starts-with(@id, 'fl-input-')])");
	private By showMoreButton = By.xpath("//button[@ng-click='$ctrl.showMore()']");
	private By addedTag = By.xpath("//a[@data-ng-click='$ctrl.getTagSearchURL($event, $chip.tag)']");
	private By insertTag = By.xpath("(//input[starts-with(@id, 'fl-input-')])");
	private String newTag = Helpers.generateNewTag();
	private By reportTagButton = By.xpath("//button[@ng-click='$ctrl.reportTag($event)']");
	private By improperContent = By.xpath("//span[@data-translate='REPORT_IMPROPER']");
	private By markAsReviewed = By.xpath("//button[@aria-label='Märgi ülevaadatuks']");
	private By declineButton = By.xpath("//button[1][@data-ng-click='button.onClick($ctrl)']");
	private By materialUrl = By.xpath("//a[@data-ng-bind='url']");

	
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

	public MaterialModal clickEditMaterial() {
		Helpers.waitForClickable(editMaterial);
		getDriver().findElement(editMaterial).sendKeys(Keys.ENTER);
		return new MaterialModal();
	}

	public String getCreatorName() {
		return getDriver().findElement(creatorName).getText();
	}

	public ConfirmationModal clickDeleteMaterial() {
		Helpers.waitForClickable(deleteMaterial);
		getDriver().findElement(deleteMaterial).click();
		return new ConfirmationModal();
	}

	public String getDeletedBannerText() {
		Helpers.waitForMilliseconds(1000);
		return getDriver().findElement(Constants.bannerText).getText();
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
		Helpers.waitForMilliseconds(2500);
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
		return getDriver().findElement(Constants.bannerText).getText();
	}

	public String getNotificationIsSentText() {
		return getDriver().findElement(Constants.toastText).getText();
	}

	public MaterialPage addNewTag() {
		Helpers.waitForVisibility(insertTag);
		getDriver().findElement(insertTag).sendKeys(newTag);
		Helpers.waitForMilliseconds(1000);
		getDriver().findElement(insertTag).sendKeys(Keys.ENTER);
		Helpers.waitForMilliseconds(2000);
		return this;
	}

	public ReportImproperModal reportImproperTag() {
		getDriver().findElement(reportTagButton).click();
		return new ReportImproperModal();
	}

	public ReportImproperModal clickReportImproperContent() {
		Helpers.waitForClickable(improperContent);
		getDriver().findElement(improperContent).click();
		return new ReportImproperModal();
	}

	public MaterialPage showMaterialComments() {
		return Comments.showMaterialComments();
	}

	public ReportImproperModal reportImproperComment() {
		return Comments.reportImproperComment();
	}

	public MaterialPage markNewMaterialAsReviewed() {
		Helpers.waitForClickable(markAsReviewed);
		getDriver().findElement(markAsReviewed).click();
		return this;
	}

	public String getChangedLinkBannerText() {
		return getDriver().findElement(Constants.bannerText).getText();
	}

	public MaterialPage markChangesDeclined() {
		Helpers.waitForClickable(declineButton);
		getDriver().findElement(declineButton).click();
		return this;
	}
	
	public String getMaterialUrlText() {
		Helpers.waitForMilliseconds(1000);
		return getDriver().findElement(materialUrl).getText();
	}



}
