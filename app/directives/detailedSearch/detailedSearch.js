define(['app'], function(app)
{    
    app.directive('dopDetailedSearch', [ '$location', 'searchService', 'translationService', '$filter', 
     function($location, searchService, translationService, $filter, bsSwitch) {
        return {
            scope: true,
            templateUrl: 'app/directives/detailedSearch/detailedSearch.html',
            controller: function ($scope) {

                init();
                
                function init() {
                    $scope.detailedSearch = {};
                    
                    var validEducationalContexts = ['preschooleducation', 'basiceducation', 'secondaryeducation', 'vocationaleducation'];
                    var defaultEducationalContext = 'preschooleducation';

                    if (searchService.getEducationalContext() && validEducationalContexts.indexOf(searchService.getEducationalContext()) > -1) {
                        $scope.detailedSearch.educationalContext = searchService.getEducationalContext();
                    } else {
                        $scope.detailedSearch.educationalContext = defaultEducationalContext;
                    }
                }

                $scope.search = function() {
                    // Clear the search query if it has been set from simple search
                    searchService.setSearch('');

                    searchService.setEducationalContext($scope.detailedSearch.educationalContext);
                    $location.url(searchService.getURL());
                };
                
                $scope.filters = [];

                $scope.licenseTypes = [ {
                  "id" : 1,
                  "name" : "allRightsReserved"
                }, {
                  "id" : 2,
                  "name" : "CCBY"
                } ];

            }
        };
    }]);

    app.filter('licenseTypeFilter', function($filter) {
        return function(items, query) {
            var translationPrefix = 'LICENSETYPE_';
            items = $filter('translatableItemFilter')(items, query, translationPrefix);
            items = $filter('orderByTranslation')(items, translationPrefix);
            return items;
        }
    });
    
    app.filter('translatableItemFilter', function($filter) {
        return function(items, query, translationPrefix) {
            var out = [];

            if (angular.isArray(items) && query) {
                items.forEach(function(item) {
                    // Get translation
                    var translatedItem = $filter('translate')(translationPrefix + item.name.toUpperCase());

                    if (translatedItem.toLowerCase().indexOf(query.toLowerCase()) !== -1) {
                        out.push(item);
                    }
                });
            } else {
                out = items;
            }

            return out;
        }
    });

    app.filter('orderByTranslation', function($filter) {
        return function(items, translationPrefix) {

            if (angular.isArray(items)) {
                for (i = 0; i < items.length; i++) {
                    // Get translation
                    var translatedItem = $filter('translate')(translationPrefix + items[i].name.toUpperCase());

                    // Create temporary property
                    items[i].translation = translatedItem.toLowerCase();
                }

                // Sort alphabetically
                items = $filter('orderBy')(items, '-translation', true);

                // Remove translation property
                for (i = 0; i < items.length; i++) {
                    items[i].translation = null;
                }

            } 

            return items;
        }
    });
    
    return app;
});
