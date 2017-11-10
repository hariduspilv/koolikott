'use strict'

{
class controller extends Controller {
    $onInit() {
        this.currentStep = -1 // disable tour on load
        this.isOpenedByUser = false
        this.getUserTourData = this.tourService.getUserTourData()
        this.$scope.$on('tour:start:firstTime', this.startGeneralTour.bind(this))
        this.$scope.$on('tour:start', this.tourStart.bind(this))
        this.$scope.$on('tour:start:editPage', this.tourStart.bind(this, 0, true, true))
        this.$scope.$on('tour:start:editPage:firstTime', this.tourStart.bind(this, 0, true, true, true))
        this.$scope.$on('tour:start:cancelled', this.tourStartCancelled.bind(this))
        this.$scope.$on('tour:close', this.tourComplete.bind(this))
        this.$scope.$on('tour:close:editPage', this.tourComplete.bind(this, true))
        this.$scope.$on('tour:close:pageSwitch', this.tourComplete.bind(this, false, true))
    }
    tourStart(startStep = 0, isOpenedByUser = true, isEditPage = false, isFirstTime = false) {
        this.getUserTourData.then(userTourData => {
            if (isFirstTime && userTourData && userTourData.editTour)
                return

            this.isEditPageTour = isEditPage
            this.currentStep = startStep
            this.isOpenedByUser = isOpenedByUser
        })
    }
    tourStartCancelled() {
        this.isCancelledTour = true
        this.tourStart(0, false)
        this.$timeout(this.tourComplete.bind(this), 5000)
    }
    tourComplete(isEditPage = false, isPageSwitch = false) {
        this.currentStep = -1
        this.isOpenedByUser = false
        this.isCancelledTour = false

        const methodName = isEditPage ? 'setEditTourSeen' : 'setGeneralTourSeen'

        if (this.authenticatedUserService.isAuthenticated() && !isPageSwitch)
            this.getUserTourData.then(userTourData =>
                this.getUserTourData = this.tourService[methodName](userTourData)
            )
    }
    startGeneralTour() {
        if (window.innerWidth >= BREAK_SM &&
            this.authenticatedUserService.isAuthenticated() &&
            !this.$rootScope.isEditPortfolioPage
        )
            this.getUserTourData.then(userTourData => {
                if (!userTourData.generalTour)
                    this.$mdDialog.show({
                        templateUrl: 'directives/tour/modal/tour.modal.html',
                        controller: 'tourModalController',
                        controllerAs: '$ctrl'
                    })
            })
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$timeout',
    '$mdDialog',
    'tourService',
    'authenticatedUserService'
]
component('dopTour', {
    bindings: {},
    templateUrl: 'directives/tour/tour.html',
    controller
})
}
