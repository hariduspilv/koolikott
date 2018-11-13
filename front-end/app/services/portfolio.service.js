'use strict';
{
class controller extends Controller {

    getPortfolioById(id) {
        return this.serverCallService.makeGet("rest/portfolio", {'id': id})
            .then((response) => {
                if (response.data) return response.data;
            });
    }

}

controller.$inject = [
    'serverCallService',
]
factory('portfolioService', controller)
}
