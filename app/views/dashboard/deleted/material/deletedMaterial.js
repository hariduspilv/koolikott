define([
    'app',
    'angular-material-data-table',
    'services/serverCallService',
    'views/dashboard/dashboard'
], function(app) {
    return ['$scope', 'serverCallService', '$controller', '$filter',
        function($scope, serverCallService, $controller, $filter) {
            var base = $controller('dashboardController', {
                $scope: $scope
            });

            serverCallService.makeGet("rest/material/getDeleted", {}, base.getItemsSuccess, base.getItemsFail);

            $scope.title = $filter('translate')('DASHBOARD_DELETED_MATERIALS');

            $scope.restoreMaterial = function(material) {
                serverCallService.makePost("rest/material/restore", material, function() {
                    restoreSuccess(material)
                }, restoreFail);
            }

            function restoreSuccess(material) {
                var index = $scope.data.indexOf(material);
                $scope.data.splice(index, 1);
            }

            function restoreFail() {
                log("Restoring material failed");
            }
        }
    ];
});