'use strict'

{
class controller extends Controller {
    $onInit() {
        this.currentStep = -1 // disable tour on load
        this.isOpenedByUser = false
        this.$scope.$on('tour:start:firstTime', () => this.startGeneralTour())
        this.$scope.$on('tour:start', () => this.tourStart())
        this.$scope.$on('tour:start:editPage', () => this.tourStart(0, true, true))
        this.$scope.$on('tour:start:editPage:firstTime', () => this.tourStart(0, true, true, true))
        this.$scope.$on('tour:start:cancelled', () => this.tourStartCancelled())
        this.$scope.$on('tour:close', () => this.tourComplete())
        this.$scope.$on('tour:close:editPage', () => this.tourComplete(true))
        this.$scope.$on('tour:close:pageSwitch', () => this.tourComplete(false, true))
    }
    getUserTourData() {
        if (this.userTourData)
            return Promise.resolve(this.userTourData)

        if (!this.userDataPromise)
            this.userDataPromise = new Promise((fulfill, reject) =>
                this.$timeout(() =>
                    this.tourService
                        .getUserTourData()
                        .then(userTourData => {
                            this.userTourData = userTourData
                            fulfill(this.userTourData)
                        }, reject)
                )
            )

        return this.userDataPromise
    }
    tourStart(startStep = 0, isOpenedByUser = true, isEditPage = false, isFirstTime = false) {
        this.getUserTourData().then(userTourData => {
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
        this.$timeout(() => this.tourComplete(), 5000)
    }
    tourComplete(isEditPage = false, isPageSwitch = false) {
        this.currentStep = -1
        this.isOpenedByUser = false
        this.isCancelledTour = false

        if (!isPageSwitch)
            this.getUserTourData().then(userTourData => {
                if (this.authenticatedUserService.isAuthenticated())
                    this.tourService[
                        isEditPage
                            ? 'setEditTourSeen'
                            : 'setGeneralTourSeen'
                    ](userTourData)
                    .then(userTourData => this.userTourData = userTourData)
            })
    }
    startGeneralTour() {
        if (window.innerWidth >= BREAK_SM &&
            this.authenticatedUserService.isAuthenticated() &&
            !this.$rootScope.isEditPortfolioPage
        )
            this.getUserTourData().then(userTourData => {
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
