define(['app'], function(app)
{
    app.controller('materialController', ['$scope', 'serverCallService', '$route', 'translationService', '$rootScope', 'searchService', '$location', 'alertService', 
    		 function($scope, serverCallService, $route, translationService, $rootScope, searchService, $location, alertService) {
    	
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
            	log('No data returned by getting material. Redirecting to landing page');
                alertService.setErrorAlert('ERROR_MATERIAL_NOT_FOUND');
                $location.url("/");
            } else {
                $scope.material = material;
                init();
            }
    	}
    	
    	function getMaterialFail(material, status) {
            log('Getting materials failed. Redirecting to landing page');
            alertService.setErrorAlert('ERROR_MATERIAL_NOT_FOUND');
            $location.url("/");
    	}
    	
    	function init() {
            setSourceType();
            
            var params = {
                'id': $scope.material.id
            };
            serverCallService.makePost("rest/material/increaseViewCount", params, countViewSuccess, countViewFail); 
    	}

        function countViewSuccess(data) { }
        
        function countViewFail(data, status) { }

        $scope.getCorrectLanguageString = function(languageStringList) {
            if (languageStringList) {
               return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), $scope.material.language);
            }
        }
        
        function isYoutubeVideo(url) {
        	// regex taken from http://stackoverflow.com/questions/2964678/jquery-youtube-url-validation-with-regex #ULTIMATE YOUTUBE REGEX
        	var youtubeUrlRegex = /^(?:https?:\/\/)?(?:www\.)?(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=))((\w|-){11})(?:\S+)?$/;
            return url && url.match(youtubeUrlRegex);
        }
        
        function setSourceType() {
        	if (isYoutubeVideo($scope.material.source)) {
        		$scope.sourceType = 'YOUTUBE';
        	} else {
        		$scope.sourceType = 'LINK';
        	}
        }

        $scope.formatMaterialIssueDate = function(issueDate) {
            return formatIssueDate(issueDate);
            
        }
        
        $scope.formatMaterialUpdatedDate = function(updatedDate) {
            var updated = new Date(updatedDate);
            return formatDay(updated.getDate()) + "." + formatMonth(updated.getMonth() + 1) + "." + updated.getFullYear();
        }
        
        $scope.isNullOrZeroLength = function (arg) {
        	return !arg || !arg.length;
        }

        $scope.getAuthorSearchURL = function (firstName, surName) {
            searchService.setSearch('author:"' + firstName + " " + surName + '"');
            $location.url(searchService.getURL());
        }
    }]);
});