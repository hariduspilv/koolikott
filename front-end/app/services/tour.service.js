
class TourService {
    constructor(serverCallService) {
        this.serverCallService = serverCallService;
    }

    isValidUserTourData(userTourData) {
        return !!userTourData && !!userTourData.user;
    }

    getUserTourData() {
        return this.serverCallService.makeGet('rest/userTourData', {})
            .then((response) => {
                return response.data
            })
    }

    setGeneralTourSeen(userTourData) {
        if(!this.isValidUserTourData(userTourData)) return;

        userTourData.generalTour = true;
        return this.adduserTourData(userTourData);
    }

    setEditTourSeen(userTourData) {
        if(!this.isValidUserTourData(userTourData)) return;

        userTourData.editTour = true;
        return this.adduserTourData(userTourData);
    }

    adduserTourData(userTourData) {
        return this.serverCallService.makePut('rest/userTourData', userTourData)
            .then((response) => {
                return response.data
            })
    }
}

angular.module('koolikottApp')
    .service('tourService', TourService);
