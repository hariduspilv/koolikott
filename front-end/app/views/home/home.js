'use strict'

angular.module('koolikottApp')
.controller('homeController',
[
    '$scope', 'serverCallService', '$rootScope',
    function ($scope, serverCallService, $rootScope) {
        $rootScope.savedPortfolio = null;
        $scope.url = "rest/search";
        $scope.params = {
            'sort': 'added',
            'sortDirection': 'desc',
            'limit': 20,
            'type': 'all'
        };
    }
]);
