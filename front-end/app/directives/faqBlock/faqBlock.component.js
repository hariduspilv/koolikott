'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.faqLanguages = ['ET', 'EN', 'RU']
            this.$scope.activeFaqLanguage = this.faqLanguages[0]
            this.getCurrentLanguage()
            this.isEditMode = true

        }

        toggleFaqLanguageInputs(faq, lang) {
            faq.activeFaqLanguage = lang
        }

        isLangFilled(lang, faq) {
            let isFilled = false;

            if (lang === 'ET' && faq.answerEst !== '' && faq.questionEst !== '')
                isFilled = true

            if (lang === 'EN' && faq.answerEng !== '' && faq.questionEng !== '')
                isFilled = true

            if (lang === 'RU' && faq.answerRus !== '' && faq.questionRus !== '')
                isFilled = true

            return isFilled;
        }

        save(faq) {

            this.$scope.isSaving = true

            debugger
            this.faqService.saveFaq(faq)
                .then(response => {
                    if (response.status === 200) {
                        this.$scope.isSaving = false
                        faq.edit = !faq.edit
                        this.getFaqs()
                        debugger
                    } else {
                        this.$scope.isSaving = false
                    }
                })
            this.$scope.isSaving = false
        }

        getCurrentLanguage() {
            return this.translationService.getLanguage()
            console.log(lang)
        }


        editFaq(faq) {
            faq.edit = !faq.edit;
        }

        cancelEdit(faq) {
            if (faq.new) {
                this.deleteFaq()
            } else {
                faq.edit = !faq.edit;
            }
        }

        delete(faq) {
            this.faqService.deleteFaq(faq)
                .then(() => {
                    this.getFaqs()
                })
        }

        isSubmitDisabled(faq) {
            return (faq.questionEst === '' && faq.answerEst === '' &&
                faq.questionEng === '' && faq.answerEng === '' &&
                faq.questionRus === '' && faq.answerRus === '')

        }
    }

    controller.$inject = [
        '$scope',
        '$rootScope',
        '$translate',
        '$mdDialog',
        'dialogService',
        'serverCallService',
        'translationService',
        'searchService',
        'faqService',
    ]
    component('dopFaqBlock', {
        bindings: {
            faqs: '<',
            isEditMode: '<',
            deleteFaq: '&',
            getFaqs: '&'
        },
        templateUrl: 'directives/faqBlock/faqBlock.html',
        controller
    })
}
