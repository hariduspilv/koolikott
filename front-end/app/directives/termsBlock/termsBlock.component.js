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

        toggleTermsLanguageInputs(term, lang) {
            term.activeTermLanguage = lang
        }

        save(term) {
            this.$scope.isSaving = true
            if (this.$scope.notifyOfGDPRUpdate || !this.blockExists()) {
                this.createAgreement(term)
                this.$scope.notifyOfGDPRUpdate = false
            } else {
                this.saveTerm(term)
            }
        }

        saveTerm(term) {
            term.type = 'USAGE'
            this.termsService.saveTerm(term)
                .then(response => {
                    if (response.status === 200) {
                        this.createDialogOpen = false
                        this.$scope.isSaving = false
                        term.edit = !term.edit
                        this.getTerms()
                        this.toastService.show('TERMS_SAVED')
                    } else {
                        this.$scope.isSaving = false
                        this.toastService.show('TERMS_SAVE_FAILED')
                    }
                })
            this.$scope.isSaving = false
        }

        getCurrentLanguage() {
            return this.translationService.getLanguage()
        }

        isTermsEditMode() {
            return this.editMode
        }

        blockExists() {
            return this.userTermsBlockExists
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
            this.getTerms()
        }

        isSubmitDisabled(term) {
            return !(term.titleEst && term.titleEng &&
                term.titleRus && (term.contentEst && term.contentEst !== '<br>') &&
                (term.contentEng && term.contentEng !== '<br>') && (term.contentRus && term.contentRus !== '<br>'))

        }

        createAgreement(term) {
            this.serverCallService
                .makePost('rest/admin/agreement', {type: 'USAGE'})
                .then((response) => {
                    if (response.status === 200) {
                        this.saveTerm(term)
                    }
                    else {
                        this.toastService.show('GDPR_NOTIFY_FAILED')
                    }
                }), () => {
                this.toastService.show('GDPR_NOTIFY_FAILED')
            }
        }
    }

    controller.$inject = [
        '$scope',
        'dialogService',
        'serverCallService',
        'translationService',
        'searchService',
        'termsService',
        'toastService',
    ]
    component('dopTermsBlock', {
        bindings: {
            terms: '<',
            editMode: '<',
            userTermsBlockExists: '<',
            removeTerm: '&',
            getTerms: '&',
            createDialogOpen: '='

        },
        templateUrl: '/directives/termsBlock/termsBlock.html',
        controller
    })
}
