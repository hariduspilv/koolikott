define([
    'app',
    'angular-material-data-table',
    'services/serverCallService',
    'views/dashboard/baseTable/baseTable'
], function(app) {
    app.controller('imporperPortfoliosController', ['$scope', 'serverCallService', '$controller', '$filter',
        function($scope, serverCallService, $controller, $filter) {
            var base = $controller('baseTableController', {
                $scope: $scope
            });

            serverCallService.makeGet("rest/portfolio/getImproper", {}, base.getItemsSuccess, base.getItemsFail);

            $scope.title = $filter('translate')('DASHBOARD_IMRPOPER_PORTFOLIOS');
            
            $scope.bindTable = function() {
                base.buildTable('#improper-portfolios-table', 'views/dashboard/improper/portfolio/improperPortfolio.html');
            }
        }
    ]);
});