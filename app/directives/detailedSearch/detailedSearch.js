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
                    searchService.setSearch(createSimpleSearchQuery(true, true, true));
                    
                    searchService.setPaid($scope.detailedSearch.paid);
                    searchService.setType($scope.detailedSearch.type);
                    searchService.setEducationalContext($scope.detailedSearch.educationalContext);

                    $location.url(searchService.getURL());
                };

                function getTextFieldsAsQuery(usingTitle, usingDescription, usingAuthor) {
                    var query = '';

                    if (usingTitle && $scope.detailedSearch.title) {
                        query += 'title:' + addQuotesIfHasWhitespace($scope.detailedSearch.title);
                    }
                    if (usingDescription && $scope.detailedSearch.combinedDescription) {
                        query += ' description:' + addQuotesIfHasWhitespace($scope.detailedSearch.combinedDescription)
                            + ' summary:' + addQuotesIfHasWhitespace($scope.detailedSearch.combinedDescription);
                    }
                    if (usingAuthor && $scope.detailedSearch.author) {
                        query += ' author:' + addQuotesIfHasWhitespace($scope.detailedSearch.author);
                    }

                    return query.trim();
                }

                function createSimpleSearchQuery(usingTitle, usingDescription, usingAuthor) {
                    var query = '';
                    var textFields = getTextFieldsAsQuery(usingTitle, usingDescription, usingAuthor);

                    if ($scope.detailedSearch.main) {
                        query = $scope.detailedSearch.main + ' ' + textFields;
                    } else {
                        query = textFields;
                    }
                    
                    return query.trim();
                }

                function parseSimpleSearchQuery(query) {
                    if (query) {
                        var titleRegex = /(title:\"(.*?)\"|title:([^\s]+?)(\s|$))/g;

                        var firstTitle = '';
                        var main = query;

                        while(title = titleRegex.exec(query)) {
                            // Remove token from main query
                            main = main.replace(title[1], '');

                            if (!firstTitle) {
                                firstTitle = title[3] ? title[3] : title[2];
                            }
                        }

                        $scope.detailedSearch.main = removeExtraWhitespace(main).trim();
                        $scope.detailedSearch.title = firstTitle.trim();
                    }
                }

                function removeExtraWhitespace(str) {
                    return str.replace(/\s{2,}/g,' ');
                }

                function hasWhitespace(str) {
                    return /\s/g.test(str);
                }

                function addQuotesIfHasWhitespace(str) {
                    return hasWhitespace(str) ? '"' + str + '"' : str;
                }

                $scope.$watch('queryIn', function(queryIn) {
                    parseSimpleSearchQuery(queryIn);
                }, true);

                $scope.$watch('detailedSearch', function() {
                    $scope.queryOut = createSimpleSearchQuery(true);
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
