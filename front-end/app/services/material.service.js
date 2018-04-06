'use strict';

angular.module('koolikottApp')
    .service('materialService', ['serverCallService' , MaterialService]);

function MaterialService(serverCallService) {

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
        return serverCallService.makePost("rest/learningObject/increaseViewCount", viewCountParams)
            .then(response => {
                if (response.data){
                    return response.data;
                }
            });
    }

    return {
        getMaterialById: getMaterialById,
        increaseViewCount: increaseViewCount
    }
}
