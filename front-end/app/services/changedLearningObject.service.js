'use strict';

angular.module('koolikottApp').factory('changedLearningObjectService', [
    '$rootScope',
    'serverCallService',
    'storageService', (
        $rootScope,
        serverCallService,
        storageService
    ) => ({
        getChangedList() {
            return serverCallService
                .makeGet('rest/admin/changed', {})
                .then(response => response.data)
        },
        getChangedData(learningObjectId) {
            if (learningObjectId)
                return serverCallService
                    .makeGet('rest/admin/changed/'+learningObjectId, {})
                    .then(response => response.data)
        },
        acceptChanges(learningObjectId) {
            if (learningObjectId)
                serverCallService
                    .makeGet(`rest/admin/changed/${learningObjectId}/acceptAll`, {})
                    .then(() => $rootScope.learningObjectChanged = false)
        },
        revertChanges(learningObjectId) {
            if (learningObjectId)
                serverCallService
                    .makeGet(`rest/admin/changed/${learningObjectId}/revertAll`, {})
                    .then(response => {
                        if (isMaterial(response.data.type))
                            storageService.setMaterial(response.data)
                        else if (isPortfolio(response.data.type))
                            storageService.setPortfolio(response.data)

                        $rootScope.learningObjectChanged = false
                    })
        }
    })
])
