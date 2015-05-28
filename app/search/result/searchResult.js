define(['app'], function(app)
{
    app.controller('searchResultController', ['$scope', "serverCallService", function($scope, serverCallService) {
    	var params = {};
    	serverCallService.makeGet("/rest/material/getAll", params, getAllMaterialSuccess, getAllMaterialFail);
    	
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