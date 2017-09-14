'use strict';

angular.module('koolikottApp')
.controller('homeController',
[
    '$scope', 'serverCallService', '$rootScope', 'storageService',
    function ($scope, serverCallService, $rootScope, storageService) {
        storageService.setPortfolio(null);
        // With http cache on, in some cases page reload is necessary to update data
        $scope.cache = false;
        $scope.url = "rest/search";
        $scope.params = {
            'sort': 'added',
            'sortDirection': 'desc',
            'limit': 20,
            'type': 'all'
        };
    }
]);
