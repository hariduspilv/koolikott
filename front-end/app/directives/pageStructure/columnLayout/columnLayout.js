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
                  return $mdSidenav('left').isOpen();
                };
                $scope.$watch(function() {
                    return $rootScope.savedPortfolio;
                }, function(newPortfolio, oldPortfolio) {
                    $scope.portfolio = newPortfolio;
                });

                $scope.portfolio = $rootScope.savedPortfolio;

                $scope.calculatePadding = function() {
                  // $scope.numero = (window.innerWidth - 1280) / 2 + "px";
                }

                // $scope.numero = (window.innerWidth - 1280) / 2 + "px";

                console.log($scope.numero);

                angular.element($window).on('resize', $scope.calculatePadding());


            }
        };
    });
});
