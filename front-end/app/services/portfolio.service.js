'use strict';

angular.module('koolikottApp')
    .factory('portfolioService', ['serverCallService', portfolioService]);

function portfolioService(serverCallService) {

    function getPortfolioById(id) {
        return serverCallService.makeGet("rest/portfolio", {'id': id})
            .then((response) => {
                if (response.data) {
                    return response.data;
                }
            });
    }

    function increaseViewCount(portfolio) {
        return serverCallService.makePost("rest/portfolio/increaseViewCount", portfolio)
            .then(response => {
                if (response.data) {
                    return response.data;
                }
            });
    }


    return {
        getPortfolioById: getPortfolioById,
        increaseViewCount: increaseViewCount
    }
}
