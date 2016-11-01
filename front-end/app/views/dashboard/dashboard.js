define([
    'directives/infiniteSearchResult/infiniteSearchResult',
    'views/dashboard/improper/material/improperMaterial',
    'views/dashboard/improper/portfolio/improperPortfolio',
    'views/dashboard/broken/material/brokenMaterial',
    'views/dashboard/deleted/material/deletedMaterial',
    'views/dashboard/deleted/portfolio/deletedPortfolio'
], function() {
    return ['$scope', 'authenticatedUserService',
        function ($scope, authenticatedUserService) {

            function init() {
                user = authenticatedUserService.getUser();
                if (user && user.userTaxons) {
                    $scope.url = "rest/search";

                    var taxons = [];
                    user.userTaxons.forEach(function(entry) {
                        taxons.push(entry.id);
                    });

                    $scope.params = {
                        'taxon': taxons,
                        'sort': 'added',
                        'sortDirection': 'desc'
                    };
                }
            }

            init();
        }];
});
