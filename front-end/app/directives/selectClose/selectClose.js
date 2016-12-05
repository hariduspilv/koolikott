define([
    'angularAMD'
], function (angularAMD) {
    angularAMD.directive('dopSelectClose', ['$compile', function($compile) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                element.find('md-select-menu[multiple]').append($compile('<div data-ng-include="\'directives/selectClose/selectClose.html\'"></div>')(scope));
            },
            controller: function($scope) {
                $scope.closeSelect = function () {
                    angular.element('.md-select-backdrop').triggerHandler('click');
                }
            }
        };
    }]);
});
