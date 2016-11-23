define([
    'angularAMD'
], function (angularAMD) {
    angularAMD.directive('dopSelectClose', [function() {
        return {
            restrict: 'A',
            compile: function(element, attrs) {
                element.append('<div data-ng-include="\'directives/selectClose/selectClose.html\'" class="select__close"></div>');
            },
            controller: function($scope) {
                $scope.closeSelect = function () {
                    angular.element('.md-select-backdrop').triggerHandler('click');
                }
            }
        };
    }]);
});
