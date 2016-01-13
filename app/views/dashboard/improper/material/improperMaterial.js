define(['app'], function(app) {
    app.controller('improperMaterialController', ['$scope', 'serverCallService', '$controller', '$filter', 
     function ($scope, serverCallService, $controller, $filter) {    
        var base = $controller('dashboardController', { $scope: $scope });
        
        serverCallService.makeGet("rest/material/getImproper", {}, base.getImproperSuccess, base.getImproperFail);

        $scope.title = $filter('translate')('DASHBOARD_IMRPOPER_MATERIALS');


    }]);
});


