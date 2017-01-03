'use strict';

angular.module('koolikottApp')
    .factory('materialService', ['serverCallService', materialService]);

function materialService(serverCallService) {

    function getMaterialById(id) {
        return serverCallService.makeGet("rest/material", {'materialId': id})
            .then((response) => {
                if (response.data) {
                    return response.data;
                }
            });
    }

    return {
        getMaterialById: getMaterialById
    }
}
