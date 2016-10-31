define([
    'app',
    'directives/materialBox/materialBox',
    'directives/portfolioBox/portfolioBox',
    'services/authenticatedUserService',
    'services/serverCallService',
    'services/alertService'
], function (app) {
    return ['$scope', '$route', 'authenticatedUserService', 'serverCallService', '$location', 'alertService',
        function ($scope, $route, authenticatedUserService, serverCallService, $location, alertService) {
            var params;

            function init() {
                if (!$scope.user) getUser();

                params = {
                    'username': $route.current.params.username,
                    'maxResults': 1000
                };

                if ($route.current.params.username) {
                    getUsersMaterials();
                    getUsersPortfolios();
                }
            }

            function getUser() {
                var userParams = {
                    'username': $route.current.params.username
                };
                var url = "rest/user";
                serverCallService.makeGet(url, userParams, getUserSuccess, getUserFail);
            }

            function getUserSuccess(user) {
                if (isEmpty(user)) {
                    getUserFail();
                } else {
                    $scope.user = user;
                }
            }

            function getUserFail() {
                console.log('Getting user failed.');
                $location.url('/');
            }

            function getUsersMaterials() {
                var url = "rest/material/getByCreator";
                serverCallService.makeGet(url, params, getUsersMaterialsSuccess, getUsersMaterialsFail);
            }

            function getUsersMaterialsSuccess(data) {
                if (isEmpty(data)) {
                    getUsersMaterialsFail();
                } else {
                    $scope.materials = data.items;
                }
            }

            function getUsersMaterialsFail() {
                console.log('Failed to get materials.');
            }

            function getUsersPortfolios() {
                var url = "rest/portfolio/getByCreator";
                serverCallService.makeGet(url, params, getUsersPortfoliosSuccess, getUsersPortfoliosFail);
            }

            function getUsersPortfoliosSuccess(data) {
                if (isEmpty(data)) {
                    getUsersPortfoliosFail();
                } else {
                    $scope.portfolios = data.items;
                }
            }

            function getUsersPortfoliosFail() {
                console.log('Failed to get portfolios.');
            }

            init();
        }];
});
