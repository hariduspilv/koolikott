'use strict';

angular.module('koolikottApp')
.controller('homeController',
[
    '$scope', 'serverCallService', '$rootScope', 'storageService', 'searchService',
    function ($scope, serverCallService, $rootScope, storageService, searchService) {
        storageService.setPortfolio(null);
        searchService.setQuery('')
        // With http cache on, in some cases page reload is necessary to update data
        $scope.cache = false;
        $scope.url = "rest/search";
        $scope.params = {
            'sort': 'default',
            'sortDirection': 'desc',
            'limit': 20,
            'type': 'all'
        };
    }
]);
