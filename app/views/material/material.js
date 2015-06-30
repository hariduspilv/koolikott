define(['app'], function(app)
{
    app.controller('materialController', ['$scope', 'serverCallService', '$route', 'translationService', '$rootScope',
    		 function($scope, serverCallService, $route, translationService, $rootScope) {
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
    	
    	function init() {
            setSourceType();
            $scope.materialSubjects = getSubjectList()
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

        $scope.formatMaterialIssueDate = function(issueDate) {
            return formatIssueDate(issueDate);
            
        }
        
        /**
         *  Gets the list of unique material subjects
         */
        function getSubjectList() {
        	var subjects = [];

        	var classifications = $scope.material.classifications;
        	if (classifications) {
            	for (var i = 0; i < classifications.length; i++) {
            		subject = getClassificationSubject(classifications[i]);
            		if (subjects.indexOf(subject) < 0) {
            			subjects[subjects.length] = subject;
            		}
            	}
            }
        	
        	return subjects;
        }

        /**
         * Subject is the root classification 
         */
        function getClassificationSubject (classification) {
            if (!classification.parent) {
                return classification.name;
            }
            return getClassificationSubject(classification.parent);
        }

    }]);
});