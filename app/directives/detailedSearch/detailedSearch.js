define(['app'], function(app)
{
    app.directive('dopDetailedSearch', [ '$location', 'searchService', 'translationService', '$filter', 'serverCallService', 'metadataService', '$rootScope',
     function($location, searchService, translationService, $filter, serverCallService, metadataService, $rootScope) {
        return {
            scope: {
                queryIn: '=',
                queryOut: '='
            },
            templateUrl: 'directives/detailedSearch/detailedSearch.html',
            controller: function ($scope, $rootScope) {

                var BASIC_EDUCATION_ID = 2;
                var SECONDARY_EDUCATION_ID = 3;
                var VOCATIONAL_EDUCATION_ID = 4;
                var SPECIAL_EDUCATION_ID = 5;

                var taxon;

                init();

                function init() {
                	$scope.isEditPortforlioMode = $rootScope.isEditPortforlioMode;
                	
                    // Detailed search fields
                    $scope.detailedSearch = {};

                    // Languages
                    serverCallService.makeGet("rest/learningMaterialMetadata/language", {}, getLanguagesSuccess, getLanguagesFail);
                    $scope.detailedSearch.language = searchService.getLanguage();

                    // Target groups
                    $scope.detailedSearch.targetGroups = searchService.getTargetGroups();

                    // Taxon
                    if (searchService.getTaxon()) {
                    	getTaxonById(searchService.getTaxon());
                    }

                    // Paid
                    var isPaid = searchService.isPaid();
                    if (isPaid === true  || isPaid === false) {
                        $scope.detailedSearch.paid = isPaid;
                    } else {
                        $scope.detailedSearch.paid = true;
                    }

                    // Type
                    $scope.detailedSearch.type = 'all';
                    if (searchService.getType() && searchService.isValidType(searchService.getType())) {
                        $scope.detailedSearch.type = searchService.getType();
                    }

                    // ResourceType
                    var resourceType = searchService.getResourceType();
                    if (resourceType && resourceType.toLowerCase() === 'textbook') {
                        $scope.detailedSearch.onlyBooks = true;
                    }

                    // Special education
                    var isSpecialEducation = searchService.isSpecialEducation();
                    if (isSpecialEducation === true || isSpecialEducation === false) {
                        $scope.detailedSearch.specialEducation = isSpecialEducation;
                    } else {
                        $scope.detailedSearch.specialEducation = false;
                    }

                    // Issue date
                    $scope.issueDateFirstYear = 2009;
                    $scope.issueDateLastYear = new Date().getFullYear();
                    var issuedFrom = searchService.getIssuedFrom();
                    if (issuedFrom && issuedFrom >= $scope.issueDateFirstYear && issuedFrom <= $scope.issueDateLastYear) {
                        $scope.detailedSearch.issueDate = issuedFrom;
                    }

                    // Cross-curricular themes
                    metadataService.loadCrossCurricularThemes(setCrossCurricularThemes);
                    var crossCurricularTheme = searchService.getCrossCurricularTheme();
                    if (crossCurricularTheme) {
                        $scope.detailedSearch.crossCurricularTheme = crossCurricularTheme;
                    }

                    // Key competences
                    metadataService.loadKeyCompetences(setKeyCompetences);
                    var keyCompetence = searchService.getKeyCompetence();
                    if (keyCompetence) {
                        $scope.detailedSearch.keyCompetence = keyCompetence;
                    }
                }

                function getTaxonById(taxonId) {
                    var params = {
                        'taxonId': taxonId
                    };
                    log('Getting taxon.')
                    serverCallService.makeGet("rest/learningMaterialMetadata/taxon", params, getTaxonSuccess, getTaxonFail);
                }
                
                $scope.search = function() {
                    searchService.setSearch(createSimpleSearchQuery());

                    addIsPaidToSearch();
                    addTypeToSearch();
                    addLanguageToSearch();
                    addTaxonToSearch();
                    addTargetGroupsToSearch();
                    addOnlyBooksCheckboxToSearch();
                    addSpecialEducationCheckboxToSearch();
                    addIssueDateToSearch();
                    addCrossCurricularThemeToSearch();
                    addKeyCompetenceToSearch();

                    $location.url(searchService.getURL());
                };

                function addIsPaidToSearch() {
                    searchService.setPaid($scope.detailedSearch.paid);
                }

                function addTypeToSearch() {
                    searchService.setType($scope.detailedSearch.type);
                }

                function addLanguageToSearch() {
                    searchService.setLanguage($scope.detailedSearch.language);
                }

                function addTaxonToSearch() {
                   if ($scope.detailedSearch.taxon) {
                        searchService.setTaxon($scope.detailedSearch.taxon.id);
                    } else {
                        searchService.setTaxon(null);
                    }
                }

                function addTargetGroupsToSearch() {
                    if ($scope.detailedSearch.targetGroups) {
                        searchService.setTargetGroups($scope.detailedSearch.targetGroups);
                    } else {
                        searchService.setTargetGroups(null);
                    }
                }

                function addOnlyBooksCheckboxToSearch() {
                    if ($scope.detailedSearch.onlyBooks) {
                        searchService.setResourceType('textbook');
                    } else {
                        searchService.setResourceType(null);
                    }
                }

                function addSpecialEducationCheckboxToSearch() {
                    searchService.setIsSpecialEducation($scope.detailedSearch.specialEducation);
                }

                function addIssueDateToSearch() {
                    var effectiveIssueDate = $scope.getEffectiveIssueDate();
                    if (effectiveIssueDate) {
                        searchService.setIssuedFrom(effectiveIssueDate);
                    } else {
                        searchService.setIssuedFrom(null);
                    }
                }

                function addCrossCurricularThemeToSearch() {
                    if ($scope.detailedSearch.crossCurricularTheme) {
                        searchService.setCrossCurricularTheme($scope.detailedSearch.crossCurricularTheme);
                    } else {
                        searchService.setCrossCurricularTheme(null);
                    }
                }

                function addKeyCompetenceToSearch() {
                    if ($scope.detailedSearch.keyCompetence) {
                        searchService.setKeyCompetence($scope.detailedSearch.keyCompetence);
                    } else {
                        searchService.setKeyCompetence(null);
                    }
                }

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

                function getCheckboxesAsQuery() {
                    if ($scope.detailedSearch.CLIL === true && $scope.detailedSearch.educationalContext.id === 2) {
                        return 'LAK "Lõimitud aine- ja keeleõpe"';
                    }
                    if ($scope.detailedSearch.specialEducationalNeed === true && $scope.detailedSearch.educationalContext.id === 4) {
                        return 'HEV "Hariduslik erivajadus"';
                    }
                    return '';
                }

                function createSimpleSearchQuery() {
                    var query = '';
                    var textFields = getTextFieldsAsQuery();
                    var checkboxes = getCheckboxesAsQuery();

                    query = (textFields + ' ' + checkboxes).trim();

                    if ($scope.detailedSearch.main) {
                        query = $scope.detailedSearch.main + ' ' + query;
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
                        var clilRegex = /(^|\s)(LAK|"Lõimitud aine- ja keeleõpe")(?=\s|$)/g;
                        var specialEducationalNeedRegex = /(^|\s)(HEV|"Hariduslik erivajadus")(?=\s|$)/g;

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

                        while(keyword = clilRegex.exec(query)) {
                            main = main.replace(keyword[2], '');
                            $scope.detailedSearch.CLIL = true;
                        }

                        while(keyword = specialEducationalNeedRegex.exec(query)) {
                            main = main.replace(keyword[2], '');
                            $scope.detailedSearch.specialEducationalNeed = true;
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

                $scope.$watchCollection('detailedSearch', function() {
                    $scope.queryOut = createSimpleSearchQuery();
                });

                function getTaxonSuccess(data) {
                    if (isEmpty(data)) {
                        getTaxonFail();
                    } else {
                        $scope.detailedSearch.taxon = data;
                    }
                }

                function getTaxonFail() {
                    console.log('Failed to get taxon.')
                }

                function getLanguagesSuccess(data) {
                    if (isEmpty(data)) {
                        getLanguagesFail();
                    } else {
                        $scope.languages = data;
                    }
                }

                function getLanguagesFail() {
                    console.log('Failed to get languages.')
                }

                $scope.getLanguageTranslationKey = function(languageCode) {
                    return 'LANGUAGE_' + languageCode.toUpperCase();
                }

                function clearHiddenFields() {
                    var educationalContext = $scope.detailedSearch.educationalContext;

                    // Only books checkbox
                    if (!educationalContext ||
                        (educationalContext.id != BASIC_EDUCATION_ID && 
                         educationalContext.id != SECONDARY_EDUCATION_ID && 
                         educationalContext.id != VOCATIONAL_EDUCATION_ID)) {
                        $scope.detailedSearch.onlyBooks = false;
                    }

                    // Special education checkbox
                    if (!educationalContext || educationalContext.id != BASIC_EDUCATION_ID) {
                        $scope.detailedSearch.specialEducation = false;
                    }

                    // Cross-curricular themes and key competences
                    if (!educationalContext || (educationalContext.id != BASIC_EDUCATION_ID && educationalContext.id != SECONDARY_EDUCATION_ID)) {
                        $scope.detailedSearch.crossCurricularTheme = null;
                        $scope.detailedSearch.keyCompetence = null;
                    }
                }

                $scope.clear = function() {
                    $scope.detailedSearch = {
                        'paid': true,
                        'onlyBooks': false,
                        'CLIL': false,
                        'targetGroups': [],
                        'specialEducation': false,
                        'specialEducationalNeed': false,
                        'issueDate': $scope.issueDateFirstYear,
                        'type': 'all'
                    };
                }

                $scope.$watch('detailedSearch.taxon', function(newTaxon, oldTaxon) {
                    if (newTaxon !== oldTaxon) {
                        $scope.detailedSearch.educationalContext = $rootScope.taxonUtils.getEducationalContext($scope.detailedSearch.taxon);
                        clearHiddenFields();
                    }
                }, true);
                
                $scope.$watch(function() { return searchService.getTaxon() }, function(newTaxon, oldTaxon) {
                    if (newTaxon !== oldTaxon) {
                    	if(newTaxon == null) {
                    		 $scope.detailedSearch.taxon = newTaxon;
                    	}
                    	else {
                    		getTaxonById(newTaxon);
                    	}
                    }
                }, true);
                
                $scope.$watch(function() { return searchService.getType() }, function(newType, oldType) {
                	$scope.detailedSearch.type = newType;
                }, true);
                
                $scope.$watch(function() { return searchService.getTargetGroups() }, function(newGroups, oldGroups) {
                	$scope.detailedSearch.targetGroups = newGroups;
                }, true);

                $scope.getEffectiveIssueDate = function() {
                    if ($scope.detailedSearch.issueDate && $scope.detailedSearch.issueDate != $scope.issueDateFirstYear) {
                        return $scope.detailedSearch.issueDate;
                    }
                }

                function setCrossCurricularThemes(crossCurricularThemes) {
                    $scope.crossCurricularThemes = crossCurricularThemes;
                }

                function setKeyCompetences(keyCompetences) {
                    $scope.keyCompetences = keyCompetences;
                }

            }
        };
    }]);

    return app;
});
