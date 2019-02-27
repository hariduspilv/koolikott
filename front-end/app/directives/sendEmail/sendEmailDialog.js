'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.emailObject = {}
            this.$scope.captchaKey = ''
            this.getCaptchaKey()
            this.checkUserEmail()
            this.$scope.isSendBtnVisible = false

            this.$scope.userHasEmail = false
            this.$scope.emailContent = ''
            this.$scope.isSaving = false

            this.$scope.cancel = () => {
                this.$mdDialog.hide();
            };
        }

        isSendButtonDisabled() {

            const {emailContent} = this.$scope.emailObject;

            if (this.$scope.emailContent.length < 1 || !this.$scope.captchaSuccess())
                return true;
        }

        checkUserEmail() {

            let portfolioCreator = this.$rootScope.portfolioCreator;

            this.userEmailService.userHasEmail(portfolioCreator)
                .then(({data}) => {
                        if (data) {
                            this.$scope.userHasEmail = data;
                        }
                    }
                );
        }

        sendEmail(){
            let content;

            this.$scope.isSaving = true

            if (this.$scope.emailContent.length > 0)
                content = this.$scope.emailContent

            const {emailcontent} = this.$scope.emailObject

            this.serverCallService.makePost('/rest/admin/???',  {})
                .then(response => {
                        this.$scope.isSaving = false
                        if (response.status === 200) {
                            this.$scope.captchaSuccess = false
                        } else {
                            this.$scope.captchaSuccess = false
                        }
                    }, () =>
                        this.$scope.isSaving = false
                )
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
    }

    controller.$inject = [
        '$scope',
        'dialogService',
        '$window',
        '$mdDialog',
        '$location',
        'locals',
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
