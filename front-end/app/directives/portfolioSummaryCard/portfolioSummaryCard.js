define([
    'app',
    'angularAMD',
    'services/translationService',
    'services/authenticatedUserService',
    'services/dialogService',
    'services/serverCallService',
    'services/toastService',
    'services/storageService',
    'services/targetGroupService',
    'directives/copyPermalink/copyPermalink',
    'directives/report/improper/improper',
    'directives/report/brokenLink/brokenLink',
    'directives/recommend/recommend',
    'directives/rating/rating',
    'directives/tags/tags',
    'directives/commentsCard/commentsCard',
    'directives/favorite/favorite',
    'directives/share/share'
], function (app, angularAMD) {
    app.directive('dopPortfolioSummaryCard', ['translationService', '$location', '$mdSidenav', '$mdDialog', '$rootScope', 'authenticatedUserService', '$route', 'dialogService', 'serverCallService', 'toastService', 'storageService', 'targetGroupService',
        function (translationService, $location, $mdSidenav, $mdDialog, $rootScope, authenticatedUserService, $route, dialogService, serverCallService, toastService, storageService, targetGroupService) {
            return {
                scope: {
                    portfolio: '=',
                    comment: '=',
                    submitClick: "&"
                },
                templateUrl: 'directives/portfolioSummaryCard/portfolioSummaryCard.html',
                controller: function ($scope, $location) {

                    function init() {
                        $scope.pageUrl = $location.absUrl();
                        $scope.isViewPortforlioPage = $rootScope.isViewPortforlioPage;
                        $scope.isEditPortfolioMode = $rootScope.isEditPortfolioMode;
                        if ($scope.portfolio) {
                            $scope.portfolioSubject = getSubject($scope.portfolio.taxon);
                        }
                    }

                    $scope.getEducationalContext = function () {
                        var educationalContext = $rootScope.taxonUtils.getEducationalContext($scope.portfolio.taxon);
                        if (educationalContext) {
                            return educationalContext.name.toUpperCase();
                        }
                    };

                    $scope.isOwner = function () {
                        if (!authenticatedUserService.isAuthenticated()) {
                            return false;
                        }

                        if ($scope.portfolio && $scope.portfolio.creator) {
                            var creatorId = $scope.portfolio.creator.id;
                            var userId = authenticatedUserService.getUser().id;
                            return creatorId === userId;
                        }
                    };

                    $scope.canEdit = function () {
                        return ($scope.isOwner() || authenticatedUserService.isAdmin() || authenticatedUserService.isModerator())
                            && !authenticatedUserService.isRestricted();
                    };

                    $scope.isAdmin = function () {
                        return authenticatedUserService.isAdmin();
                    };

                    $scope.isModerator = function () {
                        return authenticatedUserService.isModerator();
                    };

                    $scope.isAdminOrModerator = function() {
                        return authenticatedUserService.isAdmin() || authenticatedUserService.isModerator();
                    };

                    $scope.isLoggedIn = function () {
                        return authenticatedUserService.isAuthenticated();
                    };

                    $scope.isRestricted = function () {
                        return authenticatedUserService.isRestricted();
                    };

                    $scope.editPortfolio = function () {
                        var portfolioId = $route.current.params.id;
                        $location.url("/portfolio/edit?id=" + portfolioId);
                    };

                    $scope.showEditMetadataDialog = function () {
                        storageService.setPortfolio($scope.portfolio);

                        $mdDialog.show(angularAMD.route({
                            templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                            controllerUrl: 'views/addPortfolioDialog/addPortfolioDialog'
                        }));
                    };

                    $scope.addComment = function () {
                        $scope.submitClick();
                    };

                    $scope.confirmPortfolioDeletion = function () {
                        dialogService.showConfirmationDialog(
                            'PORTFOLIO_CONFIRM_DELETE_DIALOG_TITLE',
                            'PORTFOLIO_CONFIRM_DELETE_DIALOG_CONTENT',
                            'PORTFOLIO_CONFIRM_DELETE_DIALOG_YES',
                            'PORTFOLIO_CONFIRM_DELETE_DIALOG_NO',
                            deletePortfolio);
                    };

                    function getSubject(taxon) {
                        return $rootScope.taxonUtils.getSubject(taxon)
                    }

                    function deletePortfolio() {
                        var url = "rest/portfolio/delete";
                        serverCallService.makePost(url, $scope.portfolio, deletePortfolioSuccess, deletePortfolioFailed);
                    }

                    function deletePortfolioSuccess() {
                        toastService.show('PORTFOLIO_DELETED');
                        $scope.portfolio.deleted = true;
                        $rootScope.learningObjectDeleted = true;
                        $rootScope.$broadcast('dashboard:adminCountsUpdated');
                        $location.path("/");
                    }

                    function deletePortfolioFailed() {
                        log('Deleting portfolio failed.');
                    }

                    $scope.restorePortfolio = function () {
                        serverCallService.makePost("rest/portfolio/restore", $scope.portfolio, restoreSuccess, restoreFail);
                    };

                    $scope.setNotImproper = function () {
                        if ($scope.isAdmin() && $scope.portfolio) {
                            url = "rest/impropers?learningObject=" + $scope.portfolio.id;
                            serverCallService.makeDelete(url, {}, setNotImproperSuccessful, setNotImproperFailed);
                        }
                    };

                    function setNotImproperSuccessful() {
                        $scope.isReported = false;
                        $rootScope.learningObjectImproper = false;
                        $rootScope.$broadcast('dashboard:adminCountsUpdated');
                    }

                    function setNotImproperFailed() {
                        console.log("Setting not improper failed.")
                    }

                    $scope.$on("restore:portfolio", function () {
                        $scope.restorePortfolio();
                    });

                    $scope.$on("delete:portfolio", function () {
                        deletePortfolio();
                    });

                    $scope.$on("setNotImproper:portfolio", function () {
                        $scope.setNotImproper();
                    });

                    function restoreSuccess() {
                        toastService.show('PORTFOLIO_RESTORED');
                        $scope.portfolio.deleted = false;
                        $rootScope.learningObjectDeleted = false;
                        $rootScope.$broadcast('dashboard:adminCountsUpdated');
                    }

                    function restoreFail() {
                        log("Restoring portfolio failed");
                    }

                    if ($rootScope.openMetadataDialog) {
                        $scope.showEditMetadataDialog();
                        $rootScope.openMetadataDialog = null;
                    }

                    $scope.getTargetGroups = function () {
                        if ($scope.portfolio) {
                            return targetGroupService.getLabelByTargetGroupsOrAll($scope.portfolio.targetGroups);
                        }
                    };

                    $scope.isAdminButtonsShowing = function () {
                        return $scope.isAdmin() && (($rootScope.learningObjectDeleted == false
                            && $rootScope.learningObjectImproper == false
                            && $rootScope.learningObjectBroken == true)
                            || ($rootScope.learningObjectDeleted == false
                            && $rootScope.learningObjectBroken == false
                            && $rootScope.learningObjectImproper == true)
                            || ($rootScope.learningObjectDeleted == false
                            && $rootScope.learningObjectBroken == true
                            && $rootScope.learningObjectImproper == true)
                            || ($rootScope.learningObjectDeleted == true));
                    };

                    $scope.$watch('portfolio.taxon.id', function (newValue, oldValue) {
                        if (newValue !== oldValue && $scope.portfolio) {
                            $scope.portfolioSubject = getSubject($scope.portfolio.taxon);
                        }
                    }, true);

                    init();
                }
            };
        }]);
});
