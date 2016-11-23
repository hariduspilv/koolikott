define([
    'app',
    'angular-material-data-table',
    'services/serverCallService',
    'views/dashboard/baseTable/baseTable'
], function(app) {
    app.controller('deletedMaterialsController', ['$scope', 'serverCallService', '$controller', '$filter',
      function($scope, serverCallService, $controller, $filter) {
          var base = $controller('baseTableController', {
              $scope: $scope
          });

          $scope.title = $filter('translate')('DASHBOARD_DELETED_MATERIALS');

          $scope.formatMaterialUpdatedDate = function (updatedDate) {
              return formatDateToDayMonthYear(updatedDate);
          };

          $scope.bindTable = function() {
              base.buildTable('#deleted-materials-table', 'views/dashboard/deleted/material/deletedMaterial.html');
          };

          serverCallService.makeGet("rest/material/getDeleted", {}, base.getDeletedItemsSuccess, base.getItemsFail);
    }]);
});
