define(['app'], function(app)
{
    app.controller('aboutController', ['$scope', "$controller",
    		function($scope, $controller) {
    	
    	$scope.pageName = 'about';
    	$controller('abstractStaticPageController', {$scope: $scope});
    }]);
});