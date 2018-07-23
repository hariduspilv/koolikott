'use strict';
{
class controller extends Controller {

    getPortfolioById(id) {
        return this.serverCallService.makeGet("rest/portfolio", {'id': id})
            .then((response) => {
                if (response.data) return response.data;
            });
    }

    increaseViewCount(portfolio) {
        let viewCountParams = {
            'type': '.Portfolio',
            'id': portfolio.id
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
factory('portfolioService', controller)
}
