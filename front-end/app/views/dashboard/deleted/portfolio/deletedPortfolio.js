define([
    'app',
    'angular-material-data-table',
    'services/serverCallService',
    'services/userDataService',
    'views/dashboard/baseTable/baseTable'
], function(app) {
    app.controller('deletedPortfoliosController', ['$scope', 'serverCallService', '$controller', '$filter', 'userDataService',
        function($scope, serverCallService, $controller, $filter, userDataService) {
            var base = $controller('baseTableController', {
                $scope: $scope
            });

            userDataService.loadDeletedPortfolios(base.getItemsSuccess);

            $scope.title = $filter('translate')('DASHBOARD_DELETED_PORTFOLIOS');

            $scope.formatMaterialUpdatedDate = function (updatedDate) {
                return formatDateToDayMonthYear(updatedDate);
            }

            function restoreSuccess(portfolio) {
                var index = $scope.data.indexOf(portfolio);
                $scope.data.splice(index, 1);
            }

            function restoreFail() {
                log("Restoring portfolio failed");
            }

            $scope.bindTable = function() {
              base.buildTable('#deleted-portfolios-table', 'views/dashboard/deleted/portfolio/deletedPortfolio.html');
            }
        }
    ]);
});
