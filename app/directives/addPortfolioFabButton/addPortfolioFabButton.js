define(['app'], function(app)
{
    app.directive('addPortfolioFabButton', [
        function() {
            return {
                scope: true,
                templateUrl: 'directives/addPortfolioFabButton/addPortfolioFabButton.html',
                controller: function ($scope, $mdDialog) {
                    $scope.ShowAddPortfolioDialog = function() {
                        $mdDialog.show({
                            controller: 'addPortfolioDialog',
                            templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html'
                        });
                    };
                }
            };
        }
    ]);

    return app;
});
