define(['app'], function(app)
{
  app.filter('range', function() {
    return function(val, range) {
      range = parseInt(range);
      for (var i = 0; i < range; i++)
        val.push(i);
      return val;
    };
  });

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
