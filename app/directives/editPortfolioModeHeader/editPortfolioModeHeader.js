define(['app'], function(app)
{
    app.directive('dopEditPortfolioModeHeader', ['translationService', '$location', '$mdSidenav', '$mdDialog', '$rootScope',
     function(translationService, $location, $mdSidenav, $mdDialog, $rootScope) {
        return {
            scope: true,
            templateUrl: 'directives/editPortfolioModeHeader/editPortfolioModeHeader.html',
            controller: function ($scope, $location) {

                $scope.portfolioVisibility = 'PUBLIC';

                $scope.toggleSidenav = function() {
                    $mdSidenav('left').toggle();
                };

                $scope.exitEditPortfolioMode = function() {
                    $rootScope.isEditPortfolioMode = false;
                    $location.url("/");
                };

                $scope.makePublic = function() {
                    $scope.portfolioVisibility = 'PUBLIC';
                }

                $scope.makeNotListed = function() {
                    $scope.portfolioVisibility = 'NOT_LISTED';
                }

                $scope.makePrivate = function() {
                    $scope.portfolioVisibility = 'PRIVATE';
                }
            }
        };
    }]);

    return app;
});
 