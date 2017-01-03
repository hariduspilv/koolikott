'use strict'

angular.module('koolikottApp')
.directive('dopToolbarAddMaterials',
[
    '$translate', 'authenticatedUserService', 'serverCallService', 'toastService', '$q', 'storageService', 'materialService',
    function ($translate, authenticatedUserService, serverCallService, toastService, $q, storageService, materialService) {
        return {
            scope: true,
            templateUrl: 'directives/toolbarAddMaterials/toolbarAddMaterials.html',
            controller: ['$scope', '$rootScope', function ($scope, $rootScope) {

                function init() {
                    if($rootScope.isEditPortfolioMode) {
                        $scope.isPortfolioEdit = true;
                        $scope.portfolio = storageService.getPortfolio();

                        if($rootScope.savedChapter) {
                            $scope.chapter = $rootScope.savedChapter;
                        }
                    }
                }

                $scope.getPortfolioSelectLabel = function() {
                    if($scope.portfolio && $scope.portfolio.title) {
                        return $scope.portfolio.title;
                    } else {
                        return $translate.instant('CHOOSE_PORTFOLIO');
                    }
                };

                $scope.getChapterSelectLabel = function() {
                    if($scope.chapter && $scope.chapter.title) {
                        return $scope.chapter.title;
                    } else {
                        return $translate.instant('CHOOSE_PORTFOLIO_CHAPTER');
                    }
                };

                $scope.addMaterialsToChapter = function(chapter, portfolio) {
                    $scope.isSaving = true;

                    if(chapter && !chapter.contentRows) {
                        chapter.contentRows = [];
                    }

                    if (chapter && chapter.contentRows) {
                        for (let i = 0; i < $rootScope.selectedMaterials.length; i++) {
                            materialService.getMaterialById($rootScope.selectedMaterials[i].id)
                                .then((data) => {
                                    chapter.contentRows.push({learningObjects: [data]});
                                });
                        }
                    }

                    serverCallService.makePost("rest/portfolio/update", portfolio, addMaterialsToChapterSuccess, addMaterialsToChapterFailed);
                    $rootScope.$broadcast('detailedSearch:empty');
                };

                /*
                * Callbacks for serverCallService
                */

                function getUsersPortfoliosSuccess(data) {
                    if (isEmpty(data)) {
                        getUsersPortfoliosFail();
                    } else {
                        $scope.usersPortfolios = data.items;
                        $scope.$broadcast("getUsersPortfolios:done");
                    }
                    $scope.deferred.resolve();
                };

                function getUsersPortfoliosFail() {
                    $scope.deferred.resolve();
                    console.log('Failed to get portfolios.');
                };

                function addMaterialsToChapterSuccess(portfolio) {
                    if (isEmpty(portfolio)) {
                        addMaterialsToChapterFailed();
                    } else {
                        $scope.removeSelection();
                        toastService.show('PORTFOLIO_ADD_MATERIAL_SUCCESS');

                        if ($rootScope.isEditPortfolioMode) {
                            $rootScope.back();
                        }
                    }
                }

                function addMaterialsToChapterFailed() {
                    console.log('Failed to update portfolio.');
                    $scope.isSaving = false;
                    toastService.show('PORTFOLIO_ADD_MATERIAL_FAIL');
                }

                /*
                * End of callbacks
                */

                $scope.removeSelection = function() {
                    for(var i = 0; i < $rootScope.selectedMaterials.length; i++) {
                        $rootScope.selectedMaterials[i].selected = false;
                    }

                    $rootScope.selectedMaterials = [];
                };

                $scope.getUsersPortfolios = function () {

                    if(!$scope.usersPortfolios) {
                        $scope.deferred = $q.defer();
                    }

                    var user = authenticatedUserService.getUser();
                    var params = {
                        'username': user.username
                    };
                    var url = "rest/portfolio/getByCreator";
                    serverCallService.makeGet(url, params, getUsersPortfoliosSuccess, getUsersPortfoliosFail);

                    return $scope.deferred.promise;

                };

                $scope.portfolioSelectChange = function() {
                    $scope.chapter = null;
                };

                init();

            }]
        };
    }
]);
