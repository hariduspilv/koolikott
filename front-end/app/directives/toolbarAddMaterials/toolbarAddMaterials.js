'use strict'

angular.module('koolikottApp')
.directive('dopToolbarAddMaterials',
[
    '$translate', 'authenticatedUserService', 'serverCallService', 'toastService', '$q', 'storageService', 'materialService', '$mdDialog', '$filter', 'portfolioService',
    function ($translate, authenticatedUserService, serverCallService, toastService, $q, storageService, materialService, $mdDialog, $filter, portfolioService) {
        return {
            scope: true,
            templateUrl: 'directives/toolbarAddMaterials/toolbarAddMaterials.html',
            controllerAs: 'vm',
            controller: ['$rootScope', function ($rootScope) {

                var vm = this;

                function init() {
                    if($rootScope.isEditPortfolioMode) {
                        vm.isPortfolioEdit = true;
                        vm.portfolio = storageService.getPortfolio();

                        if($rootScope.savedChapter) {
                            vm.chapter = $rootScope.savedChapter;
                        }
                    } else {
                        loadUserPortfolios();
                    }
                }

                function getPortfolioSelectLabel() {
                    if(vm.portfolio && vm.portfolio.title) {
                        return vm.portfolio.title;
                    } else {
                        return $translate.instant('CHOOSE_PORTFOLIO');
                    }
                };

                function getChapterSelectLabel() {
                    if(vm.chapter && vm.chapter.title) {
                        return vm.chapter.title;
                    } else if(vm.chapter == true) {
                        return "Lisa uude peatükki";
                    } else {
                        return $translate.instant('CHOOSE_PORTFOLIO_CHAPTER');
                    }
                };

                function addMaterialsToChapter(chapter, portfolio) {
                    vm.isSaving = true;
                    if(vm.chapter == true) {
                        if(!portfolio.chapters) {
                            portfolio.chapters = [];
                        }

                        let contentRows = [];

                        for (let i = 0; i < $rootScope.selectedMaterials.length; i++) {
                            materialService.getMaterialById($rootScope.selectedMaterials[i].id)
                                .then((data) => {
                                contentRows.push({learningObjects: [data]});

                                // Last cycle
                                if($rootScope.selectedMaterials.length == i+1) {
                                    portfolio.chapters.push({
                                        title: '',
                                        contentRows: contentRows
                                    });
                                    serverCallService.makePost("rest/portfolio/update", portfolio, addMaterialsToChapterSuccess, addMaterialsToChapterFailed);
                                    $rootScope.$broadcast('detailedSearch:empty');
                                }
                            });
                        }
                    } else {
                        if(chapter && !chapter.contentRows) {
                            chapter.contentRows = [];
                        }

                        if (chapter && chapter.contentRows) {
                            for (let i = 0; i < $rootScope.selectedMaterials.length; i++) {
                                materialService.getMaterialById($rootScope.selectedMaterials[i].id)
                                    .then((data) => {
                                    chapter.contentRows.push({learningObjects: [data]});

                                // Last cycle
                                if($rootScope.selectedMaterials.length == i+1) {
                                    serverCallService.makePost("rest/portfolio/update", portfolio, addMaterialsToChapterSuccess, addMaterialsToChapterFailed);
                                    $rootScope.$broadcast('detailedSearch:empty');
                                }
                            });
                            }
                        }
                    }
                };

                function showAddPortfolioDialog() {
                    var emptyPortfolio = createPortfolio();

                    if($rootScope.selectedMaterials) {
                        emptyPortfolio.chapters = [];

                        emptyPortfolio.chapters.push({
                            title: '',
                            contentRows: []
                        });

                        if ($rootScope.selectedMaterials && $rootScope.selectedMaterials.length > 0) {
                            for (let i = 0; i < $rootScope.selectedMaterials.length; i++) {
                                materialService.getMaterialById($rootScope.selectedMaterials[i].id)
                                    .then((data) => {
                                    emptyPortfolio.chapters[0].contentRows.push({learningObjects: [data]});

                                    if($rootScope.selectedMaterials.length == i+1) {
                                        toastService.showOnRouteChange('PORTFOLIO_ADD_MATERIAL_SUCCESS');

                                        storageService.setPortfolio(emptyPortfolio);

                                        $mdDialog.show({
                                            templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                                            controller: 'addPortfolioDialogController'
                                        });

                                        removeSelection();
                                    }
                                });
                            }
                        }
                    }
                };

                /*
                * Callbacks for serverCallService
                */

                function getUsersPortfoliosSuccess(data) {
                    if (isEmpty(data)) {
                        getUsersPortfoliosFail();
                    } else {
                        vm.usersPortfolios = data.items;
                    }
                };

                function getUsersPortfoliosFail() {
                    toastService.show('LOADING_PORTFOLIOS_FAIL');
                    removeSelection();
                };

                function addMaterialsToChapterSuccess(portfolio) {
                    if (isEmpty(portfolio)) {
                        addMaterialsToChapterFailed();
                    } else {
                        removeSelection();
                        toastService.show('PORTFOLIO_ADD_MATERIAL_SUCCESS');

                        if ($rootScope.isEditPortfolioMode) {
                            $rootScope.back();
                        }
                    }
                }

                function addMaterialsToChapterFailed() {
                    console.log('Failed to update portfolio.');
                    vm.isSaving = false;
                    toastService.show('PORTFOLIO_ADD_MATERIAL_FAIL');
                }

                /*
                * End of callbacks
                */

                function removeSelection() {
                    for(var i = 0; i < $rootScope.selectedMaterials.length; i++) {
                        $rootScope.selectedMaterials[i].selected = false;
                    }

                    $rootScope.selectedMaterials = [];
                };

                function loadUserPortfolios() {
                    var user = authenticatedUserService.getUser();
                    var params = {
                        'username': user.username
                    };
                    var url = "rest/portfolio/getByCreator";
                    serverCallService.makeGet(url, params, getUsersPortfoliosSuccess, getUsersPortfoliosFail);
                };

                function portfolioSelectChange() {
                    vm.chapter = null;
                    loadPortfolioChapters(vm.portfolio);
                };

                function loadPortfolioChapters(portfolio) {
                    vm.loadingChapters = true;
                    vm.loadingChaptersFailed = false;
                    portfolioService.getPortfolioById(portfolio.id)
                        .then((data) => {
                        vm.portfolio = data;
                        vm.loadingChapters = false;
                    }, () => {
                        vm.loadingChapters = false;
                        vm.loadingChaptersFailed = true;
                        toastService.show('LOADING_PORTFOLIOS_FAIL');
                    });
                }

                function setChapter(data) {
                    vm.chapter = data;
                }

                // Exports
                vm.setChapter = setChapter;
                vm.loadPortfolioChapters = loadPortfolioChapters;
                vm.portfolioSelectChange = portfolioSelectChange;
                vm.removeSelection = removeSelection;
                vm.showAddPortfolioDialog = showAddPortfolioDialog;
                vm.addMaterialsToChapter = addMaterialsToChapter;
                vm.getPortfolioSelectLabel = getPortfolioSelectLabel;
                vm.getChapterSelectLabel = getChapterSelectLabel;

                // Run init
                init();
            }]
        };
    }
]);
