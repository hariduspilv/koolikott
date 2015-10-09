define(['app'], function(app)
{
    app.controller('materialController', ['$scope', 'serverCallService', '$route', 'translationService', '$rootScope', 'searchService', '$location', 'alertService',
    		 function($scope, serverCallService, $route, translationService, $rootScope, searchService, $location, alertService) {
        $scope.showMaterialContent = false;

        $rootScope.$on('fullscreenchange', function() {
            $scope.$apply(function() {
                $scope.showMaterialContent = !$scope.showMaterialContent;
            });
        });

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
                'type' : '.Material',
                'id': $scope.material.id
            };
            serverCallService.makePost("rest/material/increaseViewCount", params, countViewSuccess, countViewFail); 

            if ($scope.sourceType === 'SLIDESHARE') {
                getSlideshareData();
            }
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

        function isSlideshareLink(url) {
            var slideshareUrlRegex = /^https?\:\/\/www\.slideshare\.net\/[a-zA-Z0-9\-]+\/[a-zA-Z0-9\-]+$/;
            return url && url.match(slideshareUrlRegex);
        }
        
        function setSourceType() {
            if (isYoutubeVideo($scope.material.source)) {
                $scope.sourceType = 'YOUTUBE';
            } else if (isSlideshareLink($scope.material.source)) {
                $scope.sourceType = 'SLIDESHARE';
            } else {
        		$scope.sourceType = 'LINK';
            }
        }

        $scope.formatMaterialIssueDate = function(issueDate) {
            return formatIssueDate(issueDate);
            
        }
        
        $scope.formatMaterialUpdatedDate = function(updatedDate) {
            return formatDateToDayMonthYear(updatedDate);    
        }
        
        $scope.isNullOrZeroLength = function (arg) {
        	return !arg || !arg.length;
        }

        $scope.getAuthorSearchURL = function (firstName, surName) {
            searchService.setSearch('author:"' + firstName + " " + surName + '"');
            $location.url(searchService.getURL());
        }

        $scope.showSourceFullscreen = function(){
            $scope.fullscreenCtrl.toggleFullscreen();
        };

        function getSlideshareData() {
            var params = {
                url: $scope.material.source,
                format: "jsonp",
                callback: "JSON_CALLBACK"
            };
            serverCallService.makeJsonp("https://www.slideshare.net/api/oembed/2", params, getSlideshareDataSuccess, getSlideshareDataFail); 
        }

        function getSlideshareDataSuccess(data) {
            if (!isEmpty(data)) {
                var embedCodeRegex = /(?:embed_code\/key\/)((\w|-)+)(?:\")/;
                var embedCode = data.html.match(embedCodeRegex)[1];
                $scope.slideshareEmbedLink = "https://www.slideshare.net/slideshow/embed_code/key/" + embedCode;
            } else {
                log("Slideshare data is empty. ");
                $scope.sourceType = 'LINK';
            }
        }

        function getSlideshareDataFail(data) {
            log("Failed to get slideshare data. ");
            $scope.sourceType = 'LINK';
        }

    }]);
});