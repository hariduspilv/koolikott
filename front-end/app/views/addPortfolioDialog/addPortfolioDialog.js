

angular.module('koolikottApp')
    .controller('addPortfolioDialogController',
        [
            '$scope', '$mdDialog', '$location', '$translate', 'serverCallService', '$rootScope', 'storageService', '$timeout', 'pictureUploadService', '$filter', 'translationService', 'textAngularManager', 'taxonService', 'eventService', 'metadataService', 'authenticatedUserService',
            function ($scope, $mdDialog, $location, $translate, serverCallService, $rootScope, storageService, $timeout, pictureUploadService, $filter, translationService, textAngularManager, taxonService, eventService, metadataService, authenticatedUserService) {
                $scope.isSaving = false;
                $scope.showHints = true;
                $scope.isTouched = {};
                $scope.uploadingPicture = false;
                $scope.isUserAuthor = false;

                $scope.$watch('newPicture', onNewPictureChange.bind(this));
                $scope.$watchCollection('invalidPicture', onInvalidPictureChange.bind(this));

                function init() {
                    let portfolio = storageService.getEmptyPortfolio();

                    if (!portfolio) portfolio = storageService.getPortfolio();
                    else storageService.setEmptyPortfolio(null);

                    metadataService.loadLicenseTypes(setLicenseTypes.bind(this));
                    $scope.newPortfolio = createPortfolio();
                    $scope.portfolio = portfolio;
                    $scope.newPortfolio.chapters = portfolio.chapters;
                    $scope.newPortfolio.taxons = [{}];

                    if ($scope.portfolio.id != null) {
                        $scope.isEditPortfolio = true;
                        let portfolioClone = angular.copy(portfolio);

                        $scope.newPortfolio.title = portfolioClone.title;
                        $scope.newPortfolio.licenseType = portfolioClone.licenseType;
                        $scope.newPortfolio.summary = portfolioClone.summary;
                        $scope.newPortfolio.taxons = portfolioClone.taxons;
                        if (taxonService.getEducationalContext($scope.newPortfolio.taxons[0]).name === 'VOCATIONALEDUCATION')
                            $scope.newPortfolio.targetGroups = []
                         else
                            $scope.newPortfolio.targetGroups = portfolioClone.targetGroups;

                        $scope.newPortfolio.tags = portfolioClone.tags;
                        if (portfolioClone.picture) {
                            $scope.newPortfolio.picture = portfolioClone.picture;
                            const {name, surname} = authenticatedUserService.getUser();
                            if ($scope.newPortfolio.picture.author === `${name} ${surname}`) {
                                $scope.isUserAuthor = true;
                            }
                        }
                    }

                    /**
                     * Immediately show license type field error in edit-mode if it is not filled to tell the user
                     * that it is now required before any changes may be saved.
                     */
                    if ($scope.isEditPortfolio && ($scope.newPortfolio.licenseType === undefined)) {
                        $timeout(() =>
                            $scope.addPortfolioForm.licenseType.$setTouched()
                        )
                    }

                    /**
                     * Set license type to “All rights reserved” if user chooses “Do not know” option.
                     */
                    $scope.$watch('newPortfolio.licenseType', (selectedValue) => {
                        if (selectedValue && selectedValue.id === 'doNotKnow')
                            $scope.newPortfolio.licenseType = $scope.allRightsReserved
                    })
                    $scope.$watch('newPortfolio.taxons', (selectedValue, previousValue) => {
                        onTaxonsChange(selectedValue, previousValue)
                    }, true)
                    $scope.$watch('newPortfolio.picture.licenseType', (selectedValue) => {
                        if (selectedValue && selectedValue.id === 'doNotKnow')
                            $scope.newPortfolio.picture.licenseType = $scope.allRightsReserved
                    })

                    getMaxPictureSize();
                }

                function isVocational(currentValue) {
                    return !currentValue
                        .map(c => taxonService.getEducationalContext(c))
                        .filter(lo => (lo.name !== 'VOCATIONALEDUCATION'))
                        .length;
                }

                function onTaxonsChange(currentValue, previousValue) {
                    if (currentValue &&
                        currentValue !== previousValue && isVocational(currentValue)) {
                        $scope.isVocationalEducation = true
                        $scope.newPortfolio.targetGroups = []
                    }
                    else if (isVocational(currentValue))
                        $scope.isVocationalEducation = true
                    else
                        $scope.isVocationalEducation = false
                }

                $scope.cancel = function () {
                    $mdDialog.hide();
                };

                $scope.create = function () {
                    $scope.isSaving = true;

                    if ($scope.uploadingPicture) {
                        $timeout($scope.create, 500, false);
                    } else {
                        let url = "rest/portfolio/create";
                        serverCallService.makePost(url, $scope.newPortfolio, createPortfolioSuccess.bind(null, true), createPortfolioFailed, savePortfolioFinally);
                    }
                };

                $scope.deleteTaxon = function (index) {
                    $scope.newPortfolio.taxons.splice(index, 1);
                };

                function createPortfolioSuccess(isCreate, portfolio) {
                    if (portfolio) {
                        eventService.notify('portfolio:reloadTaxonObject')

                        if (!Array.isArray(portfolio.chapters) || !portfolio.chapters.length)
                            portfolio.chapters = []

                        storageService.setPortfolio(portfolio)
                        $mdDialog.hide()

                        if (isCreate) {
                            const unsubscribe = $rootScope.$on('$locationChangeSuccess', () => {
                                unsubscribe()
                                $timeout(() => {
                                    $rootScope.$broadcast('tags:focusInput')
                                    $rootScope.$broadcast('tour:start:editPage:firstTime')
                                })
                            })
                        }

                        $location.url('/portfolio/edit?id=' + portfolio.id)
                    }
                }
                function createPortfolioFailed() {
                    log('Creating portfolio failed.');
                }

                $scope.update = function () {
                    $scope.isSaving = true;

                    if ($scope.uploadingPicture) {
                        $timeout($scope.create, 500, false);
                    } else {
                        let url = "rest/portfolio/update";
                        $scope.portfolio.title = $scope.newPortfolio.title;
                        $scope.portfolio.summary = $scope.newPortfolio.summary;
                        $scope.portfolio.taxons = $scope.newPortfolio.taxons;
                        $scope.portfolio.targetGroups = $scope.newPortfolio.targetGroups;
                        $scope.portfolio.tags = $scope.newPortfolio.tags;
                        $scope.portfolio.licenseType = $scope.newPortfolio.licenseType;

                        if ($scope.newPortfolio.picture) {
                            $scope.portfolio.picture = $scope.newPortfolio.picture;
                        }

                        serverCallService.makePost(url, $scope.portfolio, createPortfolioSuccess.bind(null, false), createPortfolioFailed, savePortfolioFinally);
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

                    return $scope.addPortfolioForm.$valid && hasCorrectTaxon;
                };

                $scope.addNewTaxon = function () {
                    let educationalContext = taxonService.getEducationalContext($scope.newPortfolio.taxons[0]);

                    $scope.newPortfolio.taxons.push(educationalContext);
                };

                function savePortfolioFinally() {
                    $scope.isSaving = false;
                }

                function isTaxonSet(index) {
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

                function setLicenseTypes(data) {
                    $scope.licenseTypes = data
                    $scope.doNotKnow = { id: 'doNotKnow' }
                    $scope.allRightsReserved = data.find(t => t.name === 'allRightsReserved')
                }

                function onNewPictureChange(currentValue) {
                    if (currentValue)
                        this.pictureUpload = pictureUploadService
                            .upload(currentValue)
                            .then(({data}) => {
                                    $scope.newPortfolio.picture.id = data.id;
                                    $scope.newPortfolio.picture.name = data.name;
                                    $scope.showErrorOverlay = false;
                                }, () =>
                                    $scope.showErrorOverlay = false
                            )
                }

                function onInvalidPictureChange(currentValue) {
                    if (currentValue && currentValue.$error) {
                        $scope.showErrorOverlay = true;
                        this.$timeout(() => {
                            this.$scope.showErrorOverlay = false
                        }, 6000)
                    }
                }

                $scope.setAuthorToUser = function () {
                    const {name, surname} = authenticatedUserService.getUser();

                    if ($scope.newPortfolio.picture && $scope.newPortfolio.picture.author !== `${name} ${surname}`) {
                        $scope.newPortfolio.picture.author = `${name} ${surname}`;
                        $scope.isUserAuthor = true;
                    } else {
                        $scope.newPortfolio.picture.author = '';
                        $scope.isUserAuthor = false;
                    }
                };

                init();
            }
        ]);
