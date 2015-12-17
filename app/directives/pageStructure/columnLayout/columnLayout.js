define(['app'], function(app)
{
    app.directive('dopColumnLayout', [
     function() {
        return {
            scope: true,
            templateUrl: 'directives/pageStructure/columnLayout/columnLayout.html',
            controller: function ($scope, $rootScope, $mdSidenav) {
              $scope.toggleSidenav = function() {
                  $mdSidenav('left').toggle();
              };
            	$scope.$watch(function() {
            		return $rootScope.savedPortfolio;
            	}, function(newPortfolio, oldPortfolio) {
            		if (newPortfolio !== oldPortfolio) {
            			$scope.portfolio = newPortfolio;
            		}
                });

            	$scope.portfolio = $rootScope.savedPortfolio;
            }
        };
    }]);

    return app;
});
