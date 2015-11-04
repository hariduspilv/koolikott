define(['app'], function(app)
{    
    app.directive('dopDetailedSearch', [ '$location', 'searchService', 'translationService', '$filter', 
     function($location, searchService, translationService, $filter) {
        return {
            scope: {
                queryIn: '=',
                queryOut: '='
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

                function getTextFieldsAsQuery() {
                    var query = '';

                    if ($scope.detailedSearch.title) {
                        query += 'title:"' + $scope.detailedSearch.title + '"';
                    }
                    if ($scope.detailedSearch.combinedDescription) {
                        query += ' description:"' + $scope.detailedSearch.combinedDescription 
                            + '" summary:"' + $scope.detailedSearch.combinedDescription + '"';
                    }
                    if ($scope.detailedSearch.author) {
                        query += ' author:"' + $scope.detailedSearch.author + '"';
                    }

                    return query.trim();
                }

                function createSearchQuery() {
                    var query = '';
                    var textFields = getTextFieldsAsQuery();

                    if ($scope.detailedSearch.main) {
                        query = $scope.detailedSearch.main + ' ' + textFields;
                    } else {
                        query = textFields;
                    }
                    
                    return query.trim();
                }

                $scope.$watch('queryIn', function(queryIn) {
                    $scope.detailedSearch.main = queryIn;
                }, true);

                $scope.$watch('detailedSearch.main', function() {
                    $scope.queryOut = $scope.detailedSearch.main;
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
