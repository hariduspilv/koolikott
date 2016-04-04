define([
    'app',
    'angular-material-data-table',
    'services/serverCallService',
    'views/dashboard/baseTable/baseTable'
], function(app) {
    app.controller('brokenMaterialsController', ['$scope', 'serverCallService', '$controller', '$filter',
        function ($scope, serverCallService, $controller, $filter) {
            var base = $controller('baseTableController', { $scope: $scope });

            serverCallService.makeGet("rest/material/getBroken", {}, getBrokenItemsSuccess, base.getItemsFail);

            $scope.title = $filter('translate')('BROKEN_MATERIALS');

            $scope.bindTable = function() {
                base.buildTable('#broken-materials-table', 'views/dashboard/broken/material/brokenMaterial.html');
            };

            function getBrokenItemsSuccess(items) {
                base.getItemsSuccess(items, 'byReportCount', true);
            }

        }]);
});


