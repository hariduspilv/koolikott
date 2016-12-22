'use strict'

angular.module('koolikottApp')
.controller('editUserController',
[
    '$scope', '$mdDialog', 'serverCallService', 'translationService', 'toastService', '$rootScope', '$filter', 'taxonService', 'authenticatedUserService', '$location',
    function ($scope, $mdDialog, serverCallService, translationService, toastService, $rootScope, $filter, taxonService, authenticatedUserService, $location) {
        var ROLE_MODERATOR = "MODERATOR";

        function init() {
            if (!$scope.user) return;
            $scope.selectedRole = $scope.user.role;
            if ($scope.user.userTaxons.length === 0) {
                $scope.user.userTaxons = [{}];
            }
            serverCallService.makeGet("rest/role", {}, getRolesSuccess, fail)
        }

        function getRolesSuccess(roles) {
            if (!roles) fail();
            else $scope.roles = roles;
        }

        $scope.update = function () {
            serverCallService.makePost("rest/user", $scope.user, updateUserSuccess, fail)
        };

        $scope.setRole = function (role) {
            $scope.user.role = role;
        };

        $scope.getTranslation = function (key) {
            return $filter('translate')(key);
        };

        $scope.addNewTaxon = function () {
            var educationalContext = taxonService.getEducationalContext($scope.user.userTaxons[0]);

            $scope.user.userTaxons.push(educationalContext);
        };

        $scope.deleteTaxon = function (index) {
            $scope.user.userTaxons.splice(index, 1);
        };

        $scope.$watch('user.userTaxons', function (newTaxons, oldTaxons) {
            if (newTaxons !== oldTaxons && newTaxons.length > 0) {
                $scope.selectedRole = ROLE_MODERATOR;
                $scope.user.role = ROLE_MODERATOR;
            }
        }, true);

        $scope.isEmpty = function (object) {
          return _.isEmpty(object);
        };

        function updateUserSuccess(user) {
            if (isEmpty(user)) {
                fail();
            } else {
                var authenticatedUser = authenticatedUserService.getAuthenticatedUser();
                if (user.id === authenticatedUser.user.id) {
                    authenticatedUser.user = user;
                    authenticatedUserService.setAuthenticatedUser(authenticatedUser);
                    $location.url('/');
                }
                $mdDialog.hide();
                toastService.show('USER_UPDATED');
            }
        }

        function fail() {
            console.log("Failed to update user");
        }

        $scope.cancel = function () {
            $mdDialog.hide();
        };

        init();
    }
]);
