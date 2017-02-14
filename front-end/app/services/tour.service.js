function TourService(serverCallService) {

    function isValidUserTourData(userTourData) {
        return !!userTourData && !!userTourData.user;
    }

    function addUserTourData(userTourData) {
        return serverCallService.makePut('rest/userTourData', userTourData)
            .then((response) => {
                return response.data;
            });
    }

    return {
        getUserTourData() {
            return serverCallService.makeGet('rest/userTourData', {})
                .then((response) => {
                    return response.data;
                });
        },

        setGeneralTourSeen(userTourData) {
            if(!isValidUserTourData(userTourData)) return;

            userTourData.generalTour = true;
            return addUserTourData(userTourData);
        },

        setEditTourSeen(userTourData) {
            if(!isValidUserTourData(userTourData)) return;

            userTourData.editTour = true;
            return addUserTourData(userTourData);
        }
    }
}

angular.module('koolikottApp')
    .service('tourService', ['serverCallService', TourService]);
