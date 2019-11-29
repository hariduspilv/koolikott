'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.getLicenses()
            this.initNewLicence()
            this.$scope.editMode = false
            this.$translate('LICENSES_HEADER').then((translation) => this.$rootScope.tabTitle = translation);
        }

        initNewLicence() {
            this.$scope.newLicence = {
                titleEst: '',
                titleEng: '',
                titleRus: '',
                contentEst: '',
                contentEng: '',
                contentRus: '',
                licenceLanguages: ['ET', 'EN', 'RU'],
                edit: true,
                new: true
            }
            this.$scope.newLicence.activeLicenceLanguage = this.$scope.newLicence.licenceLanguages[0]
        }

        isAdmin() {
            return this.authenticatedUserService.isAdmin()
        }

        getLicenses() {
            this.licensesService.getLicenses()
                .then(({data}) => {
                    for (const licence of data) {
                        licence.licenceLanguages = ['ET', 'EN', 'RU'];
                        licence.activeLicenceLanguage = licence.licenceLanguages[0];
                    }
                    this.$scope.licenses = data
                })
        }

        editLicensesPage() {
            this.$scope.editMode = true
        }

        createLicence() {
            this.$scope.licenses.push(this.$scope.newLicence)
            this.initNewLicence()
            this.createDialogOpen = true
            this.$timeout(() => {
                window.scrollTo(0, document.body.scrollHeight);
            }, 50)
        }

        removeLicence() {
            this.$scope.licenses.pop()
            this.initNewLicence()
        }

    }

    controller.$inject = [
        '$scope',
        '$rootScope',
        '$translate',
        'authenticatedUserService',
        'licensesService',
        '$timeout',
        '$route'
    ]
    angular.module('koolikottApp').controller('licensesController', controller)
}
