'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.getFaqs()
            this.initNewFaq()
            this.$scope.editMode = false
            this.$translate('KKK_TAB').then((translation) => this.$rootScope.tabTitle = translation);
        }

        initNewFaq() {
            this.$scope.newFaq = {
                titleEst: '',
                titleEng: '',
                titleRus: '',
                contentEst: '',
                contentEng: '',
                contentRus: '',
                faqLanguages: ['ET', 'EN', 'RU'],
                edit: true,
                new: true

            }
            this.$scope.newFaq.activeFaqLanguage = this.$scope.newFaq.faqLanguages[0]
        }

        isAdmin() {
            return this.authenticatedUserService.isAdmin()
        }

        getFaqs() {
            this.faqService.getFaq()
                .then(({data}) => {
                    for (const faq of data) {
                        faq.faqLanguages = ['ET', 'EN', 'RU'];
                        faq.activeFaqLanguage = faq.faqLanguages[0];
                    }
                    this.$scope.faqs = data
                })
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

        editFaqPage() {
            this.$scope.editMode = true
        }

        createFaq() {
            this.$scope.faqs.push(this.$scope.newFaq)
            this.initNewFaq()
            this.createDialogOpen = true

            this.$scope.unwatch = this.$scope.$watch(() => document.getElementById('faq-save'), (selectedValue) => {
                if (selectedValue) {
                    window.scrollTo(0,document.body.scrollHeight);
                    this.$scope.unwatch()
                }
            })
        }

        removeFaq() {
            this.$scope.faqs.pop()
            this.initNewFaq()

        }

    }

    controller.$inject = [
        '$scope',
        '$rootScope',
        '$translate',
        'serverCallService',
        'authenticatedUserService',
        'faqService',
        'toastService',
    ]
    angular.module('koolikottApp').controller('faqController', controller)
}
