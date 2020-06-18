'use strict';
{
class controller extends Controller {

    getMaterialById(id) {
        return this.serverCallService.makeGet("rest/material", {'id': id})
            .then((response) => {
                if (response.data) return response.data;
            });
    }

    getMaterialLdJsonById(id) {
        return this.serverCallService.makeGet('rest/material/ldJson', {'id': id})
            .then((response) => {
                if (response.data) return response.data;
            });
    }

    getRelatedPortfolios(id) {
        return this.serverCallService.makeGet("rest/material/getRelatedPortfolios", {'id': id})
    }

    increaseViewCount(material) {
        let viewCountParams = {
            'type': '.Material',
            'id': material.id
        };
        return this.serverCallService.makePost("rest/learningObject/increaseViewCount", viewCountParams)
            .then(response => {
                if (response.data) {
                    return response.data;
                }
            });
    }
}

controller.$inject = [
    'serverCallService',
]
factory('materialService', controller)
}
