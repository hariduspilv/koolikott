'use strict'

directive('dopSelectClose', ['$compile', ($compile) => ({
    restrict: 'A',
    scope: true,
    link($scope, $element) {
        $scope.closeSelect = () =>
            angular.element(
                document.querySelector('.md-select-backdrop')
            ).triggerHandler('click')
        const $closeBtn = $compile('<div data-ng-include="\'directives/selectClose/selectClose.html\'"></div>')($scope)
        $element.find('md-select-menu[multiple]').append($closeBtn)
    },
})])
