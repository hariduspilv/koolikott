define(['app'], function(app)
{
    app.controller('aboutController', ['$scope', "serverCallService", "$filter", '$rootScope', 'translationService',
    		function($scope, serverCallService, $filter, $rootScope, translationService) {
        var pageName = "about";
        var pageLanguage = translationService.getLanguage();

        var params = {};
        var url = "rest/page?pageName=" + pageName + "&pageLanguage=" + pageLanguage;
    	serverCallService.makeGet(url, params, getAllMaterialSuccess, getAllMaterialFail);
    	
    	function getAllMaterialSuccess(data) {

            if (isEmpty(data)) {
                log('No data returned by session search.');
                } else {
                        $scope.materials = data;
                        console.log("data received");
                }
    	}
    	
    	function getAllMaterialFail(data, status) {
            console.log('Session search failed.')
    	}
    }]);
});