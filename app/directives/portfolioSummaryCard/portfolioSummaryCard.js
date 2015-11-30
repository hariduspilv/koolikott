define(['app'], function(app)
{
    app.directive('dopPortfolioSummaryCard', ['translationService', '$location', '$mdSidenav', '$mdDialog', '$rootScope',
        function(translationService, $location, $mdSidenav, $mdDialog, $rootScope) {
            return {
                scope: true,
                templateUrl: 'directives/portfolioSummaryCard/portfolioSummaryCard.html',
                controller: function ($scope, $location) {

                $scope.showEditPortfolioDialog = function($event) {
                    $event.preventDefault();

                    console.log("dopPortfolioSummaryCard");
                    console.log($scope.portfolio);

                    $mdDialog.show({
                        controller: 'addPortfolioDialog',
                        templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                        locals:{portfolio: $scope.portfolio}
                    });
                };

                }
            };
        }]);

    return app;
});
