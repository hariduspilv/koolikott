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
            this.$scope.$on('$destroy', () => document.removeEventListener('textInput', handler))

            this.unsubscribeRouteChangeSuccess = this.$rootScope.$on('$routeChangeSuccess', () =>
            {
                console.log('siin')
                this.$mdDialog.hide()
            })
            this.$scope.$watch(
                () => this.authenticatedUserService.isAuthenticated(),
                (newValue, oldValue) => {
                    if (this.$location.path() !== '/profile') {
                        newValue === true && this.$mdDialog.hide(),
                            false
                    }
                }
            );

            this.$scope.cancel = () => {
                this.$mdDialog.hide()
            }

            this.$scope.isValidateFromProfile = () => {
                return this.$location.path() === '/profile'
            }

            this.$scope.checkPin = () => {
                this.$scope.emailValidationForm.$setValidity('validationError', true)
                if (this.$scope.emailValidationForm.$valid && this.isNotEmpty()) {
                    this.$scope.isSending = true
                    let pin = this.$scope.firstNum + this.$scope.secondNum + this.$scope.thirdNum + this.$scope.fourthNum
                        this.verifyPin(pin);
                }
            }
            let handler = function(evt) {
                if (!NUMBERS.includes(evt.data))
                    evt.preventDefault();
            }

            document.addEventListener('textInput', handler)
        }

        verifyPin(pin) {
            this.userEmailService.validatePin(this.$rootScope.userFromAuthentication, pin, this.$rootScope.email, this.$location.path())
                .then(response => {
                    if (response.status === 200) {
                        this.$mdDialog.hide(true)
                        this.$scope.isSending = false
                    }
                }).catch(() => {
                this.$scope.emailValidationForm.$setValidity('validationError', false)
                this.setTouchedFalse()
                this.setFormToEmpty()
                this.$scope.isSending = false
                this.$timeout(() => {
                    angular.element('#email-firstNum').focus()
                }, 10);

            })
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
        '$timeout',
        '$location'
    ]

    angular.module('koolikottApp').controller('emailValidationController', controller)
}
