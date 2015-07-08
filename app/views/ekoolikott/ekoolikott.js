define(['app'], function(app)
{
    app.controller('ekoolikottController', ['$scope', "serverCallService", "$filter", '$rootScope', 'translationService', '$route',
    		function($scope, serverCallService, $filter, $rootScope, translationService, $route) {
        var pageName = $route.current.params.pageName;
        var pageLanguage = translationService.getLanguage();

        var params = {};
        var url = "rest/page?pageName=" + pageName + "&pageLanguage=" + pageLanguage;
    	serverCallService.makeGet(url, params, getAllMaterialSuccess, getAllMaterialFail);
    	
    	function getAllMaterialSuccess(data) {

            if (isEmpty(data)) {
                log('No data returned by session search.');
                } else {
                        $scope.materials = data;
                        console.log("data recieved");
                }
    	}
    	
    	function getAllMaterialFail(data, status) {
            console.log('Session search failed.')
    	}
    }]);
});