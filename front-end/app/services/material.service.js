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

    function deleteMaterial(material) {
        let url = "rest/material/" + material.id;
       return serverCallService.makeDelete(url, {})
            .then(response => {
                return response.data;
            });
    }

    function setNotImproper(material) {
        if (authenticatedUserService.isAdmin() && material) {
            let url = "rest/admin/improper?learningObject=" + material.id;
            return serverCallService.makeDelete(url, {})
                .then(response => {
                    return response.data;
                });
        }
    }

    function restoreMaterial(material) {
        return serverCallService
            .makePost('rest/admin/deleted/material/restore', material)
            .then(response => response.data)
    }

    function setMaterialCorrect(material) {
        return serverCallService
            .makePost('rest/admin/brokenContent/setNotBroken', material)
            .then(response => response.data)
    }

    function markReviewed(material) {
        return !material || (
            !authenticatedUserService.isAdmin() &&
            !authenticatedUserService.isModerator()
            )
            ? Promise.reject()
            : serverCallService
                .makePost('rest/admin/firstReview/setReviewed', material)
                .then(function (response) {
                    return response.data
                })
    }

    return {
        getMaterialById: getMaterialById,
        increaseViewCount: increaseViewCount,
        addComment: addComment,
        deleteMaterial: deleteMaterial,
        setNotImproper: setNotImproper,
        restoreMaterial: restoreMaterial,
        setMaterialCorrect: setMaterialCorrect,
        markReviewed: markReviewed
    }
}
