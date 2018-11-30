'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.customerSupport = {}
            this.$scope.showCustomerSupportDialog = false
            this.$scope.showUserManualsHelped = false
            this.$scope.backClickedWhileOther = false

            this.getUserManualTitles()
            this.$rootScope.$on('logout:success', this.clearData.bind(this));

            this.$scope.$watch('customerSupport.subject', (selectedValue, previousValue) => {
                if (selectedValue && selectedValue !== previousValue) {
                    this.handleSelectChange(this.$scope.customerSupport.subject)
                    this.$scope.showUserManualsHelped = false
                }
            })
        }

        clearData() {
            this.$scope.customerSupport = {}
            this.$scope.showCustomerSupportInput = false
        }

        setResponse() {
            this.$translate('CUSTOMER_SUPPORT_WILL_SEND_RESPONSE').then((value) => {
                this.$scope.finalresponse = (value.replace('${email}', this.$scope.customerSupport.email));
            })
        }

        openNewTab() {
            this.$window.open(window.location.origin + '/usermanuals', '_blank')
            this.$scope.showUserManualsHelped = true
            this.$scope.allowDialogClose = false
        }

        getUserManualTitles() {
            this.userManualsAdminService.getUserManuals()
                .then(response => {
                    this.$scope.titles = response.data.map(um => {
                        return um.title
                    })
                    this.$scope.titles.push('Muu')
                })
        }

        saveCustomerSupportRequest() {

            this.$scope.isSaving = true
            this.setResponse()

            this.serverCallService.makePost('/rest/admin/customerSupport', this.$scope.customerSupport)
                .then(response => {
                        this.$scope.isSaving = false
                        if (response.status === 200) {
                            this.$scope.showCustomerSupportInput = false
                            this.$scope.finalStep = true
                        }
                        else {
                            this.$scope.showCustomerSupportDialog = false
                        }
                    }, () =>
                        this.$scope.isSaving = false
                )
        }

        getLoggedInUserData() {
            // const { name, surname } = this.authenticatedUserService.getUser()
            if (this.authenticatedUserService.getUser()) {
                const {name, surname} = this.authenticatedUserService.getUser()
                this.$scope.customerSupport.name = name + ' ' + surname
            }
        }

        showCustomerSupportInput() {
            this.$scope.showCustomerSupportInput = true
            this.$scope.userManualExists = false
            this.$scope.showUserManualsHelped = false
        }

        toggleCustomerSupportDialog() {
            this.getLoggedInUserData()
            this.$scope.showCustomerSupportDialog = true
        }

        isSendDisabled() {
            return !(this.$scope.customerSupport.name && this.$scope.customerSupport.email && this.$scope.customerSupport.subject && this.$scope.customerSupport.message)
        }

        back() {
            this.$scope.showCustomerSupportInput = false
            if (this.$scope.customerSupport.subject === 'Muu')
                this.$scope.backClickedWhileOther = true
            else
                this.$scope.backClickedWhileOther = false

            this.handleSelectChange(this.$scope.customerSupport.subject)
        }

        close() {
            this.$scope.showCustomerSupportInput = false
            this.$scope.showCustomerSupportDialog = false
            this.$scope.userManualExists = false
            this.$scope.finalStep = false
            this.$scope.showUserManualsHelped = false
            this.$scope.customerSupport = {}
        }

        handleSelectChange(subject) {

            if (subject === 'Muu' && !this.$scope.backClickedWhileOther) {
                this.$scope.showCustomerSupportInput = true
                this.$scope.userManualExists = false
            } else if ((subject === 'Muu' && this.$scope.backClickedWhileOther)) {
                this.$scope.showCustomerSupportInput = false
                this.$scope.userManualExists = false
            } else {
                this.$scope.showCustomerSupportInput = false
                this.$scope.userManualExists = true
            }
        }

        clickOutside() {
            if (!this.$scope.isSaving && this.$scope.allowDialogClose) {
                this.$scope.showCustomerSupportDialog = false
            }
            this.$scope.allowDialogClose = true
        }
    }
    controller.$inject = [
        'userManualsAdminService',
        'serverCallService',
        'toastService',
        '$scope',
        'authenticatedUserService',
        '$location',
        '$translate',
        '$window',
        '$rootScope',
    ]
    component('dopCustomerSupport', {
        templateUrl: 'directives/customerSupport/customerSupport.html',
        controller
    })
}
