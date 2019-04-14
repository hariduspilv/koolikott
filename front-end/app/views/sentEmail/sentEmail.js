'use strict';

{
    class controller extends Controller {
        constructor(...args) {
            super(...args);

            if (!this.$scope.user) {
                this.$scope.user = this.authenticatedUserService.getUser();
                this.$rootScope.userFromAuthentication = this.$scope.user;
            }

            this.getUserEmail();
            this.getUserProfile();

            this.$scope.location = this.$location
        }

        isModerator() {
            return this.authenticatedUserService.isModerator()
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
            this.$scope.userProfile.user = this.$scope.user
            this.$rootScope.email = this.$scope.userEmail;
            this.$scope.userProfileForm.email.$setValidity('validationError', true);
            this.setRole();
            this.$scope.userProfile.email = this.$scope.userEmail;
            this.$scope.isSaving = true;
            this.userEmailService.checkDuplicateEmailForProfile(this.$scope.userEmail)
                .then( response => {
                    if (response.status === 200) {
                        this.serverCallService.makePost('rest/userProfile', this.$scope.userProfile)
                            .then( response => {
                                this.$scope.isSaving = false;
                                if (response.status === 201) {
                                    this.showEmailValidationModal()
                                } else if (response.status === 200) {
                                    this.toastService.show('USER_PROFILE_UPDATED')
                                }
                            })
                            .catch( (e) => {
                                this.$scope.isSaving = false;
                                console.log(e)
                                this.toastService.show('USER_PROFILE_UPDATE_FAILED')
                            })
                    }
                })
                .catch( () => {
                    this.$scope.isSaving = false;
                    this.$scope.userProfileForm.email.$setValidity('validationError', false);
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



        getUserProfile() {
            this.serverCallService.makeGet('rest/userProfile')
                .then( response => {
                    if (response.status === 200) {
                        this.$scope.userProfile = response.data;
                        if (!!this.$scope.userProfile.role)
                            this.activateRole(this.$scope.userProfile.role);
                        if (_.isEmpty(this.$scope.userProfile.institutions[0]))
                            this.$scope.userProfile.institutions.push({});
                        if (_.isEmpty(this.$scope.userProfile.taxons[0]))
                            this.$scope.userProfile.taxons.push({})

                    }
                })
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
    angular.module('koolikottApp').controller('sentEmailController', controller)
}
