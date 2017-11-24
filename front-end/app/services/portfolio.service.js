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

    //todo ips unify with material increaseViewCount
    function increaseViewCount(portfolio) {
        return serverCallService.makePost("rest/learningObject/increaseViewCount", portfolio)
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
