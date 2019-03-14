'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            /*if (this.$scope.userProfile) {
                this.$scope.userProfile.selectedSchools = [{
                    area: "Ida-Viru maakond",
                    ehisId: 18571,
                    id: 1,
                    name: "Ida- Virumaa Kutseharidusekeskus Narva filiaal"
                }, {
                    area: "Ida-Viru maakond",
                    ehisId: 18611,
                    id: 3,
                    name: "Ida- Virumaa Kutseharidusekeskus  Jõhvi filiaal"
                }]
            }*/
            this.$scope.userProfile = {}
            this.$scope.userProfile.schools = [{}]
            this.$scope.validEmail = VALID_EMAIL
            this.$scope.userProfile = {}
            this.$scope.roles = [{name: 'Student', checked: false},
                                {name: 'Teacher', checked: false},
                                {name: 'Parent', checked: false},
                                {name: 'Other', checked: false}]

            this.getEHISInstitutionAreas()
            angular.element(document.querySelector('#select-search')).on('keydown', (ev) => {
                ev.stopPropagation()
            })
            if (!this.$scope.user) {
                this.$scope.user = this.authenticatedUserService.getUser();
            }
            this.getUserEmail()

            /*this.$timeout(() => {
                $("#taxons").children().prop('ng-disabled',true)
            }, 10000)*/
        }

        clearSearchTerm() {
            this.$scope.searchTerm = ''

        }

        getEHISInstitutions() {
            this.$scope.institutions = []
            this.serverCallService.makeGet('rest/ehisInstitution/institutions')
                .then(response => {
                    this.$scope.institutions = response.data
                    // response.data.forEach(i => {
                    //     this.$scope.institutions.push(i.name)
                    // })
                })
        }

        handleAreaChange() {
            this.$scope.userProfile.selectedSchools = []
            this.serverCallService.makeGet('rest/ehisInstitution/institutions/?area=' + this.$scope.area)
                .then( response => {
                    this.$scope.institutions = response.data
                })
        }

        getEHISInstitutionAreas() {
            this.serverCallService.makeGet('rest/ehisInstitution/areas')
                .then(response => {
                    this.$scope.institutionAreas = response.data

                    // response.data.forEach(i => {
                    //     this.$scope.institutions.push(i.name)
                    // })
                })
        }

        isAdmin() {
            return this.authenticatedUserService.isAdmin()
        }

        getUserEmail() {
            this.userEmailService.getEmail()
                .then((response) => {
                    if (response.status === 200) {
                        this.$scope.user.email = response.data
                    }
                })
        }

        updateSelection(idx, roles) {
            angular.forEach(roles, (subscription, index) => {
                    if (idx != index)
                        subscription.checked = false;

                }
            );
        }

        updateUserProfile() {
            console.log('help im being clicked')
            this.$scope.userProfile.selectedSchools = [{
                area: "Ida-Viru maakond",
                ehisId: 18571,
                id: 1,
                name: "Ida- Virumaa Kutseharidusekeskus Narva filiaal"
            }, {
                area: "Ida-Viru maakond",
                ehisId: 18611,
                id: 3,
                name: "Ida- Virumaa Kutseharidusekeskus  Jõhvi filiaal"
            }]
        }

        onSelectTaxons(taxons) {
            this.$scope.filter.taxons = taxons
            this.$scope.clearFields = false

        }
    }

    controller.$inject = [
        '$scope',
        'serverCallService',
        'authenticatedUserService',
        'termsService',
        '$timeout',
        '$route',
        'userEmailService',
    ]
    angular.module('koolikottApp').controller('userProfileController', controller)
}
