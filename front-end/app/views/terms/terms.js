'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.getTerms()
            this.initNewTerm()
            this.$scope.editMode = false
        }

        initNewTerm() {
            this.$scope.newTerm = {
                titleEst: '',
                titleEng: '',
                titleRus: '',
                contentEst: '',
                contentEng: '',
                contentRus: '',
                termLanguages: ['ET', 'EN', 'RU'],
                edit: true,
                new: true

            }
            this.$scope.newTerm.activeTermLanguage = this.$scope.newTerm.termLanguages[0]
        }

        isAdmin() {
            return this.authenticatedUserService.isAdmin()
        }

        getTerms() {
            this.termsService.getTerm()
                .then(({data}) => {
                    for (const term of data) {
                        term.termLanguages = ['ET', 'EN', 'RU'];
                        term.activeTermLanguage = term.termLanguages[0];
                    }
                    this.$scope.terms = data
                })
        }

        editTermsPage() {
            this.$scope.editMode = true
        }

        createTerm() {
            this.$scope.terms.push(this.$scope.newTerm)
            this.initNewTerm()
            this.createDialogOpen = true
        }

        removeTerm() {
            this.$scope.terms.pop()
            this.initNewTerm()

        }

    }

    controller.$inject = [
        '$scope',
        'serverCallService',
        'authenticatedUserService',
        'termsService',
    ]
    angular.module('koolikottApp').controller('termsController', controller)
}
