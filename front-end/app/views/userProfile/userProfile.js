'use strict';

{
    class controller extends Controller {
        constructor(...args) {
            super(...args);

            this.$scope.userProfile = {};
            this.$scope.userProfile.taxons = [{}];
            this.$scope.userProfile.institutions = [{}];
            this.$scope.validEmail = VALID_EMAIL;
            this.$scope.userProfile.role = '';
            this.$scope.roles = [{name: 'STUDENT', checked: false},
                                {name: 'TEACHER', checked: false},
                                {name: 'PARENT', checked: false},
                                {name: 'OTHER', checked: false}];

            if (!this.$scope.user) {
                this.$scope.user = this.authenticatedUserService.getUser();
                this.$rootScope.userFromAuthentication = this.$scope.user;
            }

            this.getUserEmail();
            this.getUserProfile();

            this.$scope.location = this.$location;
        }

        isModerator() {
            return this.authenticatedUserService.isModerator()
        }

        addNewTaxon() {
            this.$scope.userProfile.taxons.push(undefined)

        }

        addNewSchool() {
            this.$scope.userProfile.institutions.push(undefined)
        }

        removeInstitution(index) {
            if (this.$scope.userProfile.institutions.length === 1 && index === 0 )
                this.$scope.userProfile.institutions = [{}]
            else
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
                        this.$scope.userEmail = response.data.email
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
            this.$scope.userProfile.user = this.$scope.user
            this.$rootScope.email = this.$scope.userEmail;
            this.$scope.userProfileForm.email.$setValidity('validationError', true);
            this.setRole();
            this.$scope.userProfile.email = this.$scope.userEmail;
            this.$scope.isSaving = true;
            this.userEmailService.checkDuplicateEmailForProfile(this.$scope.userEmail)
                .then(response => {
                    if (response.status === 200) {
                        this.showEmailValidationModal();
                    }
                })
                .catch(() => {
                    this.$scope.isSaving = false;
                    this.$scope.userProfileForm.email.$setValidity('validationError', false);
                    this.toastService.show('USER_PROFILE_UPDATE_FAILED')
                });
        }

        saveUserProfile() {
            this.serverCallService.makePost('rest/userProfile', this.$scope.userProfile)
                .then(response => {
                    this.$scope.isSaving = false;
                    if (response.status === 201) {
                        this.toastService.show('USER_PROFILE_UPDATED')
                    } else if (response.status === 200) {
                        this.toastService.show('USER_PROFILE_UPDATED')
                    }
                })
                .catch((e) => {
                    this.$scope.isSaving = false;
                    console.log(e)
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
                    this.saveUserProfile();
                else {
                    this.$scope.isSaving = false;
                    this.toastService.show('USER_PROFILE_UPDATE_FAILED')
                }
            })
                .catch((e) => {
                    console.log(e);
                })
        }

        setRole() {
            if (_.isEmpty(this.$scope.roles.filter(role => (role.checked)))) {
                this.$scope.userProfile.role = undefined
                this.$scope.userProfile.institutions = [{}]
                this.$scope.userProfile.customRole = ''
            } else {
                this.$scope.roles.forEach(r => {
                    if (r.checked) {
                        if (r.name === 'PARENT' || r.name === 'OTHER') {
                            this.$scope.userProfile.institutions = [{}];
                        } else {
                            this.$scope.userProfile.customRole = ''
                        }
                        this.$scope.userProfile.role = r.name
                    }
                })
            }
        }

        getUserProfile() {
            this.serverCallService.makeGet('rest/userProfile')
                .then( response => {
                    if (response.status === 200) {
                        this.$scope.userProfile = response.data;
                        this.$scope.userEmail = response.data.email;
                        if (!!this.$scope.userProfile.role)
                            this.activateRole(this.$scope.userProfile.role);
                        if (_.isEmpty(this.$scope.userProfile.institutions[0]))
                            this.$scope.userProfile.institutions.push({});
                        if (_.isEmpty(this.$scope.userProfile.taxons[0]))
                            this.$scope.userProfile.taxons.push({})
                    }
                })
                .catch(() => {
                        this.activateRole(this.$scope.userProfile.role);
                        this.$scope.userProfile.institutions.push({});
                        this.$scope.userProfile.taxons.push({})
                })
        }

        activateRole(role) {
            this.$scope.roles.forEach( r => {
                if (role.startsWith('OTHER') && (r.name === 'OTHER')) {
                    r.checked = true;
                    if (!this.$scope.userProfile.customRole)
                        this.$scope.userProfile.customRole = ''
                }
                if (r.name === role)
                    r.checked = true
            })
        }

        onSelectTaxons(taxons) {
            this.$scope.filter.taxons = taxons;
            this.$scope.clearFields = false

        }
        isEmpty (object) {
            return _.isEmpty(object)
        }

        isSubmitDisabled() {
            return !this.$scope.userProfileForm.$valid || this.$scope.isSaving;
        }

        cancelProfileEdit() {
            window.location = window.location.origin
        }

        deleteTaxon(index) {
            if (this.$scope.userProfile.taxons.length === 1 && index === 0 )
                this.$scope.userProfile.taxons = [{}]
            else
                this.$scope.userProfile.taxons.splice(index, 1);
        }

        resetDuplicateEmailError() {
            this.$scope.userProfileForm.email.$setValidity('validationError', true);
        }
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
    ];
    angular.module('koolikottApp').controller('userProfileController', controller)
}
