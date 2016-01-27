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

            $scope.formatMaterialUpdatedDate = function (updatedDate) {
                return formatDateToDayMonthYear(updatedDate);
            }
        }
    ];
});