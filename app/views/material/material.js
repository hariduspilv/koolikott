define(['app'], function(app)
{
    app.controller('materialController', ['$scope', 'serverCallService', '$route', 'translationService', '$rootScope',
    		 function($scope, serverCallService, $route, translationService, $rootScope) {
        if ($rootScope.savedMaterial){
            $scope.material = $rootScope.savedMaterial;
                                log($scope.material.classifications)

            setSourceType();
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
                    setSourceType();
                    log(material.classifications)
                }
    	}
    	
    	function getMaterialFail(material, status) {
            log('Getting materials failed.');
    	}

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

        $scope.formatIssueDate = function(issueDate) {
            if (!issueDate) {
                return;
            }
            
            if (issueDate.day && issueDate.month && issueDate.year) {
                // full date
                return formatDay(issueDate.day) + "." + formatMonth(issueDate.month) + "." + formatYear(issueDate.year); 
            } else if (issueDate.month && issueDate.year) {
                // month date
                return formatMonth(issueDate.month) + "." + formatYear(issueDate.year); 
            } else if (issueDate.year) {
                // year date
                return formatYear(issueDate.year); 
            }
        }
        
        function formatDay(day) {
            return day > 9 ? "" + day : "0" + day; 
        }
        
        function formatMonth(month) {
            return month > 9 ? "" + month : "0" + month; 
        }
        
        function formatYear(year) {
            return year < 0 ? year * -1 : year; 
        }
    }]);
});