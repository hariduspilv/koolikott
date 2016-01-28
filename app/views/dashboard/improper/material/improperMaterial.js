define([
    'app',
    'ngload!angular-material-data-table',
    'services/serverCallService',
    'views/dashboard/baseTable/baseTable'
], function(app) {
    app.controller('improperMaterialsController', ['$scope', 'serverCallService', '$controller', '$filter',
        function($scope, serverCallService, $controller, $filter) {
            var base = $controller('baseTableController', {
                $scope: $scope
            });

            serverCallService.makeGet("rest/material/getImproper", {}, base.getItemsSuccess, base.getItemsFail);

            $scope.title = $filter('translate')('DASHBOARD_IMRPOPER_MATERIALS');
            
            $scope.bindTable = function() {
              base.buildTable('#improper-materials-table', 'views/dashboard/improper/material/improperMaterial.html');
            }
        }
    ]);
});