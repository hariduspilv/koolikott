'use strict';

angular.module('koolikottApp')
    .service('materialService', ['serverCallService' , 'authenticatedUserService', MaterialService]);

function MaterialService(serverCallService, authenticatedUserService) {

    function getMaterialById(id) {
        return serverCallService.makeGet("rest/material", {'materialId': id})
            .then((response) => {
                return response.data;
            });
    }

    function increaseViewCount(material) {
        let viewCountParams = {
            'type': '.Material',
            'id': material.id
        };

        return serverCallService.makePost("rest/material/increaseViewCount", viewCountParams)
            .then(response => {
                return response.data;
            });
    }

    function addComment(comment, material) {
        let params = {
            'comment': comment,
            'material': {
                'type': '.Material',
                'id': material.id
            }
        };

        return serverCallService.makePost("rest/comment/material", params)
            .then(response => {
                return response.data;
            });
    }

    return {
        getMaterialById: getMaterialById,
        increaseViewCount: increaseViewCount,
        addComment: addComment
    }
}
