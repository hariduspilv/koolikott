'use strict'

angular.module('koolikottApp')
.directive('dopColumnLayout',
function() {
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
        }
    };
});
