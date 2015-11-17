define(['app'], function(app)
{
    app.directive('portfolioHeader', ['translationService', '$location', '$mdSidenav',
     function(translationService, $location, $mdSidenav) {
        return {
            scope: true,
            templateUrl: 'directives/portfolioHeader/portfolioHeader.html',
            controller: function ($scope, $location) {
                $scope.toggleSidenav = function() {
                    $mdSidenav('left').toggle();
                }
            }
        };
    }]);

    return app;
});
