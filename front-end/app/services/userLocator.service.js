class controller extends Controller {
    constructor(...args) {
        super(...args)
        this.updateTimer = undefined;
    }

    saveUserLocation() {
        if (this.authenticatedUserService.isAuthenticated())
            this.serverCallService.makePost('rest/user/saveLocation', {location: this.$location.url()})
    }

    getUserLocation() {
        return this.serverCallService.makeGet('rest/user/getLocation')
    }

    startTimer() {
        this.updateTimer = this.$interval(this.saveUserLocation.bind(this), 60e3) //1 min
    }

    stopTimer() {
        this.$interval.cancel(this.updateTimer)
    }
}

controller.$inject = [
    '$interval',
    '$location',
    'serverCallService',
    'authenticatedUserService',
]
factory('userLocatorService', controller)
