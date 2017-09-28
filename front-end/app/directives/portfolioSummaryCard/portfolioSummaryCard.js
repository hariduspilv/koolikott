'use strict';

angular.module('koolikottApp')
.directive('dopPortfolioSummaryCard', function () {
    return {
        scope: {
            portfolio: '=',
            comment: '=',
            submitClick: "&"
        },
        templateUrl: 'directives/portfolioSummaryCard/portfolioSummaryCard.html',
        controller: ['$scope', 'translationService', '$location', '$mdDialog', '$rootScope', 'authenticatedUserService', '$route', 'dialogService', 'serverCallService', 'toastService', 'storageService', 'targetGroupService', 'taxonService', 'taxonGroupingService', 'eventService', 'portfolioService',
        function ($scope, translationService, $location, $mdDialog, $rootScope, authenticatedUserService, $route, dialogService, serverCallService, toastService, storageService, targetGroupService, taxonService, taxonGroupingService, eventService) {

            $scope.commentsOpen = false;

            $scope.taxonObject = {};

            function init() {
                $scope.pageUrl = $location.absUrl();
                $scope.isViewPortforlioPage = $rootScope.isViewPortforlioPage;
                $scope.isEditPortfolioMode = $rootScope.isEditPortfolioMode;

                eventService.subscribe($scope, 'taxonService:mapInitialized', getTaxonObject);
                eventService.subscribe($scope, 'portfolio:reloadTaxonObject', getTaxonObject);

                eventService.notify('portfolio:reloadTaxonObject');
            }

            // Main purpose of this watch is to handle situations
            // where portfolio is undefined at the moment of init()
            $scope.$watch('portfolio', (newValue, oldValue) => {
                if (newValue !== oldValue) {
                    eventService.notify('portfolio:reloadTaxonObject');
                }
            });

            $scope.isOwner = () => {
                if (!authenticatedUserService.isAuthenticated()) {
                    return false;
                }

                if ($scope.portfolio && $scope.portfolio.creator) {
                    var creatorId = $scope.portfolio.creator.id;
                    var userId = authenticatedUserService.getUser().id;
                    return creatorId === userId;
                }
            };

            $scope.canEdit = () => ($scope.isOwner() || authenticatedUserService.isAdmin() || authenticatedUserService.isModerator()) && !authenticatedUserService.isRestricted();
            $scope.isAdmin = () => authenticatedUserService.isAdmin();
            $scope.isModerator = () => authenticatedUserService.isModerator();
            $scope.isAdminOrModerator = () => authenticatedUserService.isAdmin() || authenticatedUserService.isModerator();
            $scope.isLoggedIn = () => authenticatedUserService.isAuthenticated();
            $scope.isRestricted = () => authenticatedUserService.isRestricted();

            $scope.editPortfolio = () => {
                var portfolioId = $route.current.params.id;
                $location.url("/portfolio/edit?id=" + portfolioId);
            };

            $scope.getPortfolioEducationalContexts = () => {
                var educationalContexts = [];
                if (!$scope.portfolio || !$scope.portfolio.taxons) return;

                $scope.portfolio.taxons.forEach((taxon) => {
                    let edCtx = taxonService.getEducationalContext(taxon);
                    if (edCtx && !educationalContexts.includes(edCtx)) educationalContexts.push(edCtx);
                });

                return educationalContexts;
            };

            $scope.showEditMetadataDialog = () => {
                storageService.setPortfolio($scope.portfolio);

                $mdDialog.show({
                    templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                    controller: 'addPortfolioDialogController'
                });
            };

            $scope.addComment = () => {
                $scope.submitClick();
            };

            $scope.toggleCommentSection = () => {
                $scope.commentsOpen = !$scope.commentsOpen;
            };

            $scope.confirmPortfolioDeletion = () => {
                dialogService.showDeleteConfirmationDialog(
                    'PORTFOLIO_CONFIRM_DELETE_DIALOG_TITLE',
                    'PORTFOLIO_CONFIRM_DELETE_DIALOG_CONTENT',
                    deletePortfolio);
                };

                function getTaxonObject() {
                    if ($scope.portfolio && $scope.portfolio.taxons) {
                        $scope.taxonObject = taxonGroupingService.getTaxonObject($scope.portfolio.taxons);
                    }
                }

                function getSubject(taxon) {
                    return taxonService.getSubject(taxon)
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

                $scope.restorePortfolio = () => {
                    serverCallService.makePost('rest/admin/deleted/portfolio/restore', $scope.portfolio, restoreSuccess, restoreFail);
                };

                $scope.setNotImproper = () => {
                    if ($scope.isAdmin() && $scope.portfolio) {
                        var url = "rest/admin/improper?learningObject=" + $scope.portfolio.id;
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

                $scope.$on("restore:portfolio", () => {
                    $scope.restorePortfolio();
                });

                $scope.$on("delete:portfolio", () => {
                    deletePortfolio();
                });

                $scope.$on("setNotImproper:portfolio", () => {
                    $scope.setNotImproper();
                });

                $scope.$on("markReviewed:portfolio", function () {
                    if ($scope.portfolio && ($scope.isAdmin() || $scope.isModerator()))
                        serverCallService
                            .makePost('rest/admin/firstReview/setReviewed', $scope.portfolio)
                            .then(function () {
                                $rootScope.learningObjectUnreviewed = false
                                $rootScope.$broadcast('dashboard:adminCountsUpdated')
                            })
                })

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

                $scope.getTargetGroups = () => {
                    if ($scope.portfolio) {
                        return targetGroupService.getConcentratedLabelByTargetGroups($scope.portfolio.targetGroups);
                    }
                };

                $scope.isAdminButtonsShowing = () => {
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

                $scope.$watch('portfolio.taxon.id', (newValue, oldValue) => {
                    if (newValue !== oldValue && $scope.portfolio) {
                        $scope.portfolioSubject = getSubject($scope.portfolio.taxon);
                    }
                }, true);

                init();
            }]
        };
    });
