package ee.netgroup.htm.components;

import com.codeborne.selenide.Condition;
import ee.netgroup.htm.helpers.Helpers;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static ee.netgroup.htm.enums.VocationalEducationSubjectArea.ARTS;

public class ChangeUserDetailsModal {

    private By selectRoleDropdown = By.xpath("//md-select[@ng-model='selectedRole']");
    private By userDropdownItem = By.xpath("//md-option[@value='USER']");
    private By adminDropdownItem = By.xpath("//md-option[@value='ADMIN']");
    private By restrictedDropdownItem = By.xpath("//md-option[@value='RESTRICTED']");
    private By moderatorDropdownItem = By.xpath("//md-option[@value='MODERATOR']");
    private By selectEducationLevel = By.id("taxonEducationalSelect");
    private By educationalContext = By.xpath("(//md-select[@id='taxonEducationalSelect'])[2]");
    private By educationalTaxonVocational = By.xpath("(//md-option[@data-translate='VOCATIONALEDUCATION'])[2]");
    private By educationalTaxonPreschool = By.cssSelector("md-option[data-translate='PRESCHOOLEDUCATION']"); //also: BASICEDUCATION, SECONDARYEDUCATION, VOCATIONALEDUCATION, HOBBYACTIVITIES
    private By taxonDomainForVocational = By.cssSelector("form[name='taxonForm'] > md-input-container > md-select#taxonDomainForVocationaleducationSelect");
    private By subjectAreaForPreschoolEducation = By.cssSelector("md-select[id='taxonDomainForPreschooleducationSelect']");
    private By movementSubjectForPreschool = By.cssSelector("md-option[data-translate='DOMAIN_MOVEMENT']");
    private By addNewTaxon = By.cssSelector("button[data-ng-click='addNewTaxon()']");
    private By removeFirstTaxon = By.cssSelector("button[data-ng-click='deleteTaxon($index)']");
    private By cancelButton = By.id("add-edit-user-button");
    private By saveButton = By.id("update-user-button");


    public static ChangeUserDetailsModal getChangeUserDetailsModal() {
        return new ChangeUserDetailsModal();
    }

    public void changeUserRole(String role) {
        $(selectRoleDropdown).click();
        $(By.xpath("//md-option[@value=\"" + role + "\"]")).click();

    }

    public ChangeUserDetailsModal changeUserRoleToModerator(){
        $(selectRoleDropdown).click();
        $(moderatorDropdownItem).click();
        return this;
    }

    public ChangeUserDetailsModal changeUserRoleToUser() {
        $(selectRoleDropdown).click();
        $(userDropdownItem).click();
        return this;
    }

    public ChangeUserDetailsModal changeUserRoleToRestricted() {
        $(selectRoleDropdown).click();
        $(restrictedDropdownItem).click();
        return this;
    }

    public ChangeUserDetailsModal selectSubjectAreaForVocationEducation(String subjectAreaTranslationKey) {
        Helpers.waitForClickable(taxonDomainForVocational).click();
        Helpers.waitForClickable(By.xpath("(//md-option[@data-translate=\"" + subjectAreaTranslationKey + "\"])[2]")).click();
        return this;
    }

    public ChangeUserDetailsModal removeFirstTaxon() {
        $$(removeFirstTaxon).first().click();
        return this;
    }


    public ChangeUserDetailsModal clickToAddNewTaxon() {
        $(addNewTaxon).shouldBe(Condition.visible).click();
        return this;
    }

    public ChangeUserDetailsModal addNewTaxon_VocationalEducation() {
        Helpers.waitForClickable(educationalContext).click();
        Helpers.waitForClickable(educationalTaxonVocational).click();
        return this;
    }

    public ChangeUserDetailsModal addNewTaxon_Preschool() {
        $(educationalContext).shouldBe(Condition.visible).click();
        $$(educationalTaxonPreschool).last().click();
        return this;
    }

    public ChangeUserDetailsModal addMovementSubjectAreaForPreschoolTaxon() {
        $$(subjectAreaForPreschoolEducation).last().click();
        $$(movementSubjectForPreschool).last().click();
        return this;
    }

    public ChangeUserDetailsModal addArtsSubjectAreaforTaxonDomain() {
        //$(taxonDomainForVocational).click();
        selectSubjectAreaForVocationEducation(ARTS.getTranslationKey());
        return this;
    }

    public void clickSave(){
        $(saveButton).click();
    }
}
