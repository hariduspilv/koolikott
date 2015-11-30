define(['app'], function(app)
{
    app.directive('addPortfolioFabButton', [
        function() {
            return {
                scope: true,
                templateUrl: 'directives/addPortfolioFabButton/addPortfolioFabButton.html',
                controller: function ($scope, $mdDialog) {
                    $scope.ShowAddPortfolioDialog = function() {
                        
                        var default_portfolio = {
                            type: ".Portfolio",
                            tags:[]
                        };
                        
                        $mdDialog.show({
                            controller: 'addPortfolioDialog',
                            templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                            locals:{portfolio: default_portfolio}
                        });
                        
                    };
                }
            };
        }
    ]);

    return app;
});
