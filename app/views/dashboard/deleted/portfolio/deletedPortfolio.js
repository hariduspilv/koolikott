define(['app'], function(app) {
    app.controller('deletedPortfolioController', ['$scope', 'serverCallService', '$controller', '$filter',
     function ($scope, serverCallService, $controller, $filter) {    
        var base = $controller('dashboardController', { $scope: $scope });

        serverCallService.makeGet("rest/portfolio/getDeleted", {}, base.getItemsSuccess, base.getItemsFail);
        
        $scope.title = $filter('translate')('DASHBOARD_DELETED_PORTFOLIOS');

        $scope.restorePortfolio = function(portfolio) {
            serverCallService.makePost("rest/portfolio/restore", portfolio, function() {restoreSuccess(portfolio)}, restoreFail);   
        }
        
        function restoreSuccess(portfolio) {
        	var index = $scope.data.indexOf(portfolio);
        	$scope.data.splice(index, 1);
        }
        
        function restoreFail() {
        	log("Restoring portfolio failed");
        }

    }]);
});


