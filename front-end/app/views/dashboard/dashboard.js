'use strict'

angular.module('koolikottApp')
.controller('dashboardController',
[
    '$scope', 'authenticatedUserService',
    function ($scope, authenticatedUserService) {

        function init() {
            const user = authenticatedUserService.getUser()
            if (user && user.userTaxons && user.userTaxons.length > 0) {
                $scope.url = "rest/search";

                let taxons = []
                user.userTaxons.forEach(function (entry) {
                    taxons.push(entry.id);
                });

                $scope.params = {
                    'taxon': taxons,
                    'sort': 'added',
                    'sortDirection': 'desc',
                    'limit': 15
                };
            }
        }

        init();
    }
]);
