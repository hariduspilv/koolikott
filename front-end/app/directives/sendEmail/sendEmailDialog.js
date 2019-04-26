'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.emailObject = {}
            this.$scope.captchaKey = ''
            this.getCaptchaKey()
            this.$scope.emailToCreator = {}
            this.setPlaceholder()
            this.$scope.emailToCreator.title = this.getCorrectLanguageTitle(this.locals.learningObject)
            this.$scope.cancel = () => {
                this.$mdDialog.hide();
            };

            this.$scope.isSendButtonDisabled = () => {
                return (!this.$scope.emailToCreator.emailContent || !this.$scope.captchaSuccess)
            }
        }

        setPlaceholder() {
            if (!this.$scope.emailToCreator.emailContent || this.$scope.emailToCreator.emailContent.length <= 0) {
                this.$translate('SEND_EMAIL_CONTENT').then(value => {
                    this.$scope.placeholder = value
                })
            } else {
                this.$translate('SEND_EMAIL_CONTENT_COUNTER').then(value => {
                    this.$scope.placeholder = (value.replace('${counter}', 500 - this.$scope.emailToCreator.emailContent.length))
                })
            }
        }

        sendEmail() {

            this.$scope.isSaving = true
            if (this.locals.learningObject.type === '.Material')
                this.locals.learningObject.title = this.locals.learningObject.titles[0].text;

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
        '$translate'
    ]
    angular.module('koolikottApp').controller('sendEmailDialogController', controller)
}
