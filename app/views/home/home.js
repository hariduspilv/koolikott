define(['app'], function(app)
{
    app.controller('homeController', ['$scope', "serverCallService", 'translationService',
    		function($scope, serverCallService, translationService) {
        var params = {};
    	serverCallService.makeGet("rest/material/getNewestMaterials?numberOfMaterials=8", params, getAllMaterialSuccess, getAllMaterialFail);
    	
    	function getAllMaterialSuccess(data) {

            if (isEmpty(data)) {
                log('No data returned by session search.');
                } else {
                        $scope.materials = data;
                }
    	}
    	
    	function getAllMaterialFail(data, status) {
            console.log('Session search failed.')
    	}
    }]);
});