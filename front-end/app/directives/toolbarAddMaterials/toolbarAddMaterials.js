'use strict'

angular.module('koolikottApp')
    .directive('dopToolbarAddMaterials', function () {
        return {
            scope: true,
            templateUrl: 'directives/toolbarAddMaterials/toolbarAddMaterials.html',
            controllerAs: '$ctrl',
            controller: ['$rootScope', '$translate', 'authenticatedUserService', 'serverCallService', 'toastService', '$q', 'storageService', 'materialService', '$mdDialog', '$filter', 'portfolioService',
                function ($rootScope, $translate, authenticatedUserService, serverCallService, toastService, $q, storageService, materialService, $mdDialog, $filter, portfolioService) {

                    let vm = this;

                    function init() {
                        if ($rootScope.isEditPortfolioMode) {
                            vm.isPortfolioEdit = true;
                            vm.portfolio = storageService.getPortfolio();

                            if ($rootScope.savedChapter) {
                                vm.chapter = $rootScope.savedChapter;
                            }
                        } else {
                            loadUserPortfolios();
                        }
                    }

                    function getPortfolioSelectLabel() {
                        if (vm.portfolio != null) {
                            if (vm.portfolio.title) {
                                return vm.portfolio.title;
                            } else if (vm.portfolio === -1) {
                                return $translate.instant('ADD_TO_NEW_PORTFOLIO');
                            }
                        } else {
                            return $translate.instant('CHOOSE_PORTFOLIO');
                        }
                    }

                    function getChapterSelectLabel() {
                        if (vm.chapter != null) {
                            var indexes = vm.chapter.split("_").map((item) => { return parseInt(item) });

                            if (indexes.length > 1) {
                                // Subchapter
                                if (vm.portfolio.chapters[indexes[0]].subchapters[indexes[1]].title === '') {
                                    return $translate.instant('PORTFOLIO_CHAPTER_TITLE_MISSING');
                                }

                                return vm.portfolio.chapters[indexes[0]].subchapters[indexes[1]].title;
                            } else if (indexes[0] === -1) {
                                // New chapter
                                return $translate.instant('ADD_TO_NEW_CHAPTER');
                            } else {
                                // Old chapter
                                if (vm.portfolio.chapters[indexes[0]].title === '') {
                                    return $translate.instant('PORTFOLIO_SUBCHAPTER_TITLE_MISSING');
                                }

                                return vm.portfolio.chapters[indexes[0]].title;
                            }
                        } else {
                            return $translate.instant('CHOOSE_PORTFOLIO_CHAPTER');
                        }
                    }

                    function upgradeMaterials(materials) {
                        return new Promise(function(resolve) {
                            let list = [];
                            let count = 0;
                            materials.forEach(function(item, index, array) {
                                materialService.getMaterialById(item.id)
                                    .then((data) => {
                                        list.push({learningObjects: [data]});
                                        count++;

                                        if (count === array.length) {
                                            if(array.length == list.length) {
                                                resolve(list);
                                            } else {
                                                console.log("Something went horribly wrong")
                                                console.log(array);
                                                console.log(list);
                                            }
                                        }
                                    })
                            })
                        })
                    }

                    function addMaterialsToChapter(chapter, portfolio) {
                        vm.isSaving = true;

                        var tempPortfolio = createPortfolio();
                        var indexes = chapter.split("_").map((item) => { return parseInt(item) });

                        var chapterIndex = indexes[0];
                        var subchapterIndex = indexes[1]; // Undefined if subchapter wasn't selected

                        if (portfolio === -1) {
                            // New portfolio
                            tempPortfolio = createPortfolio();
                        } else {
                            tempPortfolio = portfolio;
                        }

                        if (indexes[0] === -1) {
                            // New chapter in old portfolio
                            if (!tempPortfolio.chapters || tempPortfolio.chapters.length <= 0) {
                                tempPortfolio.chapters = [];
                                chapterIndex = 0;
                            } else {
                                chapterIndex = tempPortfolio.chapters.length;
                            }
                        }

                        // Got portfolio, got indexes

                        upgradeMaterials($rootScope.selectedMaterials).then(function(data) {
                            // Do something with the data
                            // data = contentRows

                            if (subchapterIndex == null) {
                                if (indexes[0] == -1) {
                                    tempPortfolio.chapters[chapterIndex] = {
                                        title: '',
                                        contentRows: []
                                    };
                                }
                                data.forEach((contentRow) => {
                                    tempPortfolio.chapters[chapterIndex].contentRows.push(contentRow);
                                });

                            } else {
                                data.forEach((contentRow) => {
                                    tempPortfolio.chapters[chapterIndex].subchapters[subchapterIndex].contentRows.push(contentRow);
                                });
                            }

                            if (portfolio == -1) {
                                toastService.showOnRouteChange('PORTFOLIO_ADD_MATERIAL_SUCCESS');

                                storageService.setPortfolio(tempPortfolio);

                                $mdDialog.show({
                                    templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                                    controller: 'addPortfolioDialogController'
                                });

                                removeSelection();
                            } else {
                                // If adding to an existing portfolio
                                serverCallService.makePost("rest/portfolio/update", tempPortfolio, addMaterialsToChapterSuccess, addMaterialsToChapterFailed);
                                $rootScope.$broadcast('detailedSearch:empty');
                            }
                        });
                    }

                    /*
                     * Callbacks for serverCallService
                     */

                    function getUsersPortfoliosSuccess(data) {
                        if (isEmpty(data)) {
                            getUsersPortfoliosFail();
                        } else {
                            vm.usersPortfolios = data.items;
                        }
                    }

                    function getUsersPortfoliosFail() {
                        toastService.show('LOADING_PORTFOLIOS_FAIL');
                        removeSelection();
                    }

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
                        for (var i = 0; i < $rootScope.selectedMaterials.length; i++) {
                            $rootScope.selectedMaterials[i].selected = false;
                        }

                        $rootScope.selectedMaterials = [];
                    }

                    function loadUserPortfolios() {
                        var user = authenticatedUserService.getUser();
                        var params = {
                            'username': user.username
                        };
                        var url = "rest/portfolio/getByCreator";
                        serverCallService.makeGet(url, params, getUsersPortfoliosSuccess, getUsersPortfoliosFail);
                    }

                    function portfolioSelectChange() {
                        vm.chapter = null;

                        if(vm.portfolio != -1) {
                            loadPortfolioChapters(vm.portfolio);
                        } else {
                            vm.chapter = "-1";
                        }
                    }

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

                    // Exports
                    vm.loadPortfolioChapters = loadPortfolioChapters;
                    vm.portfolioSelectChange = portfolioSelectChange;
                    vm.removeSelection = removeSelection;
                    vm.addMaterialsToChapter = addMaterialsToChapter;
                    vm.getPortfolioSelectLabel = getPortfolioSelectLabel;
                    vm.getChapterSelectLabel = getChapterSelectLabel;

                    // Run init
                    init();
                }]
        };
    });
