'use strict'

{
class controller extends Controller {
    $onInit() {
        this.$scope.toggleSidenav = () => this.$mdSidenav('left').toggle()
        this.$scope.sidenavIsOpen = () => this.$mdSidenav('left').isOpen()

        this.$scope.portfolio = this.storageService.getPortfolio()
        this.$scope.$watch(() => this.storageService.getPortfolio(), (newPortfolio) => {
            this.$scope.portfolio = newPortfolio
        })
    }
}
controller.$inject = ['$scope', '$mdSidenav', 'storageService']

angular.module('koolikottApp').component('dopColumnLayout', {
    templateUrl: 'directives/pageStructure/columnLayout/columnLayout.html',
    controller
})
}
