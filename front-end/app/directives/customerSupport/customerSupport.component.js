'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.customerSupport = {}
            this.$scope.showCustomerSupportDialog = false
            this.$scope.customerSupportFirstStep = true

            this.getUserManualTitles()

        }

        setResponse() {
            this.$translate('CUSTOMER_SUPPORT_WILL_SEND_RESPONSE').then((value) => {
                this.$scope.finalresponse = (value.replace('${email}', this.$scope.customerSupport.email));
            })
        }

        openNewTab() {
            window.open(window.location.origin + '/usermanuals', '_blank')
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
        }

        close() {
            this.$scope.showCustomerSupportInput = false
            this.$scope.showCustomerSupportDialog = false
            this.$scope.userManualExists = false
            this.$scope.finalStep = false
            this.$scope.customerSupport = {}
        }

        handleSelectChange(subject) {

            if (subject === 'Muu') {
                this.$scope.showCustomerSupportInput = true
                this.$scope.userManualExists = false
            } else {
                this.$scope.showCustomerSupportInput = false
                this.$scope.userManualExists = true
            }
        }

        clickOutside() {
            if (!this.$scope.isSaving) {
                this.$scope.showCustomerSupportDialog = false
                this.$scope.customerSupport = {}
                this.$scope.userManualExists = false
            }
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
    ]

    component('dopCustomerSupport', {
        templateUrl: 'directives/customerSupport/customerSupport.html',
        controller
    })
}
