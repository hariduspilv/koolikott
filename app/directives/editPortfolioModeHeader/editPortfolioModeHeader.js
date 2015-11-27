define(['app'], function(app)
{
    app.directive('dopEditPortfolioModeHeader', ['translationService', '$location', '$mdSidenav', '$mdDialog',
     function(translationService, $location, $mdSidenav, $mdDialog) {
        return {
            scope: true,
            templateUrl: 'directives/editPortfolioModeHeader/editPortfolioModeHeader.html',
            controller: function ($scope, $location) {
                $scope.toggleSidenav = function() {
                    $mdSidenav('left').toggle();
                };
            }
        };
    }]);

    return app;
});
