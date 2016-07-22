define([
    'app',
    'angular-material-data-table',
    'services/serverCallService',
    'views/dashboard/baseTable/baseTable'
], function(app) {
    app.controller('deletedPortfoliosController', ['$scope', 'serverCallService', '$controller', '$filter',
        function($scope, serverCallService, $controller, $filter) {
            var base = $controller('baseTableController', {
                $scope: $scope
            });

            serverCallService.makeGet("rest/portfolio/getDeleted", {}, base.getItemsSuccess, base.getItemsFail);

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