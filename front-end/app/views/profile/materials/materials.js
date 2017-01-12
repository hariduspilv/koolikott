'use strict'

angular.module('koolikottApp')
.controller('userMaterialsController',
[
    '$scope', '$route', 'authenticatedUserService',
    function ($scope, $route, authenticatedUserService) {
        function init() {
            $scope.cache = false;
            $scope.url = "rest/material/getByCreator";
            $scope.params = {
                'maxResults': 20,
                'username': authenticatedUserService.getUser().username
            };

            setTitle();
        }

        function setTitle() {
            var user = authenticatedUserService.getUser();
            if (user && $route.current.params.username === user.username) {
                $scope.title = 'MYPROFILE_PAGE_TITLE_MATERIALS';
            } else {
                $scope.title = 'PROFILE_PAGE_TITLE_MATERIALS';
            }
        }

        init();
    }
]);
