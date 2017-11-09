'use strict'

directive('dopSelectClose', ['$compile', ($compile) => ({
    restrict: 'A',
    link($scope, $element) {
        $compile(
            `<div data-ng-include="'directives/selectClose/selectClose.html'"></div>`
        )($scope).appendTo(
            $element.find('md-select-menu[multiple]').parent()
        )
        setTimeout(() => {
            const $closeBtn = $element.find('md-select-menu[multiple]').parent().find('.select__close')
            $closeBtn.on('click', () => angular.element('.md-select-backdrop').triggerHandler('click'))
            $scope.$on('$destroy', () => $closeBtn.off())
        })
    }
})])
