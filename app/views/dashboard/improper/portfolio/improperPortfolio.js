define(['app'], function(app) {
    app.controller('improperPortfolioController', ['$scope', 'serverCallService', '$controller', '$filter', 
     function ($scope, serverCallService, $controller, $filter) {    
    	var base = $controller('dashboardController', { $scope: $scope });
        
        serverCallService.makeGet("rest/portfolio/getImproper", {}, base.getImproperSuccess, base.getImproperFail);
        
        $scope.title = $filter('translate')('DASHBOARD_IMRPOPER_PORTFOLIOS');
        
    }]);
});


