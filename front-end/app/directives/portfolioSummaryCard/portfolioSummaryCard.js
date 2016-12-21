'use strict'

angular.module('koolikottApp')
.directive('dopPortfolioSummaryCard',
[
    'translationService', '$location', '$mdDialog', '$rootScope', 'authenticatedUserService', '$route', 'dialogService', 'serverCallService', 'toastService', 'storageService', 'targetGroupService', 'taxonService',
    function (translationService, $location, $mdDialog, $rootScope, authenticatedUserService, $route, dialogService, serverCallService, toastService, storageService, targetGroupService, taxonService) {
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
                }

                $scope.getEducationalContext = function (taxon) {
                    var educationalContext = taxonService.getEducationalContext(taxon);
                    if (educationalContext) {
                        return educationalContext.name.toUpperCase();
                    }
                };

                $scope.getDomain = function (taxon) {
                    var domain = taxonService.getDomain(taxon);
                    if (domain) {
                        return domain.name.toUpperCase();
                    }
                };

                $scope.getPortfolioSubjects = function () {
                        var subjects = [];
                        if (!$scope.portfolio || !$scope.portfolio.taxons) return;

                        $scope.portfolio.taxons.forEach(function (taxon) {
                            var subject = taxonService.getSubject(taxon);
                            if (subject) subjects.push(subject);
                        });

                        return subjects;
                    };

                    $scope.getSubject = function (taxon) {
                    var subject = taxonService.getSubject(taxon);
                    if (subject) {
                        return subject.name.toUpperCase();
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

                    $mdDialog.show({
                        templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                        controller: 'addPortfolioDialogController'
                    });
                };

                $scope.addComment = function () {
                    $scope.submitClick();
                };

                $scope.confirmPortfolioDeletion = function () {
                    dialogService.showDeleteConfirmationDialog(
                        'PORTFOLIO_CONFIRM_DELETE_DIALOG_TITLE',
                        'PORTFOLIO_CONFIRM_DELETE_DIALOG_CONTENT',
                        deletePortfolio);
                    };

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

                $scope.restorePortfolio = function () {
                    serverCallService.makePost("rest/portfolio/restore", $scope.portfolio, restoreSuccess, restoreFail);
                };

                $scope.setNotImproper = function () {
                    if ($scope.isAdmin() && $scope.portfolio) {
                        var url = "rest/impropers?learningObject=" + $scope.portfolio.id;
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
                        return targetGroupService.getConcentratedLabelByTargetGroups($scope.portfolio.targetGroups);
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
        }
    ]);
