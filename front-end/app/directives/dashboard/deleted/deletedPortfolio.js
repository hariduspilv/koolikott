define(['app',], function (app) {
    app.directive('dopDeletedPortfolio', function () {
        return {
            templateUrl: 'directives/dashboard/deleted/deleted.html',
            controller: function ($scope, $filter, serverCallService) {
                $scope = $scope.$parent;

                function init() {
                    $scope.title = $filter('translate')('DASHBOARD_DELETED_PORTFOLIOS');
                    serverCallService.makeGet("rest/portfolio/getDeleted", {}, success, fail);
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
    })
});
