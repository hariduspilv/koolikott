'use strict'

angular.module('koolikottApp')
.directive('dopModeratorsTable',
function () {
    return {
        templateUrl: 'directives/dashboard/userManagement/usersTable.html',
        controller: function ($scope, $filter, serverCallService) {
            $scope.showTaxonColumn = true;
            $scope = $scope.$parent;

            function init() {
                $scope.title = $filter('translate')('MODERATORS_TAB');
                serverCallService.makeGet("rest/user/moderator", {}, success, fail);
            }

            function success(data) {
                if (data) $scope.getItemsSuccess(data, 'byUsername', false);
                else fail()
            }

            function fail() {
                console.log("Failed to get users")
            }

            $scope.getTaxonTranslation = function (taxon) {
                if (taxon.level !== '.EducationalContext') {
                    return taxon.level.toUpperCase().substr(1) + "_" + taxon.name.toUpperCase();
                } else {
                    return taxon.name.toUpperCase();
                }

            };

            init();
        }
    }
});
