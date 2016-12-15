'use strict'

angular.module('koolikottApp')
.directive('dopSelectClose',
[
    '$compile',
    function($compile) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                element.children('md-select-menu[multiple]').append($compile('<div data-ng-include="\'directives/selectClose/selectClose.html\'"></div>')(scope));
            },
            controller: function($scope) {
                $scope.closeSelect = function () {
                    angular.element(document.querySelector('.md-select-backdrop')).triggerHandler('click');
                }
            }
        };
    }
]);
