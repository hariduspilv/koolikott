'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.userProfile = {}
            this.$scope.userProfile.taxons = [{}]
            this.$scope.userProfile.institutions = [{}]
            this.$scope.validEmail = VALID_EMAIL
            this.$scope.userProfile.role = ''
            this.$scope.roles = [{name: 'STUDENT', checked: false},
                                {name: 'TEACHER', checked: false},
                                {name: 'PARENT', checked: false},
                                {name: 'OTHER', checked: false}]

            angular.element(document.querySelector('#select-search')).on('keydown', (ev) => {
                ev.stopPropagation()
            })
            if (!this.$scope.user) {
                this.$scope.user = this.authenticatedUserService.getUser();
                this.$rootScope.userFromAuthentication = this.$scope.user
                this.$scope.userProfile.user = this.$scope.user
            }
            this.getUserEmail()
            this.getUserProfile()

            this.$scope.location = this.$location
        }

        addNewTaxon() {
            this.$scope.userProfile.taxons.push(undefined)

        }

        addNewSchool() {
            this.$scope.userProfile.institutions.push(undefined)
        }

        removeInstitution(index) {
            this.$scope.userProfile.institutions.splice(index, 1);
        }

        clearSearchTerm() {
            this.$scope.searchTerm = ''

        }

        isAdmin() {
            return this.authenticatedUserService.isAdmin()
        }

        getUserEmail() {
            this.userEmailService.getEmail()
                .then((response) => {
                    if (response.status === 200) {
                        this.$scope.userEmail = response.data
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
            this.$rootScope.email = this.$scope.userEmail
            this.$scope.userProfileForm.email.$setValidity('validationError', true)
            this.setRole();
            this.$scope.userProfile.email = this.$scope.userEmail
            this.$scope.isSaving = true
            this.userEmailService.checkDuplicateEmailForProfile(this.$scope.userEmail)
                .then( response => {
                    if (response.status === 200) {
                        this.serverCallService.makePost('rest/userProfile', this.$scope.userProfile)
                            .then( response => {
                                this.$scope.isSaving = false
                                if (response.status === 201) {
                                    this.showEmailValidationModal()
                                } else if (response.status === 200) {
                                    this.toastService.show('USER_PROFILE_UPDATED')
                                }
                            })
                            .catch(( e ) => {
                                this.$scope.isSaving = false
                                    this.toastService.show('USER_PROFILE_UPDATE_FAILED')
                            })
                    }
                })
                .catch(( e ) => {
                    this.$scope.isSaving = false
                    this.$scope.userProfileForm.email.$setValidity('validationError', false)
                    this.toastService.show('USER_PROFILE_UPDATE_FAILED')
                })
        }

         showEmailValidationModal() {
            this.$mdDialog.show({
                templateUrl: 'views/emailValidation/emailValidationDialog.html',
                controller: 'emailValidationController',
                clickOutsideToClose: false,
                escapeToClose: false,
            }).then(res => {
                if (res)
                    this.toastService.show('USER_PROFILE_UPDATED')
            })
         }

        setRole() {
            this.$scope.roles.forEach(r => {
                if (r.checked) {
                    if (r.name === 'PARENT' || r.name === 'OTHER')
                        this.$scope.userProfile.institutions = [{}]
                    if (r.name === 'OTHER')
                        this.$scope.userProfile.role = r.name + ': ' + this.$scope.specifiedRole
                    else
                        this.$scope.userProfile.role = r.name
                }
            })
        }

        getUserProfile() {
            this.serverCallService.makeGet('rest/userProfile')
                .then( response => {
                    if (response.status === 200) {
                        this.$scope.userProfile = response.data
                        this.activateRole(this.$scope.userProfile.role)
                    }
                })
        }

        activateRole(role) {
            this.$scope.roles.forEach( r => {
                if (role.startsWith('OTHER') && (r.name === 'OTHER')) {
                    r.checked = true
                    this.$scope.specifiedRole = role.substring(7)
                }
                if (r.name === role)
                    r.checked = true
            })
        }

        onSelectTaxons(taxons) {
            this.$scope.filter.taxons = taxons
            this.$scope.clearFields = false

        }
        isEmpty (object) {
            return _.isEmpty(object)
        }

        isSubmitDisabled() {
            return !this.$scope.userProfileForm.$valid || this.$scope.isSaving
        }

        cancelProfileEdit() {
            window.location = window.location.origin
        }

        deleteTaxon(index) {
            this.$scope.userProfile.taxons.splice(index, 1);
        };
    }

    controller.$inject = [
        '$scope',
        '$rootScope',
        'serverCallService',
        'authenticatedUserService',
        'termsService',
        '$timeout',
        '$route',
        'userEmailService',
        'taxonService',
        '$location',
        'toastService',
        '$mdDialog'
    ]
    angular.module('koolikottApp').controller('userProfileController', controller)
}
