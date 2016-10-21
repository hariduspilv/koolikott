define([
    'app',
    'directives/copyPermalink/copyPermalink',
    'directives/report/improper/improper',
    'directives/report/brokenLink/brokenLink',
    'directives/recommend/recommend',
    'directives/rating/rating',
    'directives/commentsCard/commentsCard',
    'directives/chapter/chapter',
    'directives/materialBox/materialBox',
    'directives/errorMessage/errorMessage',
    'directives/portfolioSummaryCard/portfolioSummaryCard',
    'services/serverCallService',
    'services/translationService',
    'services/alertService',
    'services/authenticatedUserService'
], function (app) {
    return ['$scope', 'translationService', 'serverCallService', '$route', '$location', 'alertService', '$rootScope', 'authenticatedUserService', '$timeout',
        function ($scope, translationService, serverCallService, $route, $location, alertService, $rootScope, authenticatedUserService, $timeout) {
            var increaseViewCountPromise;

            function init() {
                if ($rootScope.savedPortfolio) {
                    setPortfolio($rootScope.savedPortfolio);
                    increaseViewCount();
                } else {
                    getPortfolio(getPortfolioSuccess, getPortfolioFail);
                }

                $scope.newComment = {};
            }

            function getPortfolio(success, fail) {
                var portfolioId = $route.current.params.id;
                serverCallService.makeGet("rest/portfolio?id=" + portfolioId, {}, success, fail);
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
                $rootScope.savedPortfolio = portfolio;

                if($scope.portfolio) {
                    $rootScope.learningObjectBroken = ($scope.portfolio.broken > 0) ? true : false;
                    $rootScope.learningObjectImproper = ($scope.portfolio.improper > 0) ? true : false;
                    $rootScope.learningObjectDeleted = ($scope.portfolio.deleted == true) ? true : false;

                    if($scope.portfolio.improper > 0) {
                        serverCallService.makeGet("rest/impropers", {}, sortImpropers, getItemsFail);
                    }
                }
            }

            function getItemsFail() {
                console.log("Failed to get data");
            }

            function sortImpropers(impropers) {
                var improper;

                for (var i = 0; i < impropers.length; i++) {
                    if (impropers[i].learningObject.id === $scope.portfolio.id) {
                        improper = impropers[i];
                    }
                }

                $rootScope.setReason(improper.reason);
            }

            $scope.modUser = function() {
                if (authenticatedUserService.isModerator() || authenticatedUserService.isAdmin()) {
                    return true;
                } else {
                    return false;
                }
            }

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

            $scope.isAdmin = function () {
                return authenticatedUserService.isAdmin();
            };

            $scope.isModerator = function () {
                return authenticatedUserService.isModerator();
            };

            init();
        }];
});
