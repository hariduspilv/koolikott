define(['app',], function (app) {
    app.directive('dopRestrictedUsersTable', function () {
        return {
            templateUrl: 'directives/dashboard/userManagement/usersTable.html',
            controller: function ($scope, serverCallService, $filter) {
                $scope.showTaxonColumn = false;
                $scope = $scope.$parent;

                function init() {
                    $scope.title = $filter('translate')('RESTRICTED_USERS_TAB');
                    serverCallService.makeGet("rest/user/restrictedUser", {}, success, fail)
                }

                function success(data) {
                    if (data) $scope.getItemsSuccess(data, 'byUsername', false);
                    else fail();
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
    })
});
