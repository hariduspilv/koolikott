'use strict'

angular.module('koolikottApp')
.controller('userFavoritesController',
[
    '$scope',
    function ($scope) {
        $scope.cache = false;
        $scope.url = "rest/learningObject/usersFavorite";
        $scope.params = {
            'maxResults': 20
        };
    }
]);
