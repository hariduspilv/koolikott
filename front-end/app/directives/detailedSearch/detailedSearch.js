'use strict'

angular.module('koolikottApp').directive('dopDetailedSearch', [
    '$location', 'searchService', 'translationService', '$filter', 'serverCallService', 'metadataService', 'taxonService', 'storageService',
    function ($location, searchService, translationService, $filter, serverCallService, metadataService, taxonService, storageService) {
        return {
            scope: {
                queryIn: '=',
                queryOut: '=',
                mainField: '=',
                accessor: '=',
                isVisible: '='
            },
            templateUrl: 'detailedSearch.html',
            controller: ['$scope', '$rootScope', '$timeout', '$window', '$anchorScroll', function ($scope, $rootScope, $timeout, $window, $anchorScroll) {
                $scope.queryIn = $scope.queryIn ? $scope.queryIn : "";

                var BASIC_EDUCATION_ID = 2;
                var SECONDARY_EDUCATION_ID = 3;
                var VOCATIONAL_EDUCATION_ID = 4;
                var initiated = false;
                var prefilling = false;

                init();

                function init() {
                    // Detailed search fields
                    $scope.detailedSearch = {};

                    // Languages
                    metadataService.loadUsedLanguages(setLanguages);
                    $scope.detailedSearch.language = searchService.getLanguage();
                    if (!searchService.getLanguage()) $scope.detailedSearch.language = null;

                    //Used resource types
                    metadataService.loadUsedResourceTypes(setUsedResourceTypes);

                    // Target groups
                    $scope.detailedSearch.targetGroups = searchService.getTargetGroups();

                    // Paid
                    var isPaid = searchService.isPaid();
                    if (isPaid === true || isPaid === false) {
                        $scope.detailedSearch.paid = !isPaid;
                    } else {
                        $scope.detailedSearch.paid = false;
                    }

                    // Type
                    $scope.detailedSearch.type = 'all';
                    if (searchService.getType() && searchService.isValidType(searchService.getType())) {
                        $scope.detailedSearch.type = searchService.getType();
                    }

                    // Curriculum literature
                    if (searchService.isCurriculumLiterature()) {
                        $scope.detailedSearch.onlyCurriculumLiterature = true;
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

                    if ($rootScope.isEditPortfolioMode && storageService.getPortfolio()) {
                        setEditModePrefill();
                    }
                }

                $scope.search = function () {
                    searchService.setSearch(createSimpleSearchQuery());

                    addIsPaidToSearch();
                    addTypeToSearch();
                    addLanguageToSearch();
                    addTaxonToSearch();
                    addTargetGroupsToSearch();
                    addResourceTypeToSearch();
                    addOnlyCurriculumLiteratureCheckboxToSearch();
                    addSpecialEducationCheckboxToSearch();
                    addIssueDateToSearch();
                    addCrossCurricularThemeToSearch();
                    addKeyCompetenceToSearch();

                    $location.url(searchService.getURL());
                };

                function addIsPaidToSearch() {
                    searchService.setPaid(!$scope.detailedSearch.paid);
                }

                function addTypeToSearch() {
                    searchService.setType($scope.detailedSearch.type);
                }

                function addLanguageToSearch() {
                    searchService.setLanguage($scope.detailedSearch.language);
                }

                function addTaxonToSearch() {
                    if ($scope.detailedSearch.taxon) {
                        searchService.setTaxon([$scope.detailedSearch.taxon.id]);
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

                function addResourceTypeToSearch() {
                    if ($scope.detailedSearch.resourceType) {
                        searchService.setResourceType($scope.detailedSearch.resourceType);
                    } else {
                        searchService.setResourceType(null);
                    }
                }

                function addOnlyCurriculumLiteratureCheckboxToSearch() {
                    if ($scope.detailedSearch.onlyCurriculumLiterature) {
                        searchService.setCurriculumLiterature(true);
                    } else {
                        searchService.setCurriculumLiterature(null);
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
                        query += ' description:' + addQuotesIfNecessary($scope.detailedSearch.combinedDescription) + ' summary:' + addQuotesIfNecessary($scope.detailedSearch.combinedDescription);
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
                    var textFields = getTextFieldsAsQuery();
                    var checkboxes = getCheckboxesAsQuery();
                    var main = $scope.detailedSearch.main ? $scope.detailedSearch.main : "";

                    return (main + ' ' + textFields + ' ' + checkboxes).trim();
                }

                function parseSimpleSearchQuery(query) {
                    var titleRegex = /(^|\s)(title:([^\s\"]\S*)|title:\"(.*?)\"|title:)/g;
                    var descriptionRegex = /(^|\s)(description:([^\s\"]\S*)|description:\"(.*?)\"|description:|summary:([^\s\"]\S*)|summary:\"(.*?)\"|summary:)/g;
                    var authorRegex = /(^|\s)(author:([^\s\"]\S*)|author:\"(.*?)\"|author:)/g;
                    var clilRegex = /(^|\s)(LAK|"Lõimitud aine- ja keeleõpe")(?=\s|$)/g;
                    var specialEducationalNeedRegex = /(^|\s)(HEV|"Hariduslik erivajadus")(?=\s|$)/g;

                    var firstTitle;
                    var firstDescription;
                    var firstAuthor;
                    var main = query;

                    var title;
                    while (title = titleRegex.exec(query)) {
                        // Remove token from main query
                        main = main.replace(title[2], '');

                        if (!firstTitle) {
                            // Get token content
                            firstTitle = title[3] || title[4];
                        }
                    }

                    var description;
                    while (description = descriptionRegex.exec(query)) {
                        main = main.replace(description[2], '');
                        if (!firstDescription) {
                            firstDescription = description[3] || description[4] || description[5] || description[6];
                        }
                    }

                    var author;
                    while (author = authorRegex.exec(query)) {
                        main = main.replace(author[2], '');
                        if (!firstAuthor) {
                            firstAuthor = author[3] || author[4];
                        }
                    }

                    var keyword;
                    while (keyword = clilRegex.exec(query)) {
                        main = main.replace(keyword[2], '');
                        $scope.detailedSearch.CLIL = true;
                    }

                    while (keyword = specialEducationalNeedRegex.exec(query)) {
                        main = main.replace(keyword[2], '');
                        $scope.detailedSearch.specialEducationalNeed = true;
                    }

                    $scope.detailedSearch.main = removeExtraWhitespace(main).trim() || "";
                    $scope.detailedSearch.title = firstTitle || $scope.detailedSearch.title || "";
                    $scope.detailedSearch.combinedDescription = firstDescription || $scope.detailedSearch.combinedDescription || "";
                    $scope.detailedSearch.author = firstAuthor || $scope.detailedSearch.author || "";

                    $scope.mainField = $scope.detailedSearch.main;

                }

                function removeExtraWhitespace(str) {
                    return str.replace(/\s{2,}/g, ' ');
                }

                function hasWhitespace(str) {
                    return /\s/g.test(str);
                }

                function addQuotesIfNecessary(str) {
                    return hasWhitespace(str) ? '"' + str + '"' : str;
                }

                $scope.$watch('isVisible', function () {
                    if (!initiated) {
                        initWatches();
                        initiated = true;
                    }
                }, true);

                $scope.$on('detailedSearch:open', function () {
                    $timeout(function () {
                        metadataService.updateUsedResourceTypes(setUsedResourceTypes);
                    });

                    if ($window.innerWidth < 600) {
                        $anchorScroll();
                    }
                });

                function initWatches() {

                    $scope.$watch(function() {
                        return storageService.getPortfolio()
                    }, function (newValue, oldValue) {
                        if (newValue && oldValue && (newValue !== oldValue)) {
                            setEditModePrefill();
                        }
                    }, true);

                    $rootScope.$watch("isEditPortfolioMode", function (newValue, oldValue) {
                        if (newValue && (newValue !== oldValue)) {
                            setEditModePrefill();
                        }
                    }, true);

                    $scope.$watch('queryIn', function (queryIn, oldQueryIn) {
                        if (queryIn !== oldQueryIn && $scope.isVisible) {
                            parseSimpleSearchQuery(queryIn);
                        }
                    }, true);


                    $scope.$watch('detailedSearch', function (newValue, oldValue) {
                        if (!$scope.detailedSearch.taxon) $scope.detailedSearch.educationalContext = null;

                        if ($scope.isVisible && !prefilling && hasSearchChanged(newValue, oldValue)) {
                            filterTypeSearch();
                            $scope.search();
                        } else if (prefilling) {
                            prefilling = false;
                        }
                    }, true);
                }

                function filterTypeSearch() {
                    if ($scope.detailedSearch.resourceType === 'all') {
                        $scope.detailedSearch.type = 'all';
                    } else if ($scope.detailedSearch.resourceType === 'PORTFOLIO_RESOURCE') {
                        $scope.detailedSearch.type = 'portfolio';
                    } else if ($rootScope.isEditPortfolioMode) {
                        $scope.detailedSearch.type = 'material';
                    } else {
                        $scope.detailedSearch.type = 'all';
                    }
                }

                $scope.getLanguageTranslationKey = function (languageCode) {
                    return 'LANGUAGE_' + languageCode.toUpperCase();
                };

                function hasSearchChanged(newValue, oldValue) {
                    if (newValue.main !== oldValue.main) return true;
                    if (newValue.title !== oldValue.title) return true;
                    if (newValue.language !== oldValue.language) return true;
                    if (newValue.resourceType !== oldValue.resourceType) return true;
                    if (!_.isEqual(newValue.targetGroups, oldValue.targetGroups)) return true;
                    if (newValue.onlyCurriculumLiterature !== oldValue.onlyCurriculumLiterature) return true;
                    if (newValue.specialEducation !== oldValue.specialEducation) return true;
                    if (newValue.paid !== oldValue.paid) return true;
                    if (newValue.type !== oldValue.type) return true;
                    if (newValue.issueDate !== oldValue.issueDate) return true;
                    if (newValue.crossCurricularTheme !== oldValue.crossCurricularTheme) return true;
                    if (newValue.keyCompetence !== oldValue.keyCompetence) return true;
                    if (newValue.specialEducationalNeed !== oldValue.specialEducationalNeed) return true;
                    if (newValue.CLIL !== oldValue.CLIL) return true;
                    if (newValue.taxon !== oldValue.taxon && $scope.detailedSearch.taxon) {
                        $scope.detailedSearch.educationalContext = taxonService.getEducationalContext($scope.detailedSearch.taxon);
                        portfolioEditModeProcess({newValue: newValue.taxon, oldValue: oldValue.taxon});
                        clearHiddenFields();
                        return true;
                    }
                }

                function portfolioEditModeProcess(taxonChange) {
                    if ($rootScope.isEditPortfolioMode) {
                        $scope.$broadcast("detailedSearch:taxonChange", taxonChange);
                    }
                }

                function clearHiddenFields() {
                    var educationalContext = $scope.detailedSearch.educationalContext;

                    // Only curriculum literature checkbox
                    if (!educationalContext ||
                        (educationalContext.id != BASIC_EDUCATION_ID &&
                        educationalContext.id != SECONDARY_EDUCATION_ID &&
                        educationalContext.id != VOCATIONAL_EDUCATION_ID)) {
                        $scope.detailedSearch.onlyCurriculumLiterature = false;
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

                    // Target groups
                    if (educationalContext && educationalContext.id === VOCATIONAL_EDUCATION_ID) {
                        $scope.detailedSearch.targetGroups = [];
                    }
                }

                $scope.clear = $scope.accessor.clear = function () {
                    $scope.detailedSearch = {
                        'mainField': '',
                        'paid': false,
                        'onlyCurriculumLiterature': false,
                        'CLIL': false,
                        'targetGroups': [],
                        'specialEducation': false,
                        'specialEducationalNeed': false,
                        'issueDate': $scope.issueDateFirstYear,
                        'type': 'all',
                        'taxon': {},
                        'language': null
                    };

                    if ($rootScope.isEditPortfolioMode) {
                        $scope.detailedSearch.type = "material";
                    }

                    $scope.$parent.clearTaxonSelector();
                    $scope.accessor.clearSimpleSearch();
                };

                $scope.getEffectiveIssueDate = function () {
                    if ($scope.detailedSearch.issueDate && $scope.detailedSearch.issueDate != $scope.issueDateFirstYear) {
                        return $scope.detailedSearch.issueDate;
                    }
                };

                function setLanguages(languages) {
                    $scope.languages = languages;
                }

                function setUsedResourceTypes(resourceTypes) {
                    $scope.usedResourceTypes = resourceTypes;
                    if (!listContains($scope.usedResourceTypes, 'name', 'PORTFOLIO_RESOURCE')) {
                        $scope.usedResourceTypes.push({name: 'PORTFOLIO_RESOURCE'})
                    }
                }

                function setCrossCurricularThemes(crossCurricularThemes) {
                    $scope.crossCurricularThemes = crossCurricularThemes;
                }

                function setKeyCompetences(keyCompetences) {
                    $scope.keyCompetences = keyCompetences;
                }

                function setEditModePrefill() {
                    if ($rootScope.isEditPortfolioMode && storageService.getPortfolio()) {
                        $scope.detailedSearch.taxon = storageService.getPortfolio().taxons[0];
                        $scope.detailedSearch.targetGroups = storageService.getPortfolio().targetGroups;

                        $scope.$broadcast("detailedSearch:prefillTaxon", storageService.getPortfolio().taxons[0]);
                        $scope.$broadcast("detailedSearch:prefillTargetGroup", storageService.getPortfolio().targetGroups);

                        prefilling = true;

                        $scope.detailedSearch.type = "material";
                    }
                }

            }]
        };
    }
]);
