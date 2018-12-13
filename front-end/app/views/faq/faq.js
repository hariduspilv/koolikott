'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.getFaqs()
            this.initNewFaq()
            this.$scope.editMode = false

        }

        initNewFaq() {
            this.$scope.newFaq = {
                questionEst: '',
                questionEng: '',
                questionRus: '',
                answerEst: '',
                answerEng: '',
                answerRus: '',
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

        editFaqPage() {
            this.$scope.editMode = true
        }

        createFaq() {
            this.$scope.faqs.push(this.$scope.newFaq)
        }

        removeFaq() {
            this.$scope.faqs.pop()
            this.initNewFaq()

        }

    }

    controller.$inject = [
        '$scope',
        'serverCallService',
        'authenticatedUserService',
        'faqService',
    ]
    angular.module('koolikottApp').controller('faqController', controller)
}
