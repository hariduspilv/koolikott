'use strict'

angular.module('koolikottApp')
    .controller('editPortfolioController',
        [
            '$scope', 'translationService', 'serverCallService', '$route', '$location', 'alertService', '$rootScope', 'authenticatedUserService', 'dialogService', 'toastService', '$mdDialog', '$interval', '$filter', '$timeout', '$document', 'storageService',
            function ($scope, translationService, serverCallService, $route, $location, alertService, $rootScope, authenticatedUserService, dialogService, toastService, $mdDialog, $interval, $filter, $timeout, $document, storageService) {
                var isAutoSaving = false;
                var autoSaveInterval;

                function init() {
                    if (storageService.getPortfolio()) {
                        if (checkAuthorized(storageService.getPortfolio())) {
                            setPortfolio(storageService.getPortfolio());
                        }
                    } else {
                        getPortfolio(getPortfolioSuccess, getPortfolioFail);
                    }

                    if ($rootScope.newPortfolioCreated) {
                        $scope.showFirstMessage = true;
                        $rootScope.newPortfolioCreated = false;
                    }

                    $rootScope.savedChapter = null;
                    startAutosave();
                }

                function getPortfolio(success, fail) {
                    var portfolioId = $route.current.params.id;
                    serverCallService.makeGet("rest/portfolio?id=" + portfolioId, {}, success, fail);
                }

                function getPortfolioFail() {
                    log('No data returned by getting portfolio.');
                    alertService.setErrorAlert('ERROR_PORTFOLIO_NOT_FOUND');
                    $location.url("/");
                }

                function getPortfolioSuccess(portfolio) {
                    if (isEmpty(portfolio)) {
                        getPortfolioFail();
                    } else if (checkAuthorized(portfolio)) {
                        setPortfolio(portfolio);
                    }
                }

                $scope.toggleSidenav = function (menuId) {
                    $mdSidenav(menuId).toggle();
                };

                $scope.closeSidenav = function (menuId) {
                    $mdSidenav(menuId).close();
                };

                $scope.onDeleteChapter = function (chapter) {
                    var deleteChapter = function () {
                        $scope.portfolio.chapters.splice($scope.portfolio.chapters.indexOf(chapter), 1);
                    };

                    dialogService.showDeleteConfirmationDialog(
                        'PORTFOLIO_DELETE_CHAPTER_CONFIRM_TITLE',
                        'PORTFOLIO_DELETE_CHAPTER_CONFIRM_MESSAGE',
                        deleteChapter);
                };

                function updatePortfolio() {
                    var url = "rest/portfolio/update";
                    serverCallService.makePost(url, $scope.portfolio, updatePortfolioSuccess, updatePortfolioFailed);
                }

                function updatePortfolioSuccess(portfolio) {
                    if (isEmpty(portfolio)) {
                        createPortfolioFailed();
                    } else {
                        if (!isAutoSaving) setPortfolio(portfolio);

                        var message = isAutoSaving ? 'PORTFOLIO_AUTOSAVED' : 'PORTFOLIO_SAVED';
                        toastService.show(message);
                    }
                }

                function updatePortfolioFailed() {
                    log('Updating portfolio failed.');
                }

                function setPortfolio(portfolio) {
                    $scope.portfolio = portfolio;

                    if ($scope.portfolio.chapters) {
                        $scope.portfolio.chapters.forEach(function (chapter) {
                            if (chapter.contentRows) {
                                chapter.contentRows.forEach(function (contentRow) {
                                    contentRow.learningObjects.forEach(function (learningObject) {
                                        learningObject.source = getSource(learningObject);
                                    })
                                })
                            }
                        });
                    }

                    storageService.setPortfolio(portfolio);
                    $rootScope.isPlaceholderVisible = false;
                }


                function checkAuthorized(portfolio) {
                    var user = authenticatedUserService.getUser();

                    if ((user && user.id == portfolio.creator.id) || authenticatedUserService.isAdmin() || authenticatedUserService.isModerator()) {
                        return true
                    }

                    console.log("You don't have permission to edit this portfolio");
                    $location.url("/");
                    return false;
                }

                function startAutosave() {
                    autoSaveInterval = $interval(function () {
                        isAutoSaving = true;

                        updatePortfolio();
                    }, 20000);
                }

                $scope.addNewChapter = function () {
                    if (!$scope.portfolio.chapters) {
                        $scope.portfolio.chapters = [];
                    }

                    $scope.portfolio.chapters.push({
                        title: '',
                        subchapters: [],
                        materials: [],
                        openCloseChapter: false
                    });

                    let chapterID = `chapter-${$scope.portfolio.chapters.length - 1}`;

                    $timeout(function () {
                        goToElement(chapterID);
                        focusInput(chapterID);
                    });
                };

                function goToElement(elementID) {
                    var $chapter = angular.element(document.getElementById(elementID));
                    $document.scrollToElement($chapter, 60, 200);
                }

                $scope.$watch(function () {
                    return storageService.getPortfolio();
                }, function (newPortfolio) {
                    $scope.portfolio = newPortfolio;
                });

                $scope.$on('$destroy', function () {
                    $interval.cancel(autoSaveInterval);
                });

                init();
            }
        ]);
