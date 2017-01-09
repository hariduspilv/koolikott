'use strict';

angular.module('koolikottApp')
    .factory('changedLearningObjectService', ['$rootScope', 'serverCallService', 'storageService', changedLearningObjectService]);

function changedLearningObjectService($rootScope, serverCallService, storageService) {

    function updateLearningObject(learningObject) {
        if (isMaterial(learningObject.type)) {
            storageService.setMaterial(learningObject);
        } else if (isPortfolio(learningObject.type)) {
            storageService.setPortfolio(learningObject);
        }
    }

    return {
        getChangedList: function () {
            return serverCallService.makeGet("rest/changed", {})
                .then((response) => {
                    return response.data;
                });
        },

        getChangedData: function (learningObjectId) {
            if (learningObjectId) {
                let queryUrl = "rest/changed/" + learningObjectId;
                return serverCallService.makeGet(queryUrl, {})
                    .then((response) => {
                        return response.data
                    });
            }
        },

        acceptChanges: function (learningObjectId) {
            if (learningObjectId) {
                serverCallService.makeGet("rest/changed/" + learningObjectId + "/acceptAll", {})
                    .then((response) => {
                        $rootScope.learningObjectChanged = false;
                    });
            }
        },

        revertChanges: function (learningObjectId) {
            if (learningObjectId) {
                serverCallService.makeGet("rest/changed/" + learningObjectId + "/revertAll", {})
                    .then((response) => {
                        updateLearningObject(response.data);
                        $rootScope.learningObjectChanged = false;
                    });
            }
        }
    }
}
