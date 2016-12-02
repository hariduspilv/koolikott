define([
    'app',
    'services/translationService',
    'services/serverCallService',
    'services/alertService',
    'services/authenticatedUserService',
    'services/dialogService',
    'services/toastService',
    'directives/portfolioSummaryCard/portfolioSummaryCard',
    'directives/chapter/chapter',
    'directives/materialBox/materialBox',
    'directives/tableOfContents/tableOfContents'
], function (app) {
    return ['$scope', 'translationService', 'serverCallService', '$route', '$location', 'alertService', '$rootScope', 'authenticatedUserService', 'dialogService', 'toastService', '$mdDialog', '$interval',
        function ($scope, translationService, serverCallService, $route, $location, alertService, $rootScope, authenticatedUserService, dialogService, toastService, $mdDialog, $interval) {
            var isAutoSaving = false;
            var autoSaveInterval;

            function init() {
                if ($rootScope.savedPortfolio) {
                    if (checkAuthorized($rootScope.savedPortfolio)) {
                        setPortfolio($rootScope.savedPortfolio);
                        checkPortfolioVisibility($rootScope.savedPortfolio);
                    }
                } else {
                    getPortfolio(getPortfolioSuccess, getPortfolioFail);
                }

                if ($rootScope.newPortfolioCreated) {
                    $scope.showFirstMessage = true;
                    $rootScope.newPortfolioCreated = false;
                }

                startAutosave();
            }

            function checkPortfolioVisibility(portfolio) {
                if (portfolio.visibility === 'PRIVATE') return;

                showWarning();
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
                    checkPortfolioVisibility(portfolio);
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
                        chapter.materials.forEach(function (material) {
                            material.source = getSource(material);
                        })
                    });
                }

                $rootScope.savedPortfolio = portfolio;
            }

            function showWarning() {
                var setPrivate = function () {
                    $scope.savedPortfolio.visibility = 'PRIVATE';
                    updatePortfolio();
                };

                dialogService.showConfirmationDialog(
                    "{{'PORTFOLIO_MAKE_PRIVATE' | translate}}",
                    "{{'PORTFOLIO_WARNING' | translate}}",
                    "{{'PORTFOLIO_YES' | translate}}",
                    "{{'PORTFOLIO_NO' | translate}}",
                    setPrivate);
            }

            function checkAuthorized(portfolio) {
                var user = authenticatedUserService.getUser();

                if (user.id == portfolio.creator.id || authenticatedUserService.isAdmin() || authenticatedUserService.isModerator()) {
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

            $scope.$watch(function () {
                return $rootScope.savedPortfolio;
            }, function (newPortfolio) {
                $scope.portfolio = newPortfolio;
            });

            $scope.$on('$destroy', function () {
                $interval.cancel(autoSaveInterval);
            });

            init();
        }];
});
