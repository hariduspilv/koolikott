'use strict'

angular.module('koolikottApp')
    .controller('addPortfolioDialogController',
        [
            '$scope', '$mdDialog', '$location', 'serverCallService', '$rootScope', 'storageService', '$timeout', 'pictureUploadService', '$filter', 'translationService', 'textAngularManager', 'taxonService',
            function ($scope, $mdDialog, $location, serverCallService, $rootScope, storageService, $timeout, pictureUploadService, $filter, translationService, textAngularManager, taxonService) {
                $scope.isSaving = false;
                $scope.showHints = true;
                $scope.isTouched = {};
                $scope.isSummaryVisible = false;

                var uploadingPicture = false;

                function init() {
                    var portfolio = storageService.getEmptyPortfolio();

                    if (!portfolio) portfolio = storageService.getPortfolio();
                    else storageService.setEmptyPortfolio(null);

                    $scope.newPortfolio = createPortfolio();
                    $scope.portfolio = portfolio;
                    $scope.newPortfolio.chapters = portfolio.chapters;
                    $scope.newPortfolio.taxons = [{}];

                    if ($scope.portfolio.id != null) {
                        $scope.isEditPortfolio = true;
                        $scope.isSummaryVisible = true;

                        var portfolioClone = angular.copy(portfolio);

                        $scope.newPortfolio.title = portfolioClone.title;
                        $scope.newPortfolio.summary = portfolioClone.summary;
                        $scope.newPortfolio.taxons = portfolioClone.taxons;
                        $scope.newPortfolio.targetGroups = portfolioClone.targetGroups;
                        $scope.newPortfolio.tags = portfolioClone.tags;
                        $scope.newPortfolio.picture = portfolioClone.picture;
                    }

                    getMaxPictureSize();
                }

                $scope.cancel = function () {
                    $rootScope.newPortfolioCreated = false;
                    $mdDialog.hide();
                };

                $scope.fileUpload = function (files, file, newFile) {
                    if (newFile && newFile[0] && newFile[0].$error) {
                        processInvalidUpload();
                    } else if (newFile && newFile[0] && !newFile[0].$error) {
                        uploadingPicture = true;
                        pictureUploadService.upload(newFile[0], pictureUploadSuccess, pictureUploadFailed, pictureUploadFinally);
                    }
                };

                function processInvalidUpload() {
                    $scope.showErrorOverlay = true;
                    $timeout(function () {
                        $scope.showErrorOverlay = false;
                    }, 6000);
                }

                function pictureUploadSuccess(picture) {
                    $scope.newPortfolio.picture = picture;
                }

                function pictureUploadFailed() {
                    log('Picture upload failed.');
                }

                function pictureUploadFinally() {
                    $scope.showErrorOverlay = false;
                    uploadingPicture = false;
                }

                $scope.create = function () {
                    $scope.saving = true;

                    if (uploadingPicture) {
                        $timeout($scope.create, 500, false);
                    } else {
                        var url = "rest/portfolio/create";
                        serverCallService.makePost(url, $scope.newPortfolio, createPortfolioSuccess, createPortfolioFailed, savePortfolioFinally);
                    }
                };

                $scope.deleteTaxon = function (index) {
                    $scope.newPortfolio.taxons.splice(index, 1);
                };

                function createPortfolioSuccess(portfolio) {
                    if (isEmpty(portfolio)) {
                        createPortfolioFailed();
                    } else {
                        $rootScope.$broadcast("errorMessage:updateChanged");

                        if (!portfolio.chapters || portfolio.chapters.length === 0) {
                            portfolio.chapters = [];
                            portfolio.chapters.push({
                                title: '',
                                subchapters: [],
                                contentRows: [
                                    {
                                        learningObjects: []
                                    }
                                ],
                                openCloseChapter: true
                            });
                        }

                        storageService.setPortfolio(portfolio);

                        $mdDialog.hide();
                        $location.url('/portfolio/edit?id=' + storageService.getPortfolio().id);
                    }
                }

                function createPortfolioFailed() {
                    log('Creating portfolio failed.');
                }

                $scope.update = function () {
                    $scope.saving = true;

                    if (uploadingPicture) {
                        $timeout($scope.create, 500, false);
                    } else {
                        var url = "rest/portfolio/update";
                        $scope.portfolio.title = $scope.newPortfolio.title;
                        $scope.portfolio.summary = $scope.newPortfolio.summary;
                        $scope.portfolio.taxons = $scope.newPortfolio.taxons;
                        $scope.portfolio.targetGroups = $scope.newPortfolio.targetGroups;
                        $scope.portfolio.tags = $scope.newPortfolio.tags;

                        if ($scope.newPortfolio.picture) {
                            $scope.portfolio.picture = $scope.newPortfolio.picture;
                        }

                        serverCallService.makePost(url, $scope.portfolio, createPortfolioSuccess, createPortfolioFailed, savePortfolioFinally);
                    }
                };

                $scope.isEmpty = function (object) {
                    return _.isEmpty(object)
                };

                $scope.isValid = function () {
                    let portfolio = $scope.newPortfolio;

                    let hasCorrectTaxon = true;
                    angular.forEach(portfolio.taxons, (key, value) => {
                        if (!isTaxonSet(value)) {
                            hasCorrectTaxon = false;
                        }
                    });

                    return portfolio.title && portfolio.targetGroups[0] && hasCorrectTaxon;
                };

                $scope.addNewTaxon = function () {
                    var educationalContext = taxonService.getEducationalContext($scope.newPortfolio.taxons[0]);

                    $scope.newPortfolio.taxons.push(educationalContext);
                };

                function savePortfolioFinally() {
                    $scope.saving = false;
                }

                function isTaxonSet (index) {
                    return $scope.newPortfolio.taxons[index] && $scope.newPortfolio.taxons[index].level && $scope.newPortfolio.taxons[index].level !== ".EducationalContext";
                };

                function getMaxPictureSize() {
                    serverCallService.makeGet('/rest/picture/maxSize', {}, getMaxPictureSizeSuccess, getMaxPictureSizeFail);
                }

                function getMaxPictureSizeSuccess(data) {
                    $scope.maxPictureSize = data;
                }

                function getMaxPictureSizeFail() {
                    $scope.maxPictureSize = 10;
                    console.log('Failed to get max picture size, using 10MB as default.');
                }

                $scope.openSummary = function () {
                    $scope.isSummaryVisible = true;

                    $timeout(function () {
                        var editorScope = textAngularManager.retrieveEditor('add-portfolio-description-input').scope;
                        editorScope.displayElements.text.focus();
                    });
                };

                init();
            }
        ]);
