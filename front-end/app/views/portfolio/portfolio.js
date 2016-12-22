'use strict'

angular.module('koolikottApp')
    .controller('portfolioController',
        [
            '$scope', 'translationService', 'serverCallService', '$route', '$location', 'alertService', '$rootScope', 'authenticatedUserService', '$timeout', 'storageService',
            function ($scope, translationService, serverCallService, $route, $location, alertService, $rootScope, authenticatedUserService, $timeout, storageService) {
                var increaseViewCountPromise;

                function init() {
                    if (storageService.getPortfolio()) {
                        setPortfolio(storageService.getPortfolio());
                        increaseViewCount();
                    } else {
                        getPortfolio(getPortfolioSuccess, getPortfolioFail);
                    }

                    $scope.newComment = {};
                }

                function getPortfolio(success, fail) {
                    var portfolioId = $route.current.params.id;
                    serverCallService.makeGet("rest/portfolio?id=" + portfolioId, {}, getPortfolioSuccess, getPortfolioFail);
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
                            var portfolio = createPortfolio($scope.portfolio.id);
                            serverCallService.makePost("rest/portfolio/increaseViewCount", portfolio, function success() {
                            }, function fail() {
                            });
                        }
                    }, 1000);
                }

                $scope.addComment = function () {
                    var url = "rest/comment/portfolio";

                    var portfolio = createPortfolio($scope.portfolio.id);
                    var params = {
                        'comment': $scope.newComment,
                        'portfolio': portfolio
                    };
                    serverCallService.makePost(url, params, addCommentSuccess, addCommentFailed);
                };

                function addCommentSuccess() {
                    $scope.newComment.text = "";

                    getPortfolio(function (portfolio) {
                        $scope.portfolio = portfolio;
                    }, function () {
                        log("Comment success, but failed to reload portfolio.");
                    });
                }

                function addCommentFailed() {
                    log('Adding comment failed.');
                }

                function setPortfolio(portfolio) {
                    $scope.portfolio = portfolio;
                    storageService.setPortfolio(portfolio);

                    if ($scope.portfolio) {
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

                        $rootScope.learningObjectBroken = $scope.portfolio.broken > 0;
                        $rootScope.learningObjectImproper = $scope.portfolio.improper > 0;
                        $rootScope.learningObjectDeleted = $scope.portfolio.deleted == true;
                        $rootScope.learningObjectChanged = $scope.portfolio.changed > 0;

                        if (authenticatedUserService.isAdmin() || authenticatedUserService.isModerator()) {
                            if ($scope.portfolio.improper > 0) {
                                serverCallService.makeGet("rest/impropers", {}, sortImpropers, getItemsFail);
                            }
                        }
                    }
                }

                function getItemsFail() {
                    console.log("Failed to get data");
                }

                function sortImpropers(impropers) {
                    var improper;

                    for (var i = 0; i < impropers.length; i++) {
                        if ($scope.portfolio && impropers[i].learningObject.id === $scope.portfolio.id) {
                            improper = impropers[i];
                        }
                    }

                    $rootScope.setReason(improper.reason);
                }

                $scope.modUser = function () {
                    return !!(authenticatedUserService.isModerator() || authenticatedUserService.isAdmin());
                };
                $scope.$watch(function () {
                    return storageService.getPortfolio();
                }, function (newPortfolio, oldPortfolio) {
                    if (newPortfolio !== oldPortfolio) {
                        setPortfolio(newPortfolio);
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

                $scope.updatePortfolio = function (portfolio) {
                    setPortfolio(portfolio);
                };

                /*
                 * Admin dashboard listeners
                 * Events are sent from errorMessage
                 */
                $scope.$on("restore:learningObject", function () {
                    $scope.$broadcast("restore:portfolio");
                });

                $scope.$on("delete:learningObject", function () {
                    $scope.$broadcast("delete:portfolio");
                });

                $scope.$on("setNotImproper:learningObject", function () {
                    $scope.$broadcast("setNotImproper:portfolio");
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
