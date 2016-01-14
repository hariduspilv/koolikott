define(['app'], function(app) {
    app.controller('improperMaterialController', ['$scope', 'serverCallService', '$controller', '$filter', 
     function ($scope, serverCallService, $controller, $filter) {    
        var base = $controller('dashboardController', { $scope: $scope });
        
        serverCallService.makeGet("rest/material/getImproper", {}, base.getItemsSuccess, base.getItemsFail);

        $scope.title = $filter('translate')('DASHBOARD_IMRPOPER_MATERIALS');


    }]);
});


