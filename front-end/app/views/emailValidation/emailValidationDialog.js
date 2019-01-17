'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.setFormToEmpty()
            this.setResponse()
            this.$scope.emailValidationForm = {}

            this.unsubscribeRouteChangeSuccess = this.$rootScope.$on('$routeChangeSuccess', () => this.$mdDialog.hide())
            this.$scope.$watch(
                () => this.authenticatedUserService.isAuthenticated(),
                (newValue, oldValue) => newValue === true && this.$mdDialog.hide(),
                false
            );

            this.$scope.$watchCollection('emailValidationForm', () => {
                if (this.$scope.emailValidationForm.$valid && this.isNotEmpty()) {
                    this.$scope.isSending = true
                    this.$scope.emailValidationForm.$setValidity('validationError', true)
                    let pin = this.$scope.firstNum + this.$scope.secondNum + this.$scope.thirdNum + this.$scope.fourthNum
                    this.userEmailService.validatePin(this.$rootScope.userFromAuthentication, pin)
                        .then(response => {
                            if (response.status === 200) {
                                this.$mdDialog.hide(true)
                                this.$scope.isSending = false
                            }
                        }).catch( response => {
                            this.$scope.emailValidationForm.$setValidity('validationError', false)
                            this.setFormToEmpty()
                            this.$scope.isSending = false
                    })
                }
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
        $onDestroy() {
            if (typeof this.unsubscribeRouteChangeSuccess === 'function')
                this.unsubscribeRouteChangeSuccess()
        }
    }
    controller.$inject = [
        '$scope',
        '$rootScope',
        '$mdDialog',
        'authenticatedUserService',
        'userEmailService',
        '$translate'
    ]

    angular.module('koolikottApp').controller('emailValidationController', controller)
}
