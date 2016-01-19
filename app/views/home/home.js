define(['app'], function(app)
{
  app.controller('homeController', ['$scope', "serverCallService", 'translationService', function($scope, serverCallService, translationService) {
    $scope.showHints = true;

    var params = {};
    serverCallService.makeGet("rest/material/getNewestMaterials?numberOfMaterials=8", params, getNewestMaterialsSuccess, requestFailed);

    function getNewestMaterialsSuccess(data) {
      if (isEmpty(data)) {
        log('No data returned by session search.');
      } else {
        $scope.materials = data;
      }
    }
    
    
    serverCallService.makeGet("rest/material/getPopularMaterials?numberOfMaterials=8", params, getPopularMaterialsSuccess, requestFailed);

    function getPopularMaterialsSuccess(data) {
      if (isEmpty(data)) {
        log('No data returned by session search.');
      } else {
        $scope.popularMaterials = data;
      }
    }

    function requestFailed(data, status) {
      console.log('Session search failed.')
    }
    
  }]);
});
