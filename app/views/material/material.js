define(['app'], function(app)
{
    app.controller('materialController', ['$scope', "serverCallService", "$filter", '$route', '$rootScope', 
    		 function($scope, serverCallService, $filter, $route, $rootScope) {
    	var materialId = $route.current.params.materialId;
        var params = {};
    	serverCallService.makeGet("rest/material/find?materialId=" + materialId, params, getMaterialSuccess, getMaterialFail);

    	function getMaterialSuccess(data) {

            if (isEmpty(data)) {
                log('No data returned by session search.');
                } else {
                    $scope.material = data;
                    for(var i = 0; i < data.descriptions.length; i++) {
                        if(data.descriptions[i].language === $rootScope.language) {
                            $scope.description =  data.descriptions[i].description;
                        }
                    }
                }
    	}
    	
    	function getMaterialFail(data, status) {
            console.log('Session search failed.')
    	}	
    }]);
});