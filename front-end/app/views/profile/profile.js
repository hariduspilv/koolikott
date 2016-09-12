define([
    'app',
    'directives/materialBox/materialBox',
    'directives/portfolioBox/portfolioBox',
    'services/authenticatedUserService',
    'services/serverCallService',
    'services/alertService'
], function (app) {
    return ['$scope', '$route', 'authenticatedUserService', 'serverCallService', '$location', 'alertService', function ($scope, $route, authenticatedUserService, serverCallService, $location, alertService) {
        function init() {
            isMyProfilePage();

            if (!$scope.user) {
                getUser();
            }

            if ($route.current.params.username) {
                getUsersMaterials();
                getUsersPortfolios();
            }
        }

        function isMyProfilePage() {
            if (authenticatedUserService.isAuthenticated()) {
                var user = authenticatedUserService.getUser()

                if (user && $route.current.params.username === user.username) {
                    $scope.user = user;
                    $scope.myProfile = true;
                }
            }
        }

        function getUser() {
            var params = {
                'username': $route.current.params.username
            };
            var url = "rest/user";
            serverCallService.makeGet(url, params, getUserSuccess, getUserFail);
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
            var params = {
                'username': $route.current.params.username
            };
            var url = "rest/material/getByCreator";
            serverCallService.makeGet(url, params, getUsersMaterialsSuccess, getUsersMaterialsFail);
        }

        function getUsersMaterialsSuccess(data) {
            if (isEmpty(data)) {
                getUsersMaterialsFail();
            } else {
                $scope.materials = data;
            }
        }

        function getUsersMaterialsFail() {
            console.log('Failed to get materials.');
        }

        function getUsersPortfolios() {
            var params = {
                'username': $route.current.params.username
            };
            var url = "rest/portfolio/getByCreator";
            serverCallService.makeGet(url, params, getUsersPortfoliosSuccess, getUsersPortfoliosFail);
        }

        function getUsersPortfoliosSuccess(data) {
            if (isEmpty(data)) {
                getUsersPortfoliosFail();
            } else {
                $scope.portfolios = data;
            }
        }

        function getUsersPortfoliosFail() {
            console.log('Failed to get portfolios.');
        }

        init();
    }];
});
