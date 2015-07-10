define(['app'], function(app)
{
    app.controller('helpController', ['$scope', "$controller",
    		function($scope, $controller) {
    	
    	$scope.pageName = 'help';
    	$controller('abstractStaticPageController', {$scope: $scope});
    }]);
});