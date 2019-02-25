'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.captchaKey = ''
            this.getCaptchaKey()
            this.$scope.isSendBtnVisible = false

            this.$scope.cancel = () => {
                this.$mdDialog.hide();
            };
        }

        isSendButtonDisabled(){

            return this.$scope.captchaSuccess
        }

        sendEmail(){
            let content
            this.$scope.isSaving = true

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
    ]
    angular.module('koolikottApp').controller('sendEmailDialogController', controller)
}
