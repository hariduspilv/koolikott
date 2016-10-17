define([
    'app',
    'angular-material-data-table',
    'services/serverCallService',
    'services/userDataService',
    'views/dashboard/baseTable/baseTable'
], function(app) {
    app.controller('brokenMaterialsController', ['$scope', 'serverCallService', '$controller', '$filter', 'userDataService',
        function ($scope, serverCallService, $controller, $filter, userDataService) {
            var base = $controller('baseTableController', { $scope: $scope });

            $scope.title = $filter('translate')('BROKEN_MATERIALS');

            $scope.bindTable = function() {
                base.buildTable('#broken-materials-table', 'views/dashboard/broken/material/brokenMaterial.html');
            };

            userDataService.loadBrokenMaterials(function(items) {
                var list = [];
                for(var i = 0; i < items.length; i++) {
                    if(items[i].material.deleted == false) {
                        list.push(items[i]);
                    }
                }
                base.getItemsSuccess(list, 'byReportCount', true);
            });

        }]);
});


