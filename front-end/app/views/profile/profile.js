define([
    'app',
    'directives/materialBox/materialBox',
    'directives/portfolioBox/portfolioBox',
    'services/authenticatedUserService',
    'services/serverCallService',
    'services/alertService',
    'services/userDataService'
], function (app) {
    return ['$scope', '$route', 'authenticatedUserService', 'serverCallService', '$location', 'alertService', 'userDataService', function ($scope, $route, authenticatedUserService, serverCallService, $location, alertService, userDataService) {
        function init() {
            isMyProfilePage();

            if (!$scope.user) {
                getUser();
            }

            if (!authenticatedUserService.isAuthenticated() && $route.current.params.username) {
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

        userDataService.loadUserPortfolios(function(callback) {
            $scope.portfolios = callback;
        });
        userDataService.loadUserMaterials(function(callback) {
            $scope.materials = callback;
        });
        userDataService.loadUserFavorites(function(callback) {
            $scope.favorites = callback;
        });


        /*function getUsersFavorites() {
            serverCallService.makeGet("rest/learningObject/usersFavorite", {}, getFavoritesSuccess, getFavoritesFail)
        }

        function getFavoritesSuccess(data) {
            if(data) {
                $scope.favorites = data
            }
        }

        function getFavoritesFail() {
            console.log("failed to retrieve learning objects favorited by the user")
        }
*/

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
