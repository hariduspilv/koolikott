define([
    'directives/infiniteSearchResult/infiniteSearchResult'
], function () {
    return ['$scope', '$route', 'authenticatedUserService',
        function ($scope, $route, authenticatedUserService) {
            function init() {
                $scope.url = "rest/portfolio/getByCreator";
                $scope.params = {
                    'maxResults': 15,
                    'username': authenticatedUserService.getUser().username
                };

                isMyProfilePage();
            }

            function isMyProfilePage() {
                var user = authenticatedUserService.getUser();
                if (user && $route.current.params.username === user.username) {
                    $scope.myProfile = true;
                }
            }

            init();
        }];
});
