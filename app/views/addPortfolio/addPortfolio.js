define(['app'], function(app)
{
    app.controller('addPortfolioController', ['$scope', 'translationService', '$mdSidenav',
        function($scope, translationService, $mdSidenav) {
            $scope.toggleSidenav = function() {
            	$mdSidenav('left').toggle();
			}
    	}
    ]);
});
