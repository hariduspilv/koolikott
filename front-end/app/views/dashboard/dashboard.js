define([
    'directives/infiniteSearchResult/infiniteSearchResult'
], function () {
    return ['$scope', 'authenticatedUserService',
        function ($scope, authenticatedUserService) {

            function init() {
                var user = authenticatedUserService.getUser();
                if (user && user.userTaxons && user.userTaxons.length > 0) {
                    $scope.url = "rest/search";

                    var taxons = [];
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
        }];
});
