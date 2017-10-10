'use strict'

{
class controller extends Controller {
    $onInit() {
        this.$scope.isOpen = false
        this.$scope.userHasSelectedMaterials = false

        this.$rootScope.$watch('selectedMaterials.length', (newValue) => {
            this.$scope.userHasSelectedMaterials = newValue > 0
        }, false)

        this.$rootScope.$watch('selectedSingleMaterial', (newValue) => {
            this.$scope.userHasSelectedMaterials = newValue !== null && newValue !== undefined
        }, false)

        this.$scope.showAddPortfolioDialog = ($event) => {
            $event.preventDefault()

            const emptyPortfolio = createPortfolio()

            if (this.$scope.userHasSelectedMaterials || this.$rootScope.selectedSingleMaterial) {
                emptyPortfolio.chapters = []
                emptyPortfolio.chapters.push({
                    title: '',
                    subchapters: [],
                    materials: []
                })

                if (this.$rootScope.selectedMaterials && this.$rootScope.selectedMaterials.length > 0)
                    for (var i = 0; i < this.$rootScope.selectedMaterials.length; i++)
                        emptyPortfolio.chapters[0].contentRows.push({
                            learningObjects: [this.$rootScope.selectedMaterials[i]]
                        })
                
                else if(this.$rootScope.selectedSingleMaterial != null)
                    emptyPortfolio.chapters[0].contentRows = (emptyPortfolio.chapters[0].contentRows || []).concat({
                        learningObjects: [this.$rootScope.selectedSingleMaterial]
                    })

                this.toastService.showOnRouteChange('PORTFOLIO_ADD_MATERIAL_SUCCESS')
            }

            this.storageService.setEmptyPortfolio(emptyPortfolio)
            this.$rootScope.newPortfolioCreated = true
            this.$mdDialog.show({
                templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                controller: 'addPortfolioDialogController',
                fullscreen: false
            })
        }

        this.$scope.showAddMaterialDialog = () =>
            this.$mdDialog.show({
                templateUrl: 'addMaterialDialog.html',
                controller: 'addMaterialDialogController'
            })

        this.$scope.copyPortfolio = () =>
            serverCallService
                .makePost('rest/portfolio/copy', createPortfolio(this.$route.current.params.id))
                .then(({ portfolio }) => {
                    if (portfolio) {
                        this.storageService.setPortfolio(portfolio)
                        this.$rootScope.openMetadataDialog = true
                        this.$mdDialog.hide()
                        this.$location.url('/portfolio/edit?id=' + portfolio.id)
                    }
                })

        this.$scope.hasPermission = () =>
            this.authenticatedUserService.getUser() && !this.authenticatedUserService.isRestricted()

        this.$scope.setFabState = (state) => {
            if (!('ontouchstart' in window || window.DocumentTouch && document instanceof DocumentTouch))
                this.$scope.isOpen = state
        }

        this.$scope.isViewPortforlioPage = this.$rootScope.isViewPortforlioPage
    }
}
controller.$inject = [
    '$scope',
    '$location',
    '$rootScope',
    '$route',
    '$mdDialog',
    'authenticatedUserService',
    'serverCallService',
    'storageService',
    'toastService'
]

angular.module('koolikottApp').component('dopMainFabButton', {
    templateUrl: 'directives/mainFabButton/mainFabButton.html',
    controller
})
}
