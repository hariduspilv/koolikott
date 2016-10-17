define([
    'app',
    'angular-material-data-table',
    'services/serverCallService',
    'services/userDataService',
    'views/dashboard/baseTable/baseTable'
], function(app) {
    app.controller('deletedMaterialsController', ['$scope', 'serverCallService', '$controller', '$filter', 'userDataService',
      function($scope, serverCallService, $controller, $filter, userDataService) {
          var base = $controller('baseTableController', {
              $scope: $scope
          });

          userDataService.loadDeletedMaterials(base.getItemsSuccess);

          $scope.title = $filter('translate')('DASHBOARD_DELETED_MATERIALS');

          $scope.formatMaterialUpdatedDate = function (updatedDate) {
              return formatDateToDayMonthYear(updatedDate);
          }

          $scope.bindTable = function() {
              base.buildTable('#deleted-materials-table', 'views/dashboard/deleted/material/deletedMaterial.html');
          }
    }]);
});
