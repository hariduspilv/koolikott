'use strict'

angular.module('koolikottApp')
.controller('homeController',
[
    '$scope', 'serverCallService', '$rootScope', 'storageService',
    function ($scope, serverCallService, $rootScope, storageService) {
        storageService.setPortfolio(null);
        $scope.url = "rest/search";
        $scope.params = {
            'sort': 'added',
            'sortDirection': 'desc',
            'limit': 20,
            'type': 'all'
        };
    }
]);
