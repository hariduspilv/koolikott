define(['app'], function(app)
{
    app.controller('materialController', ['$scope', "serverCallService", "$filter", '$route',
    		 function($scope, serverCallService, $filter, $route) {
    	console.log($route.current.params);
    	var materialId = $route.current.params.materialId;
        var params = {};
    	serverCallService.makeGet("rest/material/find?materialId=" + materialId, params, getAllMaterialSuccess, getAllMaterialFail);

    	function getAllMaterialSuccess(data) {

            if (isEmpty(data)) {
                log('No data returned by session search.');
                } else {
                        $scope.material = data;
                        console.log(data);
                }
    	}
    	
    	function getAllMaterialFail(data, status) {
            console.log('Session search failed.')
    	}
    }]);
});