define([
    'app',
    'directives/portfolioBox/portfolioBox',
    'services/serverCallService'
], function (app) {
    return ['$scope', '$route', 'authenticatedUserService', 'serverCallService',
        function ($scope, $route, authenticatedUserService, serverCallService) {
            function init() {
                getUserPortfolios();
                isMyProfilePage();
            }

            function getUserPortfolios() {
                var params = {
                    'username': authenticatedUserService.getUser().username
                };
                serverCallService.makeGet("rest/portfolio/getByCreator", params, getPortfoliosSuccess, getDataFailed)
            }

            function getPortfoliosSuccess(data) {
                if (data) {
                    $scope.portfolios = data
                }
            }

            function getDataFailed() {
                console.log("Failed to get data")
            }

            function isMyProfilePage() {
                if (authenticatedUserService.isAuthenticated()) {
                    var user = authenticatedUserService.getUser()
                    if (user && $route.current.params.username === user.username) {
                        $scope.myProfile = true;
                    }
                }
            }

            init();
        }];
});
