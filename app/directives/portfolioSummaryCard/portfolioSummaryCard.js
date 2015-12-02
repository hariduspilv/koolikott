define(['app'], function(app)
{
    app.directive('dopPortfolioSummaryCard', ['translationService', '$location', '$mdSidenav', '$mdDialog', '$rootScope',
        function(translationService, $location, $mdSidenav, $mdDialog, $rootScope) {
            return {
                scope: true,
                templateUrl: 'directives/portfolioSummaryCard/portfolioSummaryCard.html',
                controller: function ($scope, $location) {

                    $scope.formatDate = function(date) {
                        return formatDateToDayMonthYear(date);
                    }
                
                    $scope.showEditPortfolioDialog = function($event) {
                        $event.preventDefault();

                        $mdDialog.show({
                            controller: 'addPortfolioDialog',
                            templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                            locals:{portfolio: $scope.portfolio}
                        });
                    };
                    
                    $scope.absUrl = $location.absUrl();
                }
            };
        }]);

    return app;
});
