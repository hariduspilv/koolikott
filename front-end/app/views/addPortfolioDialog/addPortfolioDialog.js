define([
    'app',
    'ng-file-upload',
    'services/serverCallService',
    'services/storageService',
    'services/pictureUploadService'
], function (app) {
    return ['$scope', '$mdDialog', '$location', 'serverCallService', '$rootScope', 'storageService', '$timeout', 'pictureUploadService', '$filter', 'translationService', 'textAngularManager',
        function ($scope, $mdDialog, $location, serverCallService, $rootScope, storageService, $timeout, pictureUploadService, $filter, translationService, textAngularManager) {
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

                if ($scope.portfolio.id != null) {
                    $scope.isEditPortfolio = true;

                    var portfolioClone = angular.copy(portfolio);

                    $scope.newPortfolio.title = portfolioClone.title;
                    $scope.newPortfolio.summary = portfolioClone.summary;
                    $scope.newPortfolio.taxon = portfolioClone.taxon;
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

            function createPortfolioSuccess(portfolio) {
                if (isEmpty(portfolio)) {
                    createPortfolioFailed();
                } else {
                    portfolio.chapters = [];
                    portfolio.chapters.push({
                        title: $filter('translate')('PORTFOLIO_DEFAULT_NEW_CHAPTER_TITLE'),
                        subchapters: [],
                        materials: [],
                        openCloseChapter: true
                    });
                    $rootScope.savedPortfolio = portfolio;
                    $mdDialog.hide();
                    $location.url('/portfolio/edit?id=' + $rootScope.savedPortfolio.id);
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
                    $scope.portfolio.taxon = $scope.newPortfolio.taxon;
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
                var hasCorrectTaxon = portfolio.taxon && portfolio.taxon.level && portfolio.taxon.level !== ".EducationalContext";
                return portfolio.title && portfolio.targetGroups[0] && hasCorrectTaxon;
            };

            function savePortfolioFinally() {
                $scope.saving = false;
            }

            $scope.isSet = function () {
                return $scope.newPortfolio.taxon && $scope.newPortfolio.taxon.level && $scope.newPortfolio.taxon.level !== ".EducationalContext";
            };

            $scope.$watchCollection('invalidPicture', function (newValue, oldValue) {
                if (newValue !== oldValue) {
                    if (newValue && newValue.length > 0) {
                        $scope.showErrorOverlay = true;
                        $timeout(function () {
                            $scope.showErrorOverlay = false;
                        }, 6000);
                    }
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

                $timeout(function(){
                    var editorScope = textAngularManager.retrieveEditor('add-portfolio-description-input').scope;
                    editorScope.displayElements.text.trigger('focus');
                }, 0, false);
            }

            init();
        }
    ];
});
