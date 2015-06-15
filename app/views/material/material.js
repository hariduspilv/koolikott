define(['app'], function(app)
{
    app.controller('materialController', ['$scope', 'serverCallService', '$route', 'translationService', '$rootScope',
    		 function($scope, serverCallService, $route, translationService, $rootScope) {
        if ($rootScope.savedMaterial){
            $scope.material = $rootScope.savedMaterial;
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
                }
    	}
    	
    	function getMaterialFail(material, status) {
            log('Getting materials failed.');
    	}

        $scope.getCorrectLanguageString = function(languageStringList) {
            if (languageStringList && $scope.material.language) {
               return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), $scope.material.language.code);
            }
        }	
    }]);
});