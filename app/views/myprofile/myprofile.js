define(['app'], function(app)
{
    app.controller('myprofileController', ['$scope', 'serverCallService', '$route', 'translationService',
        function($scope, serverCallService, $route, translationService) {
        
        console.log("username: " + $route.current.params.username);

        var params = {};
        //serverCallService.makeGet("rest/material?materialId=" + materialId, params, getMaterialSuccess, getMaterialFail); 
    	
        function getMaterialSuccess(material) {
            if (isEmpty(material)) {
                log('No data returned by getting material');
                } else {
                    $scope.material = material;
                }
    	}
    	
    	function getMaterialFail(material, status) {
            log('Getting materials failed.');
    	}
    }]);
});