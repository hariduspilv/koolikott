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

            $scope.title = $filter('translate')('DASHBOARD_DELETED_PORTFOLIOS');

            $scope.formatMaterialUpdatedDate = function (updatedDate) {
                return formatDateToDayMonthYear(updatedDate);
            };

            $scope.bindTable = function() {
              base.buildTable('#deleted-portfolios-table', 'views/dashboard/deleted/portfolio/deletedPortfolio.html');
            };

            serverCallService.makeGet("rest/portfolio/getDeleted", {}, base.getDeletedItemsSuccess, base.getItemsFail);
        }
    ]);
});
