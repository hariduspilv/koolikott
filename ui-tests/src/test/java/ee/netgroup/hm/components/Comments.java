package ee.netgroup.hm.components;

import org.openqa.selenium.By;
import ee.netgroup.hm.helpers.Constants;
import ee.netgroup.hm.helpers.Helpers;
import ee.netgroup.hm.page.MaterialPage;
import ee.netgroup.hm.page.PortfolioPage;

public class Comments extends Component{
		
	private static By showCommentsButton = By.id("show-comments");
	private static By addCommentField = By.xpath("//textarea[@data-ng-model='newComment.text']");
	private static By addComment = By.xpath("//button[@data-ng-click='$ctrl.addComment()']");
	private static By comment = By.xpath("//p[@data-ng-bind='comment.text']");
	private static By reportCommentButton = By.xpath("//button[@ng-click='$ctrl.reportComment(comment, $event)']");
	
	
	public static PortfolioPage showPortfolioComments() {
		getDriver().findElement(showCommentsButton).click();
		return new PortfolioPage();
	}
	
	public static MaterialPage showMaterialComments() {
		getDriver().findElement(showCommentsButton).click();
		return new MaterialPage();
	}

	public static PortfolioPage addNewComment() {
		Helpers.waitForVisibility(addCommentField);
		getDriver().findElement(addCommentField).sendKeys(Constants.commentText);
		getDriver().findElement(addComment).click();
		return new PortfolioPage();
	}
	
	public static String getCommentText() {
		return getDriver().findElement(comment).getText();
	}
	
	public static ReportImproperPopUp reportImproperComment() {
		getDriver().findElement(reportCommentButton).click();
		return new ReportImproperPopUp();
	}


}
