define(['app'], function(app)
{    
    app.directive('dopDetailedSearch', [ '$location', 'searchService', 'translationService', '$filter', 
     function($location, searchService, translationService, $filter) {
        return {
            scope: {
                visible: '='
            },
            templateUrl: 'app/directives/detailedSearch/detailedSearch.html',
            controller: function ($scope) {

                init();
                
                function init() {

                    // Test data
                    $scope.filters = [];
                    $scope.licenseTypes = [ {
                      "id" : 1,
                      "name" : "allRightsReserved"
                    }, {
                      "id" : 2,
                      "name" : "CCBY"
                    } ];

                    // Detailed search fields
                    $scope.detailedSearch = {};
                    
                    // Educational context
                    var validEducationalContexts = ['preschooleducation', 'basiceducation', 'secondaryeducation', 'vocationaleducation'];
                    var defaultEducationalContext = 'preschooleducation';

                    if (searchService.getEducationalContext() && validEducationalContexts.indexOf(searchService.getEducationalContext()) > -1) {
                        $scope.detailedSearch.educationalContext = searchService.getEducationalContext();
                    } else {
                        $scope.detailedSearch.educationalContext = defaultEducationalContext;
                    }

                    // Paid
                    if (searchService.isPaid() && (searchService.isPaid() === 'true' || searchService.isPaid() === 'false')) {
                        $scope.detailedSearch.paid = searchService.isPaid();
                    } else {
                        $scope.detailedSearch.paid = 'true';
                    }

                    // Type
                    $scope.detailedSearch.type = '';
                    if (searchService.getType()) {
                        if (searchService.getType() === 'material') {
                            $scope.detailedSearch.type = 'material'
                        } else if (searchService.getType() === 'portfolio') {
                            $scope.detailedSearch.type = 'portfolio';
                        }
                    }

                }

                $scope.search = function() {
                    searchService.setSearch(createSearchQuery($scope.detailedSearch.main));
                    
                    searchService.setPaid($scope.detailedSearch.paid);
                    searchService.setType($scope.detailedSearch.type);
                    searchService.setEducationalContext($scope.detailedSearch.educationalContext);

                    $location.url(searchService.getURL());
                };

                function getFiltersAsQuery() {
                    var filters = '';

                    if ($scope.detailedSearch.title) {
                        filters += isEmpty(filters) ? '' : ' AND ';
                        filters += 'title:"' + $scope.detailedSearch.title + '"';
                    }
                    if ($scope.detailedSearch.combinedDescription) {
                        filters += isEmpty(filters) ? '' : ' AND ';
                        filters += '(description:"' + $scope.detailedSearch.combinedDescription 
                            + '" OR summary:"' + $scope.detailedSearch.combinedDescription + '")';
                    }
                    if ($scope.detailedSearch.author) {
                        filters += isEmpty(filters) ? '' : ' AND ';
                        filters += 'author:"' + $scope.detailedSearch.author + '"';
                    }

                    return filters;
                }

                function createSearchQuery() {
                    var query = '';
                    var filters = getFiltersAsQuery();

                    if ($scope.detailedSearch.main) {
                        if (filters) {
                            query = '(' + $scope.detailedSearch.main + ') AND ' + filters;
                        } else {
                            query = $scope.detailedSearch.main;
                        }
                    } else if (filters) {
                        query = filters;
                    }
                    
                    return query;
                }

                // Move search query between simple search box and detailed search
                $scope.$watch('visible', function(newValue, oldValue) {
                    if (newValue != oldValue) {
                        if ($scope.visible) {
                            $scope.detailedSearch.main = searchService.getQuery();
                            searchService.setSearch(null);
                        } else {
                            searchService.setSearch($scope.detailedSearch.main);
                            $scope.detailedSearch.main = '';
                        }
                    } 
                }, true);

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
