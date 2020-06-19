'use strict'

{
class controller extends Controller {
    $onInit() {
        this.$scope.toggleSidenav = () => this.$mdSidenav('left').toggle()
        this.$mdSidenav('left', true).then(left =>
            this.$scope.isSideNavOpen = left.isOpen()
        )

        this.$scope.portfolio = this.storageService.getPortfolio()
        this.$scope.$watch(() => this.storageService.getPortfolio(), (newPortfolio) => {
            this.$scope.portfolio = newPortfolio
        })

        this.$scope.$watch(() => this.$location.path(), (path) => {
                this.$scope.isEditPortfolio = path === '/portfolio/edit' || path.contains('/kogumik/muuda')
            }
        )

        this.$scope.modifyMargin = () => {
            return !this.$rootScope.showCookieBanner && (this.$location.path().includes('/search/') || this.$location.path() === '/')
        }

    }
}
controller.$inject = [
    '$rootScope',
    '$scope',
    '$location',
    '$mdSidenav',
    'storageService'
]
component('dopColumnLayout', {
    templateUrl: '/directives/pageStructure/columnLayout/columnLayout.html',
    controller
})
}
