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
        getChangedData: function (learningObjectId) {
            if (learningObjectId) {
                var queryUrl = "rest/changed/" + learningObjectId;
                return serverCallService.makeGet(queryUrl, {})
                    .then(function (response) {
                        return response.data
                    });
            }
        },

        acceptChanges: function (learningObjectId) {
            if (learningObjectId) {
                serverCallService.makeGet("rest/changed/" + learningObjectId + "/acceptAll", {})
                    .then(function (response) {
                        $rootScope.learningObjectChanged = false;
                    });
            }
        },

        revertChanges: function (learningObjectId) {
            if (learningObjectId) {
                serverCallService.makeGet("rest/changed/" + learningObjectId + "/revertAll", {})
                    .then(function (response) {
                        updateLearningObject(response.data);
                        $rootScope.learningObjectChanged = false;
                    });
            }
        }
    }
}
