'use strict'

angular.module('koolikottApp')
.directive('dopColumnLayout',
function() {
    return {
        scope: true,
        templateUrl: 'directives/pageStructure/columnLayout/columnLayout.html',
        controller: ['$scope', '$rootScope', '$mdSidenav', '$window', 'storageService', '$timeout', function($scope, $rootScope, $mdSidenav, $window, storageService, $timeout) {
            $scope.toggleSidenav = function() {
                $mdSidenav('left').toggle();
            };

            $scope.sidenavIsOpen = function() {
                return $mdSidenav('left').isOpen();
            };
        }]
    };
});
