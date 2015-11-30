define(['app'], function(app)
{
    app.directive('addPortfolioFabButton', [
        function() {
            return {
                scope: true,
                templateUrl: 'directives/addPortfolioFabButton/addPortfolioFabButton.html',
                controller: function ($scope, $mdDialog) {
                    $scope.ShowAddPortfolioDialog = function() {
                        
                        var portfolio = {
                            type: ".Portfolio",
                            tags:[]
                        };
                        
                        $mdDialog.show({
                            controller: 'addPortfolioDialog',
                            templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                            locals:{portfolio: portfolio}
                        });
                        
                    };
                }
            };
        }
    ]);

    return app;
});
