define(['app'], function(app) {
    app.controller('deletedPortfolioController', ['$scope', 'serverCallService', '$controller', '$filter',
     function ($scope, serverCallService, $controller, $filter) {    
        var base = $controller('dashboardController', { $scope: $scope });

        serverCallService.makeGet("rest/portfolio/getDeleted", {}, base.getItemsSuccess, base.getItemsFail);
        
        $scope.title = $filter('translate')('DASHBOARD_DELETED_PORTFOLIOS');


    }]);
});


