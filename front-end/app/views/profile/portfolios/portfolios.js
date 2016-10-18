define([
    'app',
    'directives/portfolioBox/portfolioBox',
    'services/serverCallService'
], function (app) {
    return ['$scope', '$route', 'authenticatedUserService', 'serverCallService',
        function ($scope, $route, authenticatedUserService, serverCallService) {
        function init() {
            getUserPortfolios();
        }

        function getUserPortfolios() {
            var params = {
                'username': authenticatedUserService.getUser().username
            }
            serverCallService.makeGet("rest/portfolio/getByCreator", params, getPortfoliosSuccess, getDataFailed)
        }

        function getPortfoliosSuccess(data) {
            if(data) {
                $scope.portfolios = data
            }
        }

        function getDataFailed() {
            console.log("Failed to get data")
        }

        init();
    }];
});
