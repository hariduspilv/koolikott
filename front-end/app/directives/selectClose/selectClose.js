'use strict'

{
class controller extends Controller {
    $onInit() {
        this.$scope.closeSelect = () =>
            angular.element(
                document.querySelector('.md-select-backdrop')
            ).triggerHandler('click')
    }
}
controller.$inject = ['$scope']
directive('dopSelectClose', ['$compile', ($compile) => ({
    restrict: 'A',
    link($scope, $element) {
        const $closeBtn = $compile('<div data-ng-include="\'directives/selectClose/selectClose.html\'"></div>')($scope)
        $element.find('md-select-menu[multiple]').append($closeBtn)
    },
    controller
})])
}
