'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.emailObject = {}
            this.$scope.captchaKey = ''
            this.getCaptchaKey()
            this.$scope.emailToCreator = {}

            this.$scope.cancel = () => {
                this.$mdDialog.hide();
            };

            this.$scope.isSendButtonDisabled = () => {
                return (!this.$scope.emailToCreator.emailContent || !this.$scope.captchaSuccess)
            }
        }

        sendEmail() {

            this.$scope.isSaving = true
            this.serverCallService.makePost('rest/userEmail/sendEmailToCreator',
                {
                    message: this.$scope.emailToCreator.emailContent,
                    creatorId:this.locals.learningObject.creator.id,
                    learningObjectId:this.locals.learningObject.id,
                    learningObjectTitle:this.locals.learningObject.title
                })
                .then(response => {
                        this.$scope.isSaving = false

                        if (response.status === 200) {
                            this.$scope.emailSent = true
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
        '$interval',
        'vcRecaptchaService',
        'serverCallService',
        'translationService',
        'userEmailService',
        'authenticatedUserService',
        'locals',
        '$rootScope',
    ]
    angular.module('koolikottApp').controller('sendEmailDialogController', controller)
}
