define(['app'], function(app)
{
    app.controller('abstractStaticPageController', ['$scope', "serverCallService", 'translationService', '$sce',
    		function($scope, serverCallService, translationService, $sce) {
    	
    	function getPage(pageLanguage) {
	
	        var params = {
        		'name': $scope.pageName,
        		'language': pageLanguage
		    };
	        var url = "rest/page";
	    	serverCallService.makeGet(url, params, getPageSuccess, getPageFail);
    	}
    	
    	function getPageSuccess(data) {
            if (isEmpty(data)) {
            	console.log('No data returned.');
            } else {
                $scope.pageContent = $sce.trustAsHtml(data.content);
            }
    	}
    	
    	function getPageFail(data, status) {
            console.log('Getting page failed.')
    	}
    	
    	$scope.$on("$destroy", function() {
    		translationService.removeLanguageChangeListener();
        });
    	
    	getPage(translationService.getLanguage());
    	translationService.setLanguageChangeListener(getPage);
    }]);
});