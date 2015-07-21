define(['app'], function(app)
{
    app.controller('searchResultController', ['$scope', "serverCallService", 'translationService', '$location', '$rootScope',
             function($scope, serverCallService, translationService, $location, $rootScope) {
    	var searchObject = $location.search();
    	
    	if (searchObject.q) {
	    	var params = {'q': searchObject.q};
	    	serverCallService.makeGet("rest/search", params, getAllMaterialSuccess, getAllMaterialFail);
	    	$rootScope.searchFields.searchQuery = searchObject.q;
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
    	
    	$scope.$on("$destroy", function() {
    		$rootScope.searchFields.searchQuery = "";
        });
    	
    }]);
});