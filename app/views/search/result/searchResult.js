define(['app'], function(app)
{
    app.controller('searchResultController', ['$scope', "serverCallService", 'translationService', '$location',
             function($scope, serverCallService, translationService, $location) {
    	var searchObject = $location.search();
    	
    	if (searchObject.q) {
	    	var params = {'q': searchObject.q};
	    	serverCallService.makeGet("rest/search", params, getAllMaterialSuccess, getAllMaterialFail);
    	}
    	
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