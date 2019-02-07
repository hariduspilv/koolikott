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

        isLangFilled(lang, term) {
            let isFilled = false;

            if ((lang === 'ET') && !!(term.titleEst && term.contentEst))
                isFilled = true

            if ((lang === 'EN') && !!(term.titleEng && term.contentEng))
                isFilled = true

            if ((lang === 'RU') && !!(term.titleRus && term.contentRus))
                isFilled = true

            return isFilled;
        }

        save(term) {
            this.$scope.isSaving = true

            if (this.$scope.notifyOfGDPRUpdate) {
                this.createAgreement()
                this.$scope.notifyOfGDPRUpdate = false
            }

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

        delete(term) {
            this.dialogService.showDeleteConfirmationDialog(
                'ARE_YOU_SURE_DELETE',
                '',
                () => this.termsService.deleteTerm(term)
                    .then(() => {
                        this.getTerms()
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

        createAgreement() {
            this.serverCallService
                .makePost('rest/admin/agreement', {url: '/terms', version: 1, validFrom: new Date})
                .then((response) => {
                    if (response.status === 200) {
                        console.log('agreement added')
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
            removeTerm: '&',
            getTerms: '&',
            createDialogOpen: '='

        },
        templateUrl: 'directives/termsBlock/termsBlock.html',
        controller
    })
}
