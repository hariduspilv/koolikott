define(['app'], function(app)
{
    app.controller('materialController', ['$scope', 'serverCallService', '$route', 'translationService', '$rootScope',
    		 function($scope, serverCallService, $route, translationService, $rootScope) {
    	
    	var materialId = $route.current.params.materialId;
        var params = {};
        if ($rootScope.savedMaterial){
            $scope.material = $rootScope.savedMaterial;
            init();
        } else {
            serverCallService.makeGet("rest/material?materialId=" + materialId, params, getMaterialSuccess, getMaterialFail); 
        }
        
        function countViewSuccess(data) { }
        
        function countViewFail(data, status) { }
    	
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
            
            serverCallService.makePost("rest/material/increaseViewCount", $scope.material.id, countViewSuccess, countViewFail); 
    	}

        $scope.getCorrectLanguageString = function(languageStringList) {
            if (languageStringList) {
               return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), $scope.material.language);
            }
        }
        
        $scope.getAllCorrectLanguageStrings = function(languageStringList) {
            if (languageStringList) {
            	var languagePreferenceList = [translationService.getLanguage(), "est", "rus", $scope.material.language];
                return getAllUserDefinedLanguageStrings(languageStringList, languagePreferenceList);
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
        
        $scope.isNullOrZeroLength = function (arg) {
        	return !arg || !arg.length;
        }
        
    }]);
});