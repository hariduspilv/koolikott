define(['app'], function(app)
{
    app.controller('addPortfolioDialog', ['$scope', '$mdDialog',
        function($scope, $mdDialog) {
            
            $scope.hide = function() {
                $mdDialog.hide();
            };
        }
    ]);

    app.directive('dopAddPortfolio', [ 
        function() {
            return {
                scope: true,
                templateUrl: 'app/directives/addPortfolio/addPortfolio.html',
                controller: function ($scope, $mdDialog) {
                    
                    $scope.ShowAddPortfolioDialog = function() {
                        $mdDialog.show({
                            controller: 'addPortfolioDialog',
                            templateUrl: 'app/directives/addPortfolio/addPortfolioDialog.html'
                        });
                    };                    
                }
            };
        }
    ]);

    return app;
});