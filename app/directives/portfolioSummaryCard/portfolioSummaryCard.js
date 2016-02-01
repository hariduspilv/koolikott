define([
    'app',
    'angularAMD',
    'services/translationService',
    'services/authenticatedUserService',
    'services/dialogService',
    'services/serverCallService',
    'services/toastService',
    'services/storageService',
    'directives/copyPermalink/copyPermalink',
    'directives/report/improper/improper',
    'directives/report/brokenLink/brokenLink',
    'directives/recommend/recommend',
    'directives/rating/rating',
    'directives/commentsCard/commentsCard'
], function(app, angularAMD) {
    app.directive('dopPortfolioSummaryCard', ['translationService', '$location', '$mdSidenav', '$mdDialog', '$rootScope', 'authenticatedUserService', '$route', 'dialogService', 'serverCallService', 'toastService', 'storageService', function(translationService, $location, $mdSidenav, $mdDialog, $rootScope, authenticatedUserService, $route, dialogService, serverCallService, toastService, storageService) {
        return {
            scope: {
                portfolio: '=',
                comment: '=',
                submitClick: "&"
            },
            templateUrl: 'directives/portfolioSummaryCard/portfolioSummaryCard.html',
            controller: function($scope, $location) {

                function init() {
                    $scope.isViewPortforlioPage = $rootScope.isViewPortforlioPage;
                    $scope.isEditPortfolioMode = $rootScope.isEditPortfolioMode;
                }

                $scope.getEducationalContext = function() {
                    var educationalContext = $rootScope.taxonUtils.getEducationalContext($scope.portfolio.taxon);
                    if (educationalContext) {
                        return educationalContext.name.toUpperCase();
                    }
                };

                $scope.isOwner = function() {
                    if (!authenticatedUserService.isAuthenticated()) {
                        return false;
                    }

                    if ($scope.portfolio && $scope.portfolio.creator) {
                        var creatorId = $scope.portfolio.creator.id;
                        var userId = authenticatedUserService.getUser().id;
                        return creatorId === userId;
                    }
                };

                $scope.isAdmin = function() {
                    return authenticatedUserService.getUser() && authenticatedUserService.getUser().role === 'ADMIN';
                };

                $scope.isLoggedIn = function() {
                    return authenticatedUserService.isAuthenticated();
                };

                $scope.editPortfolio = function() {
                    var portfolioId = $route.current.params.id;
                    $location.url("/portfolio/edit?id=" + portfolioId);
                };

                $scope.showEditMetadataDialog = function() {
                    storageService.setPortfolio($scope.portfolio);
                    
                    $mdDialog.show(angularAMD.route({
                        templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                        controllerUrl: 'views/addPortfolioDialog/addPortfolioDialog'
                    }));
                };

                $scope.addComment = function() {
                    $scope.submitClick();
                };

                $scope.confirmPortfolioDeletion = function() {
                    dialogService.showConfirmationDialog(
                        'PORTFOLIO_CONFIRM_DELETE_DIALOG_TITLE',
                        'PORTFOLIO_CONFIRM_DELETE_DIALOG_CONTENT',
                        'PORTFOLIO_CONFIRM_DELETE_DIALOG_YES',
                        'PORTFOLIO_CONFIRM_DELETE_DIALOG_NO',
                        deletePortfolio);
                };

                function deletePortfolio() {
                    var url = "rest/portfolio/delete";
                    serverCallService.makePost(url, $scope.portfolio, deletePortfolioSuccess, deletePortfolioFailed);
                }

                function deletePortfolioSuccess() {
                    toastService.showOnRouteChange('PORTFOLIO_DELETED');
                    $location.url('/' + authenticatedUserService.getUser().username);
                }

                function deletePortfolioFailed() {
                    log('Deleting portfolio failed.');
                }

                $scope.restorePortfolio = function() {
                    serverCallService.makePost("rest/portfolio/restore", $scope.portfolio, restoreSuccess, restoreFail);
                };

                function restoreSuccess() {
                    $scope.portfolio.deleted = false;
                    toastService.show('PORTFOLIO_RESTORED');
                }

                function restoreFail() {
                    log("Restoring portfolio failed");
                }

                if ($rootScope.openMetadataDialog) {
                    $scope.showEditMetadataDialog();
                    $rootScope.openMetadataDialog = null;
                }

                init();

            }
        };
    }]);
});