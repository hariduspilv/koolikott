'use strict';

angular.module('koolikottApp')
    .service('materialService', ['serverCallService' , 'authenticatedUserService', MaterialService]);

function MaterialService(serverCallService, authenticatedUserService) {

    function getMaterialById(id) {
        return serverCallService.makeGet("rest/material", {id})
            .then((response) => {
                return response.data;
            });
    }

    function increaseViewCount(material) {
        let viewCountParams = {
            'type': '.Material',
            'id': material.id
        };
        //todo ips unify with portfolio increaseViewCount
        return serverCallService.makePost("rest/learningObject/increaseViewCount", viewCountParams)
            .then(response => {
                return response.data;
            });
    }

    return {
        getMaterialById: getMaterialById,
        increaseViewCount: increaseViewCount
    }
}
