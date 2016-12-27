'use strict'

angular.module('koolikottApp')
.directive('dopColumnLayout',
function() {
    return {
        scope: true,
        templateUrl: 'directives/pageStructure/columnLayout/columnLayout.html',
        controller: ['$scope', '$rootScope', '$mdSidenav', '$window', 'storageService', function($scope, $rootScope, $mdSidenav, $window, storageService) {
            $scope.toggleSidenav = function() {
                $mdSidenav('left').toggle();
            };

            $scope.sidenavIsOpen = function() {
                return $mdSidenav('left').isOpen();
            };

            $scope.$watch(function() {
                return storageService.getPortfolio();
            }, function(newPortfolio, oldPortfolio) {
                $scope.portfolio = newPortfolio;
            });

            $scope.portfolio = storageService.getPortfolio();
        }]
    };
});
