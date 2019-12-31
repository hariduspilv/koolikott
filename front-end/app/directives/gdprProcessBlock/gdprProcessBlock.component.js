'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.termLanguages = ['ET', 'EN', 'RU']
            this.$scope.activeTermLanguages = this.termLanguages[0]
            this.getCurrentLanguage()
            this.$scope.notifyOfGDPRUpdate = false
        }

        notifyOfGDPRUpdate() {
            this.$scope.notifyOfGDPRUpdate = !this.$scope.notifyOfGDPRUpdate
        }

        toggleGdprProcessLanguageInputs(term, lang) {
            term.activeTermLanguage = lang
        }

        save(term) {
            this.$scope.isSaving = true

            if (this.$scope.notifyOfGDPRUpdate) {
                this.createAgreement(term)
                this.$scope.notifyOfGDPRUpdate = false
            } else {
                this.saveTerm(term)
            }
        }

        getCurrentLanguage() {
            return this.translationService.getLanguage()
        }

        isTermsEditMode() {
            return this.editMode
        }

        blockExists() {
            return this.gdprTermsBlockExists
        }

        isCreateDialogOpen() {
            return this.createDialogOpen
        }

        editTerm(term) {
            this.createDialogOpen = !this.createDialogOpen
            term.edit = !term.edit;
        }

        cancelEdit(term) {
            this.dialogService.showCancelConfirmationDialog(
                'ARE_YOU_SURE_CANCEL',
                '',
                () => this.cancelConfirmed(term))
        }

        cancelConfirmed(term) {
            if (term.new) {
                this.removeTerm()
            } else {
                term.edit = !term.edit;
            }
            this.createDialogOpen = false
            this.$scope.notifyOfGDPRUpdate = false
            this.getGdprTerms()
        }

        delete(term) {
            this.dialogService.showDeleteConfirmationDialog(
                'ARE_YOU_SURE_DELETE',
                '',
                () => this.termsService.deleteTerm(term)
                    .then(() => {
                        this.getGdprTerms()
                        this.toastService.show('TERM_DELETED')
                        this.createDialogOpen = false
                    })
            )
        }

        isSubmitDisabled(term) {
            return !(term.titleEst && term.titleEng &&
                term.titleRus && (term.contentEst && term.contentEst !== '<br>') &&
                (term.contentEng && term.contentEng !== '<br>') && (term.contentRus && term.contentRus !== '<br>'))

        }

        saveTerm(term) {
            term.type = 'GDPR'
            this.termsService.saveTerm(term)
                .then(response => {
                    if (response.status === 200) {
                        this.createDialogOpen = false
                        this.$scope.isSaving = false
                        term.edit = !term.edit
                        this.getGdprTerms()
                        this.toastService.show('TERMS_SAVED')
                    } else {
                        this.$scope.isSaving = false
                        this.toastService.show('TERMS_SAVE_FAILED')
                    }
                })
            this.$scope.isSaving = false
        }

        createAgreement(term) {
            this.serverCallService
                .makePost('rest/admin/agreement', {type: 'GDPR'})
                .then((response) => {
                    if (response.status === 200) {
                        this.saveTerm(term)
                        console.log('agreement added')
                    }
                    else {
                        this.toastService.show('GDPR_NOTIFY_FAILED')
                    }
                }), (() => {
                this.toastService.show('GDPR_NOTIFY_FAILED')
            });
        }
    }

    controller.$inject = [
        '$scope',
        'dialogService',
        'serverCallService',
        'translationService',
        'termsService',
        'toastService',
    ]
    component('dopGdprProcessBlock', {
        bindings: {
            terms: '<',
            editMode: '<',
            gdprTermsBlockExists: '<',
            removeTerm: '&',
            getGdprTerms: '&',
            createDialogOpen: '='

        },
        templateUrl: 'directives/gdprProcessBlock/gdprProcessBlock.html',
        controller
    })
}
