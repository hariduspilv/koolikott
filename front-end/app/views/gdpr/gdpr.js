'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.$scope.data = {}
            this.$scope.newAgreement = {}
            this.$scope.addNewRow = false;

            this.getAgreements()

            this.$scope.isInfoTextOpen = false
            this.$scope.sort = this.sort.bind(this)
            this.$scope.paginate = this.paginate.bind(this)
            this.$scope.confirmMaterialDeletion = this.confirmMaterialDeletion.bind(this)
            this.$scope.addAgreement = this.addAgreement.bind(this)
            this.$scope.toggleNewRow = this.toggleNewRow.bind(this)
            this.$scope.moveToPage = this.moveToPage.bind(this)
            this.$scope.maxDate = new Date()
            this.$scope.perPage = 100
            this.$scope.page = 1
            this.$scope.numPages = 1

            // Set the info text height in pixels for css-animatable collapse
            this.setInfoTextHeight = this.setInfoTextHeight.bind(this)
            window.addEventListener('resize', this.setInfoTextHeight)
            this.$scope.$on('$destroy', () => window.removeEventListener('resize', this.setInfoTextHeight))
            setTimeout(this.setInfoTextHeight)
        }

        moveToPage(url) {
            this.$window.open(url);
        }

        toggleNewRow() {
            this.$scope.addNewRow = !this.$scope.addNewRow;
        }

        confirmMaterialDeletion(agreement) {
            this.dialogService.showConfirmationDialog(
                'MATERIAL_CONFIRM_DELETE_DIALOG_TITLE',
                'MATERIAL_CONFIRM_DELETE_DIALOG_CONTENT',
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
                    return this.$scope.data = res.data;
                })
        }

        addAgreement() {
            this.serverCallService
                .makePost('rest/admin/agreement', this.$scope.newAgreement)
                .then(() => {
                    this.getAgreements();
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
            this.sortService.orderItems(this.$scope.allRows, order)
            this.paginate(this.$scope.page, this.$scope.perPage)
        }
        toggleInfoText() {
            this.setInfoTextHeight()
            this.$scope.isInfoTextOpen = !this.$scope.isInfoTextOpen
        }
        paginate(page, perPage) {
            const startIdx = (page - 1) * perPage
            this.$scope.page = page
            this.$scope.data.rows = this.$scope.allRows.slice(startIdx, startIdx + perPage)
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
