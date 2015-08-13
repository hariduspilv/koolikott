define(['app'], function(app)
{
    app.controller('myprofileController', ['$scope', 'serverCallService', '$route', 'translationService', '$rootScope', 'searchService', '$location',
        function($scope, serverCallService, $route, translationService, $rootScope, searchService, $location) {
    	
        if ($rootScope.savedMaterial){
            $scope.material = $rootScope.savedMaterial;
            init();
        } else {
            var materialId = $route.current.params.materialId;
            var params = {};
            serverCallService.makeGet("rest/material?materialId=" + materialId, params, getMaterialSuccess, getMaterialFail); 
        }
    	
        function getMaterialSuccess(material) {
            if (isEmpty(material)) {
                log('No data returned by getting material');
                } else {
                    $scope.material = material;
                    init();
                }
    	}
    	
    	function getMaterialFail(material, status) {
            log('Getting materials failed.');
    	}
    }]);
});