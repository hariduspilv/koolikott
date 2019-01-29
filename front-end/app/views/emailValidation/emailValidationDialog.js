'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.setFormToEmpty()
            this.setResponse()
            this.$scope.emailValidationForm = {}
            this.$scope.isSending = false
            this.$scope.input = [this.$scope.firstNum, this.$scope.secondNum, this.$scope.thirdNum, this.$scope.fourthNum]

            this.unsubscribeRouteChangeSuccess = this.$rootScope.$on('$routeChangeSuccess', () => this.$mdDialog.hide())
            this.$scope.$watch(
                () => this.authenticatedUserService.isAuthenticated(),
                (newValue, oldValue) => newValue === true && this.$mdDialog.hide(),
                false
            );

            this.$scope.checkPin = () => {
                this.$scope.emailValidationForm.$setValidity('validationError', true)
                if (this.$scope.emailValidationForm.$valid && this.isNotEmpty()) {
                    this.$scope.isSending = true
                    let pin = this.$scope.firstNum + this.$scope.secondNum + this.$scope.thirdNum + this.$scope.fourthNum
                    this.userEmailService.validatePin(this.$rootScope.userFromAuthentication, pin, this.$rootScope.email)
                        .then(response => {
                            if (response.status === 200) {
                                this.$mdDialog.hide(true)
                                this.$scope.isSending = false
                            }
                        }).catch( () => {
                        this.$scope.emailValidationForm.$setValidity('validationError', false)
                        this.setTouchedFalse()
                        this.setFormToEmpty()
                        this.$scope.isSending = false
                        this.$timeout( () => {
                            angular.element('#email-firstNum').focus()
                        }, 10);

                    })
                }
            }

            document.addEventListener("keypress", (evt) => {
                if (evt.which < 48 || evt.which > 57)
                    evt.preventDefault();
            });
        }

        setResponse() {
            this.$translate('EMAIL_VALIDATION_DIALOG_TEXT').then((value) => {
                this.$scope.emailDialogText = (value.replace('${email}', this.$rootScope.email));
            })
        }
        setFormToEmpty() {
            this.$scope.firstNum = ''
            this.$scope.secondNum = ''
            this.$scope.thirdNum = ''
            this.$scope.fourthNum = ''
        }

        isNotEmpty() {
            return !!(this.$scope.firstNum && this.$scope.secondNum && this.$scope.thirdNum && this.$scope.fourthNum)
        }
        $onDestroy() {
            if (typeof this.unsubscribeRouteChangeSuccess === 'function')
                this.unsubscribeRouteChangeSuccess()
        }

        setTouchedFalse() {
            this.$scope.emailValidationForm.firstNum.$touched = !this.$scope.emailValidationForm.firstNum.$touched
            this.$scope.emailValidationForm.secondNum.$touched = !this.$scope.emailValidationForm.secondNum.$touched
            this.$scope.emailValidationForm.thirdNum.$touched = !this.$scope.emailValidationForm.thirdNum.$touched
            this.$scope.emailValidationForm.fourthNum.$touched = !this.$scope.emailValidationForm.fourthNum.$touched
        }
    }
    controller.$inject = [
        '$scope',
        '$rootScope',
        '$mdDialog',
        'authenticatedUserService',
        'userEmailService',
        '$translate',
        '$timeout'
    ]

    angular.module('koolikottApp').controller('emailValidationController', controller)
}
