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
            var portfolio = storageService.getPortfolio();

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
            $mdDialog.hide();
        };

        $scope.$watch(function () {
            return $scope.newPicture;
        }, function (newPicture) {
            if (newPicture) {
                uploadingPicture = true;
                pictureUploadService.upload(newPicture, pictureUploadSuccess, pictureUploadFailed, pictureUploadFinally);
            }
        });

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
                    portfolio.chapters = [];
                    portfolio.chapters.push({
                        title: $filter('translate')('PORTFOLIO_DEFAULT_NEW_CHAPTER_TITLE'),
                        subchapters: [],
                        contentRows: [
                            {
                                learningObjects: []
                            }
                        ],
                        openCloseChapter: true
                    });
                    storageService.setPortfolio(portfolio);
                    $rootScope.newPortfolioCreated = true;
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

        $scope.isValid = function () {
            var portfolio = $scope.newPortfolio;
            var hasCorrectTaxon = portfolio.taxons && portfolio.taxons[0] && portfolio.taxons[0].level && portfolio.taxons[0].level !== ".EducationalContext";

            return portfolio.title && portfolio.targetGroups[0] && hasCorrectTaxon;
        };

        $scope.addNewTaxon = function () {
            var educationalContext = taxonService.getEducationalContext($scope.newPortfolio.taxons[0]);

            $scope.newPortfolio.taxons.push(educationalContext);
        };

        function savePortfolioFinally() {
            $scope.saving = false;
        }

        $scope.isSet = function (index) {
            return $scope.newPortfolio.taxons[index] && $scope.newPortfolio.taxons[index].level && $scope.newPortfolio[index].taxon.level !== ".EducationalContext";
        };

        $scope.$watchCollection('invalidPicture', function (newValue, oldValue) {
            if (newValue && newValue.$error) {
                $scope.showErrorOverlay = true;
                $timeout(function () {
                    $scope.showErrorOverlay = false;
                }, 6000);
            }
        });

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
