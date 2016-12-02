define([
    'app',
    'services/serverCallService',
    'services/translationService',
    'services/authenticatedUserService'
], function (app) {
    return ['$scope', '$mdDialog', 'serverCallService', 'translationService', 'toastService', '$rootScope',
        function ($scope, $mdDialog, serverCallService, translationService, toastService, $rootScope) {
            function init() {
                if(!$scope.user) return;
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

            $scope.addNewTaxon = function () {
                var educationalContext = $rootScope.taxonService.getEducationalContext($scope.user.userTaxons[0]);

                $scope.user.userTaxons.push(educationalContext);
            };

            $scope.deleteTaxon = function (index) {
                $scope.user.userTaxons.splice(index, 1);
            };

            function updateUserSuccess(user) {
                if (isEmpty(user)) {
                    fail();
                } else {
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
        }];
});
