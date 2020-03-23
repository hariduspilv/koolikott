'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.faqLanguages = ['ET', 'EN', 'RU']
            this.$scope.activeFaqLanguage = this.faqLanguages[0]
            this.getCurrentLanguage()
            this.$scope.isIE = super.isIE();

        }

        toggleFaqLanguageInputs(faq, lang) {
            faq.activeFaqLanguage = lang
        }

        save(faq) {

            this.$scope.isSaving = true
            if (faq.faqOrder === undefined) {
                faq.faqOrder = this.faqs.length
            }
            this.faqService.saveFaq(faq)
                .then(response => {
                    if (response.status === 200) {
                        this.createDialogOpen = false
                        this.$scope.isSaving = false
                        faq.edit = !faq.edit
                        this.getFaqs()
                        this.toastService.show('FAQ_SAVED')
                    } else {
                        this.$scope.isSaving = false
                        this.toastService.show('FAQ_SAVE_FAILED')
                    }
                })
            this.$scope.isSaving = false
        }

        saveAllFaqs(faqs) {
            this.$scope.isSaving = true
            this.$scope.saveSuccessful = true
            let i = 0;
            for (const faq of faqs) {
                faq.faqOrder = i
                this.faqService.saveFaq(faq)
                    .then(response => {
                        if (response.status !== 200) {
                            this.$scope.saveSuccessful = false
                        }
                    })
                i++;
                if (i === faqs.length) {
                    if (this.$scope.saveSuccessful) {
                        this.toastService.show('FAQ_ORDER_CHANGED')
                    } else {
                        this.toastService.show('FAQ_ORDER_CHANGE_FAIL')
                    }
                }
            }
            this.$scope.isSaving = false
        }

        getCurrentLanguage() {
            return this.translationService.getLanguage()
        }

        isFaqEditMode() {
            return this.editMode
        }

        isCreateDialogOpen() {
            return this.createDialogOpen
        }

        editFaq(faq) {
            this.createDialogOpen = !this.createDialogOpen
            faq.edit = !faq.edit;
            this.$scope.isEditFaq = true;
        }

        cancelEdit(faq) {
            if (faq.new) {
                this.removeFaq()
            } else {
                faq.edit = !faq.edit;
            }
            this.createDialogOpen = false
            this.getFaqs()
        }

        delete(faq) {
            this.dialogService.showDeleteConfirmationDialog(
                'ARE_YOU_SURE_DELETE',
                '',
                () => this.faqService.deleteFaq(faq)
                    .then(() => {
                        this.getFaqs()
                        this.toastService.show('FAQ_DELETED')
                        this.createDialogOpen = false
                    })
            )
        }

        isSubmitDisabled(faq) {
            return !(faq.titleEst && faq.contentEst &&
                faq.titleEng && faq.contentEng &&
                faq.titleRus && faq.contentRus)

        }

        move(idx, up = false) {
            this.faqs.splice(
                up ? idx - 1
                    : idx + 1,
                0,
                this.faqs.splice(idx, 1)[0]
            )
        }
    }

    controller.$inject = [
        '$scope',
        'dialogService',
        'serverCallService',
        'translationService',
        'searchService',
        'faqService',
        'toastService',
    ]
    component('dopFaqBlock', {
        bindings: {
            faqs: '<',
            editMode: '<',
            removeFaq: '&',
            getFaqs: '&',
            createDialogOpen: '='
        },
        templateUrl: 'directives/faqBlock/faqBlock.html',
        controller
    })
}
