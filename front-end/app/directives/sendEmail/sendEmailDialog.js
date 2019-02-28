'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.emailObject = {}
            this.$scope.captchaKey = ''
            this.getCaptchaKey()

            this.$scope.isSendBtnVisible = false

            this.$scope.userHasEmail = false
            this.$scope.emailContent = ''
            this.$scope.isSaving = false

            this.$scope.cancel = () => {
                this.$mdDialog.hide();
            };
        }

        isSendButtonDisabled() {
            return (this.$scope.emailContent.length < 3 || !this.$scope.captchaSuccess)
        }

        sendEmail() {

            this.$scope.isSaving = true

            this.userEmailService.saveEmailForCreator(this.$scope.emailContent)
                .then(response => {
                    this.$scope.isSaving = false
                    if (response.status === 200) {
                        alert('töötab')
                        this.setTimeout(this.showSentEmailDialog(), 2000)
                    }
                })
        }

        captchaSuccess() {
            this.$scope.captchaSuccess = true
        }
        getCaptchaKey() {
            this.serverCallService.makeGet('/rest/captcha')
                .then(({data}) => {
                    this.$scope.captchaKey = data
                })
        }

        getLanguage() {
            let language = this.translationService.getLanguage();
            return language === 'est' ? 'et' : language === 'rus' ? 'ru' : 'en';
        }

        captchaExpired() {
            this.$scope.captchaSuccess = false
        }

        resetCaptcha() {
            if (this.grecaptcha.getResponse() !== '') {
                this.grecaptcha.reset();
            }
        }
        showSentEmailDialog(){
            this.$mdDialog.show({
                template: `<md-dialog aria-label="Email edukalt saadetud modaalaken" class="login-dialog">
                    <md-toolbar class="md-accent">
                      <div class="md-toolbar-tools" flex>
                        <span flex></span>
                        <md-button ng-click="cancel()" id="email-sent class="md-icon-button"
                                   aria-label="Email edukalt saadetud modaalaken">
                          <md-icon>close</md-icon>
                        </md-button>
                      </div>
                    </md-toolbar>
                    <md-dialog-content>
                      <md-content data-layout-padding>
                        <p><span id="location-restore-text" data-translate="SEND_EMAIL_SENT"></span></p>
                      </md-content>
                    </md-dialog-content>
                   </md-dialog>`,
                controller: 'sendEmailDialogController',
                controllerAs: '$ctrl',
                clickOutsideToClose: true,

            })

        }
    }

    controller.$inject = [
        '$scope',
        'dialogService',
        '$window',
        '$mdDialog',
        '$location',
        '$interval',
        'vcRecaptchaService',
        'serverCallService',
        'translationService',
        'userEmailService',
        'authenticatedUserService',
        '$rootScope'
    ]
    angular.module('koolikottApp').controller('sendEmailDialogController', controller)
}
