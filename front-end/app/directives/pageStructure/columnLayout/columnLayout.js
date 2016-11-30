define([
    'angularAMD',
    'directives/tableOfContents/tableOfContents',
    'directives/alert/alert'
], function(angularAMD) {
    angularAMD.directive('dopColumnLayout', function() {
        return {
            scope: true,
            templateUrl: 'directives/pageStructure/columnLayout/columnLayout.html',
            controller: function($scope, $rootScope, $mdSidenav, $window) {
                $scope.toggleSidenav = function() {
                    $mdSidenav('left').toggle();
                };

                $scope.sidenavIsOpen = function() {
                    setTimeout(function() {
                        return $mdSidenav('left').isOpen();
                    }, 100);
                };

                $scope.$watch(function() {
                    return $rootScope.savedPortfolio;
                }, function(newPortfolio, oldPortfolio) {
                    $scope.portfolio = newPortfolio;
                });

                $scope.portfolio = $rootScope.savedPortfolio;
            }
        };
    });
});
