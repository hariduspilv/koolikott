'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.$scope.data = {}
            this.$scope.newAgreement = {}
            this.$scope.addNewRow = false;
            this.$scope.isInfoTextOpen = false

            this.getAgreements()

            this.$scope.sort = this.sort.bind(this)
            this.$scope.paginate = this.paginate.bind(this)
            this.$scope.confirmMaterialDeletion = this.confirmMaterialDeletion.bind(this)
            this.$scope.addAgreement = this.addAgreement.bind(this)
            this.$scope.toggleNewRow = this.toggleNewRow.bind(this)
            this.$scope.minDate = new Date()
            this.$scope.perPage = 20
            this.$scope.page = 1
            this.$scope.numPages = 1

            // Set the info text height in pixels for css-animatable collapse
            this.setInfoTextHeight = this.setInfoTextHeight.bind(this)
            window.addEventListener('resize', this.setInfoTextHeight)
            this.$scope.$on('$destroy', () => window.removeEventListener('resize', this.setInfoTextHeight))
            setTimeout(this.setInfoTextHeight)
        }

        toggleNewRow() {
            this.$scope.addNewRow = !this.$scope.addNewRow;
            this.$scope.newAgreement.$error = false;
        }

        confirmMaterialDeletion(agreement) {
            this.dialogService.showConfirmationDialog(
                'GDPR_DELETE_DIALOG_TITLE',
                'GDPR_DELETE_DIALOG_CONTENT',
                'ALERT_CONFIRM_POSITIVE',
                'ALERT_CONFIRM_NEGATIVE',
                () => {
                    this.deleteAgreement(agreement)
                });
        };

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

        deleteAgreement(agreement) {
            this.serverCallService
                .makePost('rest/admin/agreement/delete', agreement)
                .then(() => {
                    this.getAgreements();
                })
        }

        setInfoTextHeight() {
            requestAnimationFrame(() => {
                const infoText = document.querySelector('.statistics-info-text')
                infoText.style.height = infoText.scrollHeight + 'px'
            })
        }

        sort(order) {
            this.$scope.sortBy = order
            this.sortService.orderItems(this.$scope.alldata, order)
            this.paginate(this.$scope.page, this.$scope.perPage)
        }

        toggleInfoText() {
            this.setInfoTextHeight()
            this.$scope.isInfoTextOpen = !this.$scope.isInfoTextOpen
        }

        paginate(page, perPage) {
            const startIdx = (page - 1) * perPage
            this.$scope.page = page
            this.$scope.data = this.$scope.alldata.slice(startIdx, startIdx + perPage)
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
    angular.module('koolikottApp').controller('gdprController', controller)
}
