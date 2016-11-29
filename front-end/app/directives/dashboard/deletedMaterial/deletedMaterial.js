define(['app',], function (app) {
    app.directive('dopDeletedMaterial', function () {
        return {
            templateUrl: 'directives/dashboard/deletedMaterial/deletedMaterial.html',
            controller: function ($scope, $filter, serverCallService) {
                $scope = $scope.$parent;

                function init() {
                    $scope.title = $filter('translate')('DASHBOARD_DELETED_MATERIALS');
                    serverCallService.makeGet("rest/material/getDeleted", {}, success, fail);
                }

                function success(data) {
                    if (data) $scope.getItemsSuccess(data, 'byUpdatedAt');
                    else fail();
                }

                function fail() {
                    console.log("Failed to get deleted portfolios")
                }

                init();

            }
        }
    });
});
