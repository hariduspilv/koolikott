'use strict'

angular.module('koolikottApp').directive('dopHeader', [
    'translationService', '$location', 'searchService', 'authenticationService', 'authenticatedUserService', '$timeout', '$mdDialog', 'suggestService', 'serverCallService', 'toastService', '$route', '$http', '$window', '$translate', 'storageService', 'dialogService',
    function (translationService, $location, searchService, authenticationService, authenticatedUserService, $timeout, $mdDialog, suggestService, serverCallService, toastService, $route, $http, $window, $translate, storageService, dialogService) {
        return {
            scope: true,
            templateUrl: 'directives/header/header.html',
            link: function () {
                var scrollTimer,
                    detailedSearch = document.getElementById('detailedSearch'),
                    $detailedSearch = angular.element(detailedSearch),
                    $header = document.getElementById('md-toolbar-header');

                angular.element($window).on('scroll', function () {
                    clearTimeout(scrollTimer);
                    scrollTimer = setTimeout(function () {
                        var $backdrop = document.querySelectorAll('.md-menu-backdrop, .md-select-backdrop'),
                            isDetailedSearchHidden = detailedSearch.getAttribute('aria-hidden');

                        if ($backdrop.length === 0 && isDetailedSearchHidden) {
                            if (this.pageYOffset >= $header.offsetHeight && $window.innerWidth >= 960) {
                                $detailedSearch.addClass('md-toolbar-filter--fixed');
                            } else {
                                $detailedSearch.removeClass('md-toolbar-filter--fixed');
                            }
                        }
                    }, 200);
                });
            },
            controller: ['$scope', '$location', '$rootScope', function ($scope, $location, $rootScope) {
                $scope.detailedSearch = {};
                $scope.detailedSearch.isVisible = false;
                $scope.mobileSearch = {};
                $scope.mobileSearch.isVisible = false;
                $scope.showLanguageSelection = false;
                $scope.selectedLanguage = translationService.getLanguage();
                $scope.searchFields = {};
                $scope.searchFields.searchQuery = searchService.getQuery();
                $scope.detailedSearch = {};
                $scope.suggest = {};
                $scope.suggest.suggestions = null;
                $scope.suggest.selectedItem = null;
                var dontSearch = false;

                const VISIBILITY_PUBLIC = "PUBLIC";
                const VISIBILITY_PRIVATE = "PRIVATE";
                const VISIBILITY_NOT_LISTED = "NOT_LISTED";

                $scope.detailedSearch.accessor = {
                    clearSimpleSearch: function () {
                        $scope.searchFields.searchQuery = '';
                    }
                };

                $scope.setLanguage = function (language) {
                    var prevLanguage = $scope.selectedLanguage;
                    translationService.setLanguage(language);
                    $scope.selectedLanguage = language;
                    if (prevLanguage !== language) {
                        $window.location.reload();
                    }
                };

                $scope.logout = function () {
                    authenticationService.logout();
                    $location.url('/');
                };

                $scope.showLogin = function (ev) {
                    $mdDialog.show({
                        templateUrl: 'views/loginDialog/loginDialog.html',
                        controller: 'loginDialogController',
                        targetEvent: ev
                    });
                };

                $scope.search = function () {
                    searchService.setSearch($scope.searchFields.searchQuery);
                    searchService.clearFieldsNotInSimpleSearch();
                    if ($rootScope.isEditPortfolioMode) {
                        searchService.setType('material');
                    } else {
                        searchService.setType('all');
                    }
                    $location.url(searchService.getURL());
                };

                $scope.openDetailedSearch = () => {
                    $scope.detailedSearch.isVisible = true;
                    $scope.detailedSearch.queryIn = $scope.searchFields.searchQuery;
                    broadcastSearchOpen();
                };

                function broadcastSearchOpen() {
                    $scope.$broadcast("detailedSearch:open");
                }

                $scope.closeDetailedSearch = () => {
                    $timeout(function () {
                        if (!$rootScope.isEditPortfolioMode) {
                            $scope.clearTaxonSelector();
                            $scope.detailedSearch.accessor.clear();
                        }
                    }, 500);
                    dontSearch = true;
                    $scope.detailedSearch.isVisible = false;
                    $scope.detailedSearch.queryIn = "";
                };

                $scope.openMobileSearch = () => {
                    $scope.mobileSearch.isVisible = true;

                    $timeout(() => {
                        document.getElementById('header-simple-search-input').focus();
                    });
                }

                $scope.closeMobileSearch = (emptySearch) => {
                    $scope.mobileSearch.isVisible = false;

                    if (emptySearch) {
                        $scope.detailedSearch.accessor.clearSimpleSearch();
                    }
                }

                $scope.$on('detailedSearch:open', () => $scope.detailedSearch.isVisible = true);
                $scope.$on('detailedSearch:close', () => $scope.detailedSearch.isVisible = false);
                $scope.$on('detailedSearch:empty', () => $scope.closeDetailedSearch());
                $scope.$on('mobileSearch:open', () => $scope.openMobileSearch());

                $scope.suggest.doSuggest = function (query) {
                    if (query == null) {
                        return [];
                    }

                    $scope.suggest.suggestions = suggestService.suggest(query, suggestService.getSuggestURLbase());
                    if ($scope.doInlineSuggestion) {
                        suggestInline($scope.suggest.suggestions);
                    }
                    return $scope.suggest.suggestions;
                };

                function suggestInline(suggestions) {
                    if (!suggestions) return;
                    suggestions.then(function (data) {
                        var firstSuggestion = data[0];
                        if (!firstSuggestion) {
                            $scope.clearInlineSuggestion();
                            return;
                        }
                        var searchTextLength = $scope.searchFields.searchQuery.length;
                        $scope.hiddenInline = firstSuggestion.substring(0, searchTextLength);
                        $scope.inlineSuggestion = firstSuggestion.substring(searchTextLength);
                    })
                }

                $scope.clearInlineSuggestion = function () {
                    $scope.hiddenInline = "";
                    $scope.inlineSuggestion = "";
                };

                $scope.keyPressed = function (event) {
                    if (event.keyCode === 8) { // backspace
                        if ($scope.inlineSuggestion) {
                            event.preventDefault();
                        }

                        $scope.doInlineSuggestion = false;
                    } else if (event.keyCode === 13) { // enter
                        if (!isSearchResultPage()) {
                            processSearchQuery($scope.searchFields.searchQuery);
                        }

                        angular.element(document.querySelector("#header-search-input")).controller('mdAutocomplete').hidden = true;
                        document.getElementById("header-search-input").blur();
                        $scope.doInlineSuggestion = false;
                    } else {
                        $scope.doInlineSuggestion = true;
                    }

                    $scope.clearInlineSuggestion();
                };

                function isSearchResultPage() {
                    return $location.url().startsWith('/' + searchService.getSearchURLbase());
                }

                $scope.clickOutside = function () {
                    if ($rootScope.dontCloseSearch) {
                        $rootScope.dontCloseSearch = false;
                    }
                };

                $scope.$watch('detailedSearch.mainField', function (newValue, oldValue) {
                    if (newValue != oldValue) {
                        $scope.searchFields.searchQuery = newValue || "";
                    }
                }, true);


                $scope.$watch('searchFields.searchQuery', processSearchQuery, true);

                function processSearchQuery(newValue, oldValue) {
                    if (newValue != oldValue && !newValue) {
                        $scope.clearInlineSuggestion();
                    }

                    $scope.searchFields.searchQuery = newValue || "";
                    if (newValue !== oldValue && !$scope.detailedSearch.isVisible && !dontSearch) {
                        $scope.search();
                    } else if ($scope.detailedSearch.isVisible) {
                        $scope.detailedSearch.queryIn = $scope.searchFields.searchQuery;
                    }

                    if (dontSearch) dontSearch = false;
                }

                $scope.$watch(function () {
                    return authenticatedUserService.getUser();
                }, function (user) {
                    $scope.user = user;
                }, true);

                $scope.$watch(function () {
                    return searchService.getQuery();
                }, function (query) {
                    // Search query is not updated from search service while detailed search is open
                    if (!query || !$scope.detailedSearch.isVisible) {
                        $scope.searchFields.searchQuery = query;
                    }
                }, true);

                $scope.$watch(function () {
                    return translationService.getLanguage();
                }, function (language) {
                    $scope.setLanguage(language);
                }, true);

                $scope.isAdmin = function () {
                    return authenticatedUserService.isAdmin();
                };

                $scope.isModerator = function () {
                    return authenticatedUserService.isModerator();
                };

                $scope.isAdminOrModerator = function () {
                    return $scope.isAdmin() || $scope.isModerator();
                };

                $scope.getShareUrl = buildShareUrl();

                function buildShareUrl() {
                    var protocol = $location.protocol();
                    var host = $location.host();
                    var path = '/portfolio';
                    var params = $location.search();

                    return protocol + '://' + host + path + '?id=' + params.id;
                }

                $scope.makePublic = function () {
                    storageService.getPortfolio().visibility = VISIBILITY_PUBLIC;
                    updatePortfolio();

                    toastService.show('PORTFOLIO_HAS_BEEN_MADE_PUBLIC');
                };

                $scope.makeNotListed = function () {
                    storageService.getPortfolio().visibility = VISIBILITY_NOT_LISTED;
                    updatePortfolio();
                };

                $scope.makePrivate = function () {
                    storageService.getPortfolio().visibility = VISIBILITY_PRIVATE;
                    updatePortfolio();
                };

                $scope.clearTaxonSelector = function () {
                    $rootScope.$broadcast('taxonSelector:clear', null);
                };

                function updatePortfolio() {
                    var url = "rest/portfolio/update";
                    serverCallService.makePost(url, storageService.getPortfolio(), updatePortfolioSuccess, updatePortfolioFailed);
                }

                function updatePortfolioSuccess(portfolio) {
                    if (isEmpty(portfolio)) {
                        updatePortfolioFailed();
                    } else {
                        log('Portfolio updated.');
                    }
                }

                $scope.saveAndExitPortfolio = function () {
                    startSaving(storageService.getPortfolio());
                };

                function saveAndExitPortfolioSuccess(portfolio) {
                    if (!isEmpty(portfolio)) {
                        toastService.show('PORTFOLIO_SAVED');
                        storageService.setPortfolio(null);
                        $location.url('/portfolio?id=' + portfolio.id);
                        $route.reload();
                    }
                }

                function updatePortfolioFailed() {
                    log('Updating portfolio failed.');
                }

                function saveAndExit() {
                    var url = "rest/portfolio/update";
                    serverCallService.makePost(url, storageService.getPortfolio(), saveAndExitPortfolioSuccess, updatePortfolioFailed);
                }

                function startSaving(portfolio) {
                    if (portfolio.visibility === VISIBILITY_PUBLIC) saveAndExit();
                    else showVisibilityChangeDialog();
                }

                function showVisibilityChangeDialog() {
                    var makePublic = function () {
                        storageService.getPortfolio().visibility = VISIBILITY_PUBLIC;
                        saveAndExit();
                    };

                    dialogService.showConfirmationDialog(
                        "{{'PORTFOLIO_MAKE_PUBLIC' | translate}}",
                        "{{'PORTFOLIO_WARNING' | translate}}",
                        "{{'PORTFOLIO_YES' | translate}}",
                        "{{'PORTFOLIO_NO' | translate}}",
                        makePublic,
                        saveAndExit);
                }

                $scope.$watch(function () {
                    return $location.path()
                }, function (params) {
                    if (params.indexOf("/portfolio") !== -1 || params.indexOf("/material") !== -1) {
                        $scope.detailedSearch.isVisible = false;
                    }
                });

                $scope.$watch(function () {
                    return [$location.url(), $rootScope.isEditPortfolioMode];
                }, function () {
                    $scope.isEditModeAndNotEditView = ($rootScope.isEditPortfolioMode && $location.url().indexOf('/portfolio/edit') !== 0);
                }, true);

                $scope.getTranslation = function (string) {
                    return $translate.instant(string);
                };

                $scope.isHeaderRed = function () {
                    return ($scope.isAdminOrModerator()
                    && ($scope.isViewAdminPanelPage
                    || (($scope.learningObjectImproper
                    || $scope.learningObjectBroken
                    || $scope.learningObjectChanged)
                    && $scope.isViewMaterialOrPortfolioPage)));
                };

                $scope.getPortfolioVisibility = function () {
                    if (storageService.getPortfolio()) {
                        return storageService.getPortfolio().visibility;
                    }
                };

                $scope.openTour = () => $rootScope.$broadcast('tour:open');

            }]
        };
    }
]);
