package ee.netgroup.htm.page;

import com.codeborne.selenide.Condition;
import ee.netgroup.htm.components.ChangeUserDetailsModal;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static ee.netgroup.htm.util.JSWaiter.waitForJQueryAndAngularToFinishRendering;

public class ModeratorManagementPage extends Page {
    private By searchUser = By.name("autocompleteField");
    private By filterSuggestionsByUserInput = By.xpath("//span[@md-highlight-text='filterUsersInput']");
    private By editUser = By.xpath("//button[@data-ng-click='$ctrl.editUser(selectedUser)']");



    public ModeratorManagementPage() {
        waitForJQueryAndAngularToFinishRendering();
    }

    public ModeratorManagementPage insertAndSelectUserName(String name) {
            $(searchUser).shouldBe(Condition.visible).sendKeys(name);
            $(filterSuggestionsByUserInput).shouldHave(Condition.text(name)).click();
        return this;
    }

    public ChangeUserDetailsModal clickOpen() {
        $(editUser).click();
        return new ChangeUserDetailsModal();
    }



}
