package ee.netgroup.htm.components;

import ee.netgroup.htm.helpers.Constants;
import ee.netgroup.htm.helpers.Helpers;
import ee.netgroup.htm.page.MaterialPage;
import ee.netgroup.htm.page.PortfolioPage;
import org.openqa.selenium.By;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class Comments {

    private static By showCommentsButton = By.id("show-comments");
    private static By addCommentField = By.xpath("//textarea[@data-ng-model='newComment.text']");
    private static By addComment = By.xpath("//button[@data-ng-click='$ctrl.addComment()']");
    private static By comment = By.xpath("//p[@data-ng-bind='comment.text']");
    private static By reportCommentButton = By.xpath("//button[@ng-click='$ctrl.reportComment($event)']");


    public static PortfolioPage showPortfolioComments() {
        getWebDriver().findElement(showCommentsButton).click();
        return new PortfolioPage();
    }

    public static MaterialPage showMaterialComments() {
        Helpers.waitForClickable(showCommentsButton);
        getWebDriver().findElement(showCommentsButton).click();
        return new MaterialPage();
    }

    public static PortfolioPage addNewComment() {
        Helpers.waitForVisibility(addCommentField);
        getWebDriver().findElement(addCommentField).sendKeys(Constants.commentText);
        getWebDriver().findElement(addComment).click();
        return new PortfolioPage();
    }

    public static String getCommentText() {
        return getWebDriver().findElement(comment).getText();
    }

    public static ReportImproperModal reportImproperComment() {
        getWebDriver().findElement(reportCommentButton).click();
        return new ReportImproperModal();
    }


}
