'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.$scope.data = {}
            this.$scope.landingPage = {}

            this.getAgreements()

            this.$scope.addAgreement = this.addAgreement.bind(this)
        }

        getAgreements() {
            this.serverCallService
                .makeGet('rest/admin/agreement')
                .then(res => {
                    this.$scope.alldata = res.data
                    this.$scope.page = 1
                    this.$scope.numPages = Math.ceil(this.$scope.alldata.length / this.$scope.perPage)
                    this.paginate(this.$scope.page, this.$scope.perPage)
                    this.sort(this.$scope.sortBy)
                })
        }

        addAgreement() {
            this.$scope.newAgreement.$error = false;
            this.serverCallService
                .makePost('rest/admin/agreement/validate', this.$scope.newAgreement)
                .then((response) => {
                    if (!(response.status === 200 && response.data)) {
                        this.$scope.newAgreement.$error = true;
                    } else {
                        this.serverCallService
                            .makePost('rest/admin/agreement', this.$scope.newAgreement)
                            .then((response) => {
                                if (response.status === 200) {
                                    this.getAgreements();
                                    this.$scope.newAgreement = {}
                                    this.$scope.addNewRow = false;
                                }
                            })
                    }
                })
        }
    }

    controller.$inject = [
        '$scope',
        '$translate',
        'serverCallService',
        'sortService',
        'dialogService',
        '$window'
    ]
    angular.module('koolikottApp').controller('landingPageAdminController', controller)
}
