define(['app'], function(app) {
    app.controller('deletedMaterialController', ['$scope', 'serverCallService', '$controller', '$filter', 
     function ($scope, serverCallService, $controller, $filter) {    
        var base = $controller('dashboardController', { $scope: $scope });
        
        serverCallService.makeGet("rest/material/getDeleted", {}, base.getImproperSuccess, base.getImproperFail);

        $scope.title = $filter('translate')('DASHBOARD_DELETED_MATERIALS');


    }]);
});


