define(['app'], function(app)
{
    app.directive('dopMainFabButton', [
        function() {
            return {
                scope: true,
                templateUrl: 'directives/mainFabButton/mainFabButton.html',
                controller: function ($scope, $mdDialog) {
                    $scope.isOpen = false;

                    var portfolio = {
                        type: ".Portfolio",
                        tags:[]
                    };

                    $scope.showAddPortfolioDialog = function() {
                        $mdDialog.show({
                            controller: 'addPortfolioDialog',
                            templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                            locals:{portfolio: portfolio}
                        });
                    }

                    $scope.showAddMaterialDialog = function() {
                        $mdDialog.show({
                            controller: 'addMaterialDialog',
                            templateUrl: 'views/addMaterialDialog/addMaterialDialog.html'
                        });
                    }
                }
            };
        }
    ]);

    return app;
});
