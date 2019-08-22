'use strict'

angular.module('koolikottApp')
.controller('userFavoritesController',
[
    '$scope', '$location', 'authenticatedUserService',
    function ($scope, $location, authenticatedUserService) {
        $scope.cache = false;
        $scope.url = "rest/learningObject/usersFavorite";
        $scope.params = {
            'maxResults': 20,
            'username': authenticatedUserService.getUser().username
        };
        $location.url(`/${$scope.params.username}/lemmikud`)
    }
]);

