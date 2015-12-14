define(['app'], function(app)
{
  app.controller('homeController', ['$scope', "serverCallService", 'translationService', function($scope, serverCallService, translationService) {
    $scope.showHints = true;
    var params = {};
    serverCallService.makeGet("rest/material/getNewestMaterials?numberOfMaterials=8", params, getNewestMaterialsSuccess, getNewestMaterialsFail);

    function getNewestMaterialsSuccess(data) {
      if (isEmpty(data)) {
        log('No data returned by session search.');
      } else {
        $scope.materials = data;
      }
    }

    function getNewestMaterialsFail(data, status) {
      console.log('Session search failed.')
    }
  }]);
});
