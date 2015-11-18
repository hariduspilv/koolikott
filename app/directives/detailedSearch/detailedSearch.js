define(['app'], function(app)
{
    app.directive('dopDetailedSearch', [ '$location', 'searchService', 'translationService', '$filter',
     function($location, searchService, translationService, $filter) {
        return {
            scope: {
                queryIn: '=',
                queryOut: '='
            },
            templateUrl: 'directives/detailedSearch/detailedSearch.html',
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

                    if (searchService.getEducationalContext() && validEducationalContexts.indexOf(searchService.getEducationalContext()) > -1) {
                        $scope.detailedSearch.educationalContext = searchService.getEducationalContext();
                    }

                    // Paid
                    if (searchService.isPaid() === true  || searchService.isPaid() === false) {
                        $scope.detailedSearch.paid = searchService.isPaid();
                    } else {
                        $scope.detailedSearch.paid = true;
                    }

                    // Type
                    $scope.detailedSearch.type = 'all';

                    if (searchService.getType() && searchService.isValidType(searchService.getType())) {
                        $scope.detailedSearch.type = searchService.getType();
                    }

                }

                $scope.search = function() {
                    searchService.setSearch(createSimpleSearchQuery());

                    searchService.setPaid($scope.detailedSearch.paid);
                    searchService.setType($scope.detailedSearch.type);
                    searchService.setEducationalContext($scope.detailedSearch.educationalContext);

                    $location.url(searchService.getURL());
                };

                function getTextFieldsAsQuery() {
                    var query = '';

                    if ($scope.detailedSearch.title) {
                        query += 'title:' + addQuotesIfNecessary($scope.detailedSearch.title);
                    }
                    if ($scope.detailedSearch.combinedDescription) {
                        query += ' description:' + addQuotesIfNecessary($scope.detailedSearch.combinedDescription)
                            + ' summary:' + addQuotesIfNecessary($scope.detailedSearch.combinedDescription);
                    }
                    if ($scope.detailedSearch.author) {
                        query += ' author:' + addQuotesIfNecessary($scope.detailedSearch.author);
                    }

                    return query.trim();
                }

                function createSimpleSearchQuery() {
                    var query = '';
                    var textFields = getTextFieldsAsQuery();

                    if ($scope.detailedSearch.main) {
                        query = $scope.detailedSearch.main + ' ' + textFields;
                    } else {
                        query = textFields;
                    }

                    return query.trim();
                }

                function parseSimpleSearchQuery(query) {
                    $scope.detailedSearch.main = '';
                    $scope.detailedSearch.title = '';
                    $scope.detailedSearch.combinedDescription = '';
                    $scope.detailedSearch.author = '';

                    if (query) {
                        var titleRegex = /(^|\s)(title:([^\s\"]\S*)|title:\"(.*?)\"|title:)/g;
                        var descriptionRegex = /(^|\s)(description:([^\s\"]\S*)|description:\"(.*?)\"|description:|summary:([^\s\"]\S*)|summary:\"(.*?)\"|summary:)/g;
                        var authorRegex = /(^|\s)(author:([^\s\"]\S*)|author:\"(.*?)\"|author:)/g;

                        var firstTitle;
                        var firstDescription;
                        var firstAuthor;
                        var main = query;

                        while(title = titleRegex.exec(query)) {
                            // Remove token from main query
                            main = main.replace(title[2], '');

                            if (!firstTitle) {
                                // Get token content
                                firstTitle = title[3] || title[4];
                            }
                        }

                        while(description = descriptionRegex.exec(query)) {
                            main = main.replace(description[2], '');
                            if (!firstDescription) {
                                firstDescription = description[3] || description[4] || description[5] || description[6];
                            }
                        }

                        while(author = authorRegex.exec(query)) {
                            main = main.replace(author[2], '');
                            if (!firstAuthor) {
                                firstAuthor = author[3] || author[4];
                            }
                        }

                        $scope.detailedSearch.main = removeExtraWhitespace(main).trim();
                        $scope.detailedSearch.title = firstTitle;
                        $scope.detailedSearch.combinedDescription = firstDescription;
                        $scope.detailedSearch.author = firstAuthor;
                    }
                }

                function removeExtraWhitespace(str) {
                    return str.replace(/\s{2,}/g,' ');
                }

                function hasWhitespace(str) {
                    return /\s/g.test(str);
                }

                function addQuotesIfNecessary(str) {
                    return hasWhitespace(str) ? '"' + str + '"' : str;
                }

                $scope.$watch('queryIn', function(queryIn) {
                    parseSimpleSearchQuery(queryIn);
                }, true);

                $scope.$watch('detailedSearch', function() {
                    $scope.queryOut = createSimpleSearchQuery();
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
