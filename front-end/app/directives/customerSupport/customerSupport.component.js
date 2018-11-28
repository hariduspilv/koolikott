'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.customerSupport = {}
            this.$scope.showCustomerSupportDiv = false
            this.$scope.customerSupportFirstStep = true

            this.getUserManualTitles()
            // this.getLoggedInUserData()

            $(document).click(e => {

                if ($(e.target).closest('#customerSupportDiv').length != 0 || $(e.target).closest('#customerSupportButton').length != 0) return false;
                if (!this.$scope.isSaving)
                    this.$scope.showCustomerSupportDiv = false
            });
        }


        getUserManualTitles() {
            this.userManualsAdminService.getUserManuals()
                .then(response => {
                    this.$scope.titles = response.data.map(um => {
                        return um.title
                    })
                    this.$scope.titles.push('Muu')
                    console.log(this.$scope.title)
                })
        }

        saveCustomerSupportRequest() {

            this.$scope.isSaving = true

            this.serverCallService.makePost('/rest/admin/customerSupport', this.$scope.customerSupport)
                .then(response => {
                        if (response.status === 200) {
                            this.$scope.isSaving = false
                            this.$scope.showCustomerSupportInput = false
                            this.$scope.finalStep = true

                            this.toastService.show('CUSTOMER_SUPPORT_REQUEST_SENT')
                        }
                        else {
                            this.$scope.showCustomerSupportDiv = false
                            this.toastService.show('CUSTOMER_SUPPORT_REQUEST_FAILED')
                        }
                    }, () =>
                        this.$scope.isSaving = false
                )
        }

        getLoggedInUserData() {
            // const { name, surname } = this.authenticatedUserService.getUser()
            if (this.authenticatedUserService.getUser()) {
                const poop = this.authenticatedUserService.getUser()
                const { name, surname } = this.authenticatedUserService.getUser()
                console.log(name + ' ' + surname)
                this.$scope.customerSupport.name = name + ' ' + surname
            }
        }

        showCustomerSupportInput() {
            this.$scope.showCustomerSupportInput = true
            this.$scope.userManualExists = false
        }

        toggleCustomerSupportForm() {
            this.getLoggedInUserData()
            this.$scope.showCustomerSupportDiv = true
        }

        isSendDisabled() {
            return !(this.$scope.customerSupport.name && this.$scope.customerSupport.email && this.$scope.customerSupport.subject && this.$scope.customerSupport.message)
        }

        back() {
            this.$scope.showCustomerSupportInput = false
        }

        close() {
            this.$scope.showCustomerSupportInput = false
            this.$scope.showCustomerSupportDiv = false
            this.$scope.userManualExists = false
            this.$scope.finalStep = false
            this.$scope.customerSupport = {}
        }

        handleSelectChange(subject) {

            console.log(subject)
            if (subject === 'Muu') {
                this.$scope.showCustomerSupportInput = true
                this.$scope.userManualExists = false
            } else {
                this.$scope.showCustomerSupportInput = false
                this.$scope.userManualExists = true
            }
        }

    }

    controller.$inject = [
        'userManualsAdminService',
        'serverCallService',
        'toastService',
        '$scope',
        'authenticatedUserService',
    ]

    component('dopCustomerSupport', {
        templateUrl: 'directives/customerSupport/customerSupport.html',
        controller
    })
}
