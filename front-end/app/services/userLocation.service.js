'use strict';
{
class controller extends Controller {

    $onInit() {
        debugger
        getUser()
    }

    getUser() {
        return this.authenticatedUserService.getAuthenticatedUser()
    }

    postUserLocation() {

        const location = window.location.href

        this.serverCallService.makePost('rest/user/saveLocation', {location : location})
        console.log('tictoc')
    }

    getUserLocation() {
        return this.serverCallService.makeGet('rest/user/getLocation').then((response) => {
            if (response.data) return response.data;
        });
    }
}

controller.$inject = [
    '$rootScope',
    'serverCallService',
    'authenticatedUserService',
    'storageService'
]
factory('userLocationService', controller)
}
