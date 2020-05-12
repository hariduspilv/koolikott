'use strict'

directive('dopSelectClose', ['$compile', ($compile) => ({
    restrict: 'A',
    link($scope, $element) {
        $compile(
            `<div data-ng-include="'/directives/selectClose/selectClose.html'"></div>`
        )($scope).appendTo(
            $element.find('md-select-menu[multiple]').parent()
        )
        angular.element(document).on('click', '.select__close', () =>
            angular.element('.md-select-backdrop').triggerHandler('click')
        )
        $scope.$on('$destroy', () =>
            angular.element(document).off('click', '.select__close')
        )
    }
})])
