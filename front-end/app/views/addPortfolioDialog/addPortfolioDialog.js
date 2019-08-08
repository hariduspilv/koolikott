angular.module('koolikottApp')
    .controller('addPortfolioDialogController',
        [
            '$scope', '$mdDialog', '$location', '$translate', 'serverCallService', '$rootScope', 'storageService', '$timeout', 'pictureUploadService', '$filter', 'translationService', 'textAngularManager', 'taxonService', 'eventService', 'metadataService', 'authenticatedUserService', 'locals','toastService',
            function ( $scope, $mdDialog, $location, $translate, serverCallService, $rootScope, storageService, $timeout, pictureUploadService, $filter, translationService, textAngularManager, taxonService, eventService, metadataService, authenticatedUserService, locals,toastService) {
                $scope.isSaving = false;
                $scope.showHints = true;
                $scope.isTouched = {};
                $scope.uploadingPicture = false;
                $scope.isUserAuthor = false;

                $scope.$watch('newPicture', onNewPictureChange.bind(this));
                $scope.$watchCollection('invalidPicture', onInvalidPictureChange.bind(this));

                // $rootScope.$on('portfolio:autoSave', getHistoryLogType.bind(this))

                function init() {
                    const portfolio = getPortfolio();
                    $scope.portfolio = portfolio;
                    getMaxPictureSize();

                    metadataService.loadLicenseTypes(setLicenseTypes.bind(this));
                    $scope.newPortfolio = createPortfolio();
                    $scope.newPortfolio.chapters = portfolio.chapters;
                    $scope.newPortfolio.taxons = [{}];
                    $scope.isVocationalEducation = true

                    $scope.mode = locals.mode;
                    if ($scope.mode === 'EDIT' || $scope.mode === 'COPY') {
                        setExistingFields()
                    }
                    /**
                     * Immediately show license type field error in edit-mode if it is not filled to tell the user
                     * that it is now required before any changes may be saved.
                     */
                    if (($scope.mode === 'EDIT' || $scope.mode === 'COPY') && !$scope.newPortfolio.licenseType) {
                        $timeout(() => $scope.addPortfolioForm.licenseType.$setTouched())
                    }
                    /**
                     * Set license type to “All rights reserved” if user chooses “Do not know” option.
                     */
                    $scope.$watch('newPortfolio.licenseType', (selectedValue) => {
                        if (selectedValue && selectedValue.id === 'doNotKnow')
                            $scope.newPortfolio.licenseType = $scope.allRightsReserved
                    })
                    $scope.$watch('newPortfolio.taxons', (selectedValue) => {
                        $scope.isVocationalEducation = isVocational(taxonService, selectedValue);
                        if ($scope.isVocationalEducation) {
                            $scope.newPortfolio.targetGroups = []
                        }
                    }, true)
                    $scope.$watch('newPortfolio.picture.licenseType', (selectedValue) => {
                        if (selectedValue && selectedValue.id === 'doNotKnow')
                            $scope.newPortfolio.picture.licenseType = $scope.allRightsReserved
                    })
                }

                $scope.cancel = function () {
                    $mdDialog.hide();
                };

                $scope.create = function () {
                    $scope.isSaving = true;

                    if ($scope.uploadingPicture) {
                        $timeout($scope.create, 500, false);
                    } else {
                        serverCallService.makePost(
                            "rest/portfolio/create",
                            $scope.newPortfolio,
                            createPortfolioSuccess.bind(null, true),
                            createPortfolioFailed,
                            savePortfolioFinally);
                    }
                };

                $scope.deleteTaxon = function (index) {
                    $scope.newPortfolio.taxons.splice(index, 1);
                };

                function createPortfolioSuccess(isCreate, portfolio) {
                    if (portfolio) {
                        eventService.notify('portfolio:reloadTaxonObject')

                        if (!Array.isArray(portfolio.chapters) || !portfolio.chapters.length) {
                            portfolio.chapters = []
                        }

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

                        $location.url('/kogumik/muuda/' + portfolio.id)
                    }
                }

                $scope.update = function () {
                    updateOrCopy(`rest/portfolio/update`, $scope.update);
                };

                $scope.copy = function () {
                    updateOrCopy(`rest/portfolio/update`, $scope.copy);
                };

                function updateOrCopy(url, func) {
                    $scope.isSaving = true;

                    if ($scope.uploadingPicture) {
                        $timeout(func, 500, false);
                    } else {
                        $scope.portfolio.title = $scope.newPortfolio.title;
                        $scope.portfolio.summary = $scope.newPortfolio.summary;
                        $scope.portfolio.taxons = $scope.newPortfolio.taxons;
                        $scope.portfolio.targetGroups = $scope.newPortfolio.targetGroups;
                        $scope.portfolio.tags = $scope.newPortfolio.tags;
                        $scope.portfolio.licenseType = $scope.newPortfolio.licenseType;
                        $scope.portfolio.saveType = 'MANUAL';

                        if ($scope.newPortfolio.picture) {
                            $scope.portfolio.picture = $scope.newPortfolio.picture;
                        }

                        serverCallService.makePost(
                            url,
                            $scope.portfolio,
                            createPortfolioSuccess.bind(null, false),
                            createPortfolioFailed,
                            savePortfolioFinally);
                    }
                }

                function setExistingFields() {
                    let portfolioClone = angular.copy($scope.portfolio);

                    $scope.newPortfolio.title = portfolioClone.title;
                    $scope.newPortfolio.licenseType = portfolioClone.licenseType;
                    $scope.newPortfolio.summary = portfolioClone.summary;
                    $scope.newPortfolio.taxons = portfolioClone.taxons;
                    $scope.newPortfolio.targetGroups = getTargetGroups(portfolioClone);
                    $scope.newPortfolio.tags = portfolioClone.tags;
                    if (portfolioClone.picture) {
                        $scope.newPortfolio.picture = portfolioClone.picture;
                        const {name, surname} = authenticatedUserService.getUser();
                        if ($scope.newPortfolio.picture.author === `${name} ${surname}`) {
                            $scope.isUserAuthor = true;
                        }
                    }
                }

                $scope.isEmpty = function (object) {
                    return _.isEmpty(object)
                };

                $scope.isValid = function () {
                    return $scope.addPortfolioForm.$valid && hasCorrectTaxon();
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
                }

                function getMaxPictureSize() {
                    serverCallService.makeGet('/rest/picture/maxSize')
                        .then(({data: size}) => {
                            $scope.maxPictureSize = size;
                        }, () => {
                            $scope.maxPictureSize = 10;
                            console.log('Failed to get max picture size, using 10MB as default.');
                        });
                }

                function setLicenseTypes(data) {
                    $scope.licenseTypes = data
                    $scope.doNotKnow = {id: 'doNotKnow'}
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

                function getPortfolio() {
                    let portfolio = storageService.getEmptyPortfolio();
                    if (portfolio) {
                        storageService.setEmptyPortfolio(null);
                        return portfolio;
                    } else {
                        return storageService.getPortfolio();
                    }
                }

                function getTargetGroups(portfolioClone) {
                    return taxonService.getEducationalContext($scope.newPortfolio.taxons[0]).name === 'VOCATIONALEDUCATION' ? [] : portfolioClone.targetGroups;
                }

                function createPortfolioFailed() {
                    console.log('Creating portfolio failed.');
                    toastService.show('PORTFOLIO_SAVE_FAILED', 15000);
                }

                function hasCorrectTaxon() {
                    const portfolio = $scope.newPortfolio;
                    angular.forEach(portfolio.taxons, (key, value) => {
                        if (!isTaxonSet(value)) {
                            return false;
                        }
                    });
                    return hasCorrectTaxon;
                }

                init();
            }
        ]);
