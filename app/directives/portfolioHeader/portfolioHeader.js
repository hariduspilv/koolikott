define(['app'], function(app)
{
    app.directive('portfolioHeader', ['translationService', '$location', '$mdSidenav', '$mdDialog',
     function(translationService, $location, $mdSidenav, $mdDialog) {
        return {
            scope: true,
            templateUrl: 'directives/portfolioHeader/portfolioHeader.html',
            controller: function ($scope, $location) {
                $scope.toggleSidenav = function() {
                    $mdSidenav('left').toggle();
                };

                $scope.showSendLinkDialog = function() {
                    $mdDialog.show({
                        controller: function($scope, $mdDialog) {
                            $scope.cancel = function() {
                                $mdDialog.hide();
                            };

                            // TEST DATA
                            $scope.link = "https://ekoolikott.ee/#/viewPortfolio?id=1";
                            // TEST DATA
                        },
                        templateUrl: 'views/addPortfolio/sendLinkDialog/sendLinkDialog.html'
                    });
                };
            }
        };
    }]);

    return app;
});
