'use strict'

angular.module('koolikottApp')
    .controller('portfolioController',
        [
            '$scope', 'translationService', 'serverCallService', '$route', '$location', 'alertService', '$rootScope', 'authenticatedUserService', '$timeout', 'storageService', 'eventService', 'portfolioService',
            function ($scope, translationService, serverCallService, $route, $location, alertService, $rootScope, authenticatedUserService, $timeout, storageService, eventService, portfolioService) {
                var increaseViewCountPromise;

                function init() {
                    if (storageService.getPortfolio() && storageService.getPortfolio().type !== ".ReducedPortfolio" && storageService.getPortfolio().type !== ".AdminPortfolio") {
                        setPortfolio(storageService.getPortfolio(), true);
                        increaseViewCount();
                    } else {
                        getPortfolio(getPortfolioSuccess, getPortfolioFail);
                    }

                    $scope.newComment = {};
                }

                function getPortfolio() {
                    portfolioService.getPortfolioById($route.current.params.id)
                        .then(getPortfolioSuccess, getPortfolioFail);
                }

                function getPortfolioSuccess(portfolio) {
                    if (isEmpty(portfolio)) {
                        getPortfolioFail();
                    } else {
                        setPortfolio(portfolio);
                        increaseViewCount();
                    }
                }

                function getPortfolioFail() {
                    log('No data returned by getting portfolio.');
                    alertService.setErrorAlert('ERROR_PORTFOLIO_NOT_FOUND');
                    $location.url("/");
                }

                function increaseViewCount() {
                    /*
                     *  It is needed to have it in a timeout because of double call caused by using two different page structure.
                     *  So we cancel it in case the page is destroyed so the new one that will be create makes the valid call.
                     */
                    increaseViewCountPromise = $timeout(function () {
                        if ($scope.portfolio) {
                            portfolioService.increaseViewCount(createPortfolio($scope.portfolio.id))
                        }
                    }, 1000);
                }

                function setPortfolio(portfolio, isLocallyStored = false) {
                    $scope.portfolio = portfolio;
                    storageService.setPortfolio(portfolio);

                    if ($scope.portfolio) {
                        $rootScope.learningObjectPrivate = ["PRIVATE"].includes($scope.portfolio.visibility);
                        $rootScope.learningObjectImproper = $scope.portfolio.improper > 0;
                        $rootScope.learningObjectDeleted = $scope.portfolio.deleted === true;
                        $rootScope.learningObjectChanged = $scope.portfolio.changed > 0;
                        $rootScope.learningObjectUnreviewed = !!$scope.portfolio.unReviewed;
                    }

                    if (!isLocallyStored)
                        portfolio.chapters = (new Controller()).transformChapters(portfolio.chapters)
                }

                $scope.modUser = function () {
                    return !!(authenticatedUserService.isModerator() || authenticatedUserService.isAdmin());
                };

                $scope.$watch(function () {
                    return storageService.getPortfolio();
                }, function (newPortfolio, oldPortfolio) {

                    eventService.notify('portfolio:reloadTaxonObject');

                    if (newPortfolio !== oldPortfolio) {
                        setPortfolio(newPortfolio, true);
                    }
                });

                $scope.$watch(function () {
                    return $location.url().replace(window.location.hash, '');
                }, function (newValue, oldValue) {
                    if (newValue !== oldValue) {
                        $route.reload()
                    }
                }, true);

                $scope.$on('$routeChangeStart', function () {
                    if (!$location.url().startsWith("/portfolio/edit?id=")) {
                        setPortfolio(null);
                    }
                });

                $scope.$on('$destroy', function () {
                    if (increaseViewCountPromise) {
                        $timeout.cancel(increaseViewCountPromise);
                    }
                });

                $scope.$on("tags:updatePortfolio", function (event, value) {
                    if (!_.isEqual(value, $scope.portfolio)) {
                        setPortfolio(value, true);
                    }
                });

                $scope.isAdmin = function () {
                    return authenticatedUserService.isAdmin();
                };

                $scope.isModerator = function () {
                    return authenticatedUserService.isModerator();
                };

                init();
            }
        ]);
