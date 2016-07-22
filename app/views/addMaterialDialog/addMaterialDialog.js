define([
    'app',
    'services/serverCallService',
    'services/translationService',
    'services/metadataService',
    'services/authenticatedUserService',
    'services/storageService',
    'services/pictureUploadService',
    'services/fileUploadService',
    'directives/validate/validateUrl'
], function (app) {
    return ['$scope', '$mdDialog', '$mdDateLocale', 'serverCallService', 'translationService', 'metadataService', '$filter', '$location', '$rootScope', 'authenticatedUserService', 'storageService', '$timeout', 'pictureUploadService', 'fileUploadService',
        function ($scope, $mdDialog, $mdDateLocale, serverCallService, translationService, metadataService, $filter, $location, $rootScope, authenticatedUserService, storageService, $timeout, pictureUploadService, fileUploadService) {
            $scope.isSaving = false;
            $scope.showHints = true;
            $scope.creatorIsPublisher = false;

            var preferredLanguage;
            var TABS_COUNT = 2;
            var uploadingPicture = false;

            $scope.isUpdateMode = false;
            $scope.step = {};
            $scope.step.currentStep = 0;
            $scope.step.canProceed = false;
            $scope.step.isMaterialUrlStepValid = false;
            $scope.step.isMetadataStepValid = false;
            $scope.titleDescriptionGroups = [];
            $scope.fileUploaded = false;
            $scope.uploadingFile = false;

            init();

            $scope.step.nextStep = function () {
                $scope.step.currentStep += 1;
            };

            $scope.step.previousStep = function () {
                $scope.step.currentStep -= 1;
            };

            $scope.step.isTabDisabled = function (index) {
                if (index == 0)
                    return false;

                return !isStepValid(index - 1);
            };

            $scope.step.canProceed = function () {
                return isStepValid($scope.step.currentStep);
            };

            $scope.step.canCreateMaterial = function () {
                return isStepValid(0) && isStepValid(1) && isStepValid(2);
            };

            $scope.step.isLastStep = function () {
                return $scope.step.currentStep === TABS_COUNT;
            };

            $scope.$watch('addMaterialForm.source.$valid', function (isValid) {
                $scope.step.isMaterialUrlStepValid = isValid;
            });

            $scope.addNewMetadata = function () {
                $scope.titleDescriptionGroups.forEach(function (item) {
                    item.expanded = false
                });

                addNewMetadata();
            };

            $scope.addNewAuthor = function () {
                $scope.material.authors.push({});
                $timeout(function () {
                    angular.element('#material-author-' + ($scope.material.authors.length - 1) + '-name').focus();
                });
            };

            $scope.deleteAuthor = function (index) {
                $scope.material.authors.splice(index, 1);
            };

            $scope.deleteMetadata = function (index) {
                $scope.titleDescriptionGroups.splice(index, 1);
            };

            $scope.addNewTaxon = function () {
                var educationalContext = $rootScope.taxonUtils.getEducationalContext($scope.material.taxons[0]);

                $scope.material.taxons.push(educationalContext);
            };

            $scope.deleteTaxon = function (index) {
                var taxon = $scope.material.taxons[index];
                $rootScope.selectedTopics = $rootScope.selectedTopics.filter(topic => topic.id !== taxon.id);

                $scope.material.taxons.splice(index, 1);
            };

            $scope.getLanguageById = function (id) {
                return $scope.languages.filter(function (language) {
                    return language.id == id;
                })[0].name;
            };

            $scope.$watch('material.taxons[0]', function (newValue, oldValue) {
                if (newValue && newValue.level === $rootScope.taxonUtils.constants.EDUCATIONAL_CONTEXT && newValue !== oldValue) {
                    $scope.educationalContextId = newValue.id;
                    $scope.material.taxons = $scope.material.taxons.slice(0, 1);
                }
            }, false);

            $scope.cancel = function () {
                $mdDialog.hide();
            };

            $scope.save = function () {
                $scope.isSaving = true;

                if (uploadingPicture) {
                    $timeout($scope.save, 500, false);
                } else {
                    var metadata = getTitlesAndDecriptions();
                    $scope.material.titles = metadata.titles;
                    $scope.material.descriptions = metadata.descriptions;
                    $scope.material.type = ".Material";

                    $scope.material.crossCurricularThemes = $scope.material.crossCurricularThemes
                        .filter(theme => theme.name !== "NOT_RELEVANT");

                    $scope.material.keyCompetences = $scope.material.keyCompetences
                        .filter(competence => competence.name !== "NOT_RELEVANT");
                    serverCallService.makePut('rest/material', $scope.material, saveMaterialSuccess, saveMaterialFail, saveMaterialFinally);
                }
            };

            $scope.isTouchedOrSubmitted = element => (element && element.$touched) || ($scope.addMaterialForm && $scope.addMaterialForm.$submitted);

            $scope.showCompetencesWarning = element => {
                if ($scope.isTouchedOrSubmitted(element)) return $scope.material.keyCompetences.length === 0;
            };

            $scope.showThemesWarning = element => {
                if ($scope.isTouchedOrSubmitted(element)) return $scope.material.crossCurricularThemes.length === 0;
            };

            $scope.isAuthorOrPublisherSet = () => ($scope.material.authors[0].name && $scope.material.authors[0].surname) || $scope.material.publishers[0];

            $scope.isAdmin = function () {
                return authenticatedUserService.isAdmin();
            };

            function getIssueDate(date) {
                return {
                    day: date.getDate(),
                    month: date.getMonth() + 1,
                    year: date.getFullYear()
                };
            }


            function getTitlesAndDecriptions() {
                var titles = [];
                var descriptions = [];

                $scope.titleDescriptionGroups.forEach(function (item) {
                    if (item.title) {
                        var title = {
                            language: item.language,
                            text: item.title
                        };

                        titles.push(title);
                    }

                    if (item.description) {
                        var description = {
                            language: item.language,
                            text: item.description
                        };

                        descriptions.push(description);
                    }
                });

                return {
                    titles: titles,
                    descriptions: descriptions
                };
            }

            $scope.isTabTwoValid = () => {
                return $rootScope.selectedTopics && $rootScope.selectedTopics.length > 0 && $scope.material.targetGroups.length > 0
                    && ($scope.isBasicOrSecondaryEducation() ? $scope.material.keyCompetences.length > 0 && $scope.material.crossCurricularThemes.length > 0 : true);
            };

            $scope.isTabThreeValid = () => {
                return (($scope.material.publishers[0] && $scope.material.publishers[0].name)
                    || ($scope.material.authors[0].name && $scope.material.authors[0].surname))
                    && $scope.material.licenseType && $scope.material.issueDate.year;
            };

            function isStepValid(index) {
                switch (index) {
                    case 0:
                        return $scope.step.isMaterialUrlStepValid && isMetadataStepValid();
                    case 1:
                        return $scope.isTabTwoValid();
                    case 2:
                        return $scope.isTabThreeValid();
                    default:
                        return isStepValid(index - 1);
                }
            }

            $scope.translate = function (item, prefix) {
                return $filter("translate")(prefix + item.toUpperCase());
            };

            /**
             * Search for keyCompetences.
             */
            $scope.searchKeyCompetences = function (query) {
                return query ? $scope.keyCompetences
                    .filter(searchFilter(query, "KEY_COMPETENCE_")) : $scope.keyCompetences;
            };

            /**
             * Search for CrossCurricularThemes.
             */
            $scope.searchCrossCurricularThemes = function (query) {
                return query ? $scope.crossCurricularThemes
                    .filter(searchFilter(query, "CROSS_CURRICULAR_THEME_")) : $scope.crossCurricularThemes;
            };

            /**
             * Create filter function for a query string
             */
            function searchFilter(query, translationPrefix) {
                var lowercaseQuery = angular.lowercase(query);

                return function filterFn(filterSearchObject) {
                    var lowercaseItem = $scope.translate(filterSearchObject.name, translationPrefix);
                    lowercaseItem = angular.lowercase(lowercaseItem);

                    if (lowercaseItem.indexOf(lowercaseQuery) === 0) {
                        return filterSearchObject;
                    }
                };
            }

            $scope.isBasicOrSecondaryEducation = () => $scope.educationalContextId === 2 || $scope.educationalContextId === 3;

            function loadMetadata() {
                metadataService.loadLanguages(setLangugeges);
                metadataService.loadLicenseTypes(setLicenseTypes);
                metadataService.loadResourceTypes(setResourceTypes);
                metadataService.loadKeyCompetences(setKeyCompetences);
                metadataService.loadCrossCurricularThemes(setCrossCurricularThemes);
            }

            function initEmptyMaterial() {
                $scope.material = {};
                $scope.material.tags = [];
                $scope.material.taxons = [{}];
                $scope.material.authors = [{}];
                $scope.material.keyCompetences = [];
                $scope.material.crossCurricularThemes = [];
                $scope.material.publishers = [];
                $scope.material.resourceTypes = [];
                $scope.issueDate = new Date();

                addNewMetadata();
            }

            function setPublisher() {
                if (authenticatedUserService.isPublisher()) {
                    $scope.material.publishers = [{}];
                    $scope.material.publishers[0].name = authenticatedUserService.getUser().publisher.name;
                    $scope.creatorIsPublisher = true;
                }
            }

            function init() {
                if ($scope.isChapterMaterial) {
                    var addChapterMaterialUrl = $scope.material.source;
                }

                if ($scope.material && !$scope.isChapterMaterial) {
                    preSetMaterial($scope.material);
                } else {
                    initEmptyMaterial();
                    prefillMetadataFromPortfolio();
                    $scope.material.source = addChapterMaterialUrl;
                }
                setPublisher();
                loadMetadata();
                getMaxPictureSize();
                getMaxFileSize();
                setSelectedTopics();
            }

            function setSelectedTopics() {
                $rootScope.selectedTopics = [];
                $scope.material.taxons.forEach(function (taxon) {
                    const TOPIC = $rootScope.taxonUtils.constants.TOPIC;
                    if (taxon.level === TOPIC) {
                        $rootScope.selectedTopics.push(taxon);
                    } else if ($rootScope.taxonUtils.getTopic(taxon) && $rootScope.taxonUtils.getTopic(taxon).level === TOPIC) {
                        $rootScope.selectedTopics.push($rootScope.taxonUtils.getTopic(taxon));
                    }
                })
            }

            $scope.$watch(() => {
                var a = document.getElementsByClassName("md-datepicker-input");
                if (a[0]) return a[0].value;
            }, function (newDate, oldDate) {
                if (newDate !== oldDate) {
                    var dateObj = $mdDateLocale.parseDate(newDate)
                    $scope.material.issueDate = getIssueDate(dateObj);

                    //Set date for datepicker, which needs a full date
                    if ($scope.material.issueDate && $scope.material.issueDate.year) {
                        $scope.issueDate = dateObj;
                    }
                }
            }, true);

            $scope.$watch(function () {
                return $scope.newPicture;
            }, function (newPicture) {
                if (newPicture) {
                    uploadingPicture = true;
                    pictureUploadService.upload(newPicture, pictureUploadSuccess, pictureUploadFailed, pictureUploadFinally);
                }
            });

            $scope.$watch(function () {
                return $scope.newFile;
            }, function (newFile) {
                if (newFile) {
                    $scope.uploadingFile = true;
                    fileUploadService.upload(newFile, fileUploadSuccess, fileUploadFailed, fileUploadFinally);
                }
            });

            function pictureUploadSuccess(picture) {
                $scope.material.picture = picture;
            }

            function pictureUploadFailed() {
                log('Picture upload failed.');
            }

            function pictureUploadFinally() {
                uploadingPicture = false;
            }

            function fileUploadSuccess(file) {
                $scope.fileUploaded = true;
                $scope.uploadingFile = false;
                $scope.material.uploadedFile = file;
                $scope.material.source = "https://localhost/rest/file/" + file.id;
                $scope.step.isMaterialUrlStepValid = true;
            }

            function fileUploadFailed() {
                log('File upload failed.');
            }

            function fileUploadFinally() {
                uploadingFile = false;
            }

            function preSetMaterial(material) {
                $scope.isUpdateMode = true;
                $scope.material = material;

                for (var i = 0; i < material.titles.length; i++) {
                    if (material.descriptions[i]) {
                        var desc = material.descriptions[i].text;
                    }

                    var meta = {
                        title: material.titles[i].text,
                        description: desc,
                        language: material.titles[i].language
                    };

                    $scope.titleDescriptionGroups.push(meta);
                }
                if (!$scope.titleDescriptionGroups[0]) $scope.titleDescriptionGroups.push({});

                if (material.issueDate) {
                    $scope.issueDate = issueDateToDate(material.issueDate);
                }

                if (!$scope.material.authors[0]) {
                    $scope.material.authors = [{}];
                }

                if (!$scope.material.taxons[0]) {
                    $scope.material.taxons = [{}];
                }

                var educationalContext = $rootScope.taxonUtils.getEducationalContext($scope.material.taxons[0]);

                if (educationalContext) {
                    $scope.educationalContextId = educationalContext.id;
                }
            }

            function prefillMetadataFromPortfolio() {
                if ($rootScope.savedPortfolio) {
                    if ($rootScope.savedPortfolio.taxon) {
                        var taxon = Object.create($rootScope.savedPortfolio.taxon);
                        $scope.material.taxons = [taxon];
                        var educationalContext = $rootScope.taxonUtils.getEducationalContext(taxon);

                        if (educationalContext) {
                            $scope.educationalContextId = educationalContext.id;
                        }
                    }

                    if ($rootScope.savedPortfolio.tags) {
                        $scope.material.tags = $rootScope.savedPortfolio.tags.slice();
                    }

                    if ($rootScope.savedPortfolio.targetGroups) {
                        $scope.material.targetGroups = $rootScope.savedPortfolio.targetGroups.slice();
                    }
                }
            }

            function setLangugeges(data) {
                $scope.languages = data;

                setDefaultMaterialMetadataLanguage();
                setMaterialLanguage();
            }

            function setMaterialLanguage() {
                if (!$scope.material.language && preferredLanguage !== null && preferredLanguage !== undefined) {

                    if ($scope.titleDescriptionGroups[0] && !$scope.titleDescriptionGroups[0].language) {
                        $scope.titleDescriptionGroups[0].language = preferredLanguage[0];
                    }

                    $scope.material.language = preferredLanguage[0];
                }
            }

            function setLicenseTypes(data) {
                $scope.licenceTypes = data;
                $scope.allRightsReserved = data.filter(type => type.name.toUpperCase() === "ALLRIGHTSRESERVED")[0];
            }

            function setCrossCurricularThemes(data) {
                if (!isEmpty(data)) {
                    $scope.crossCurricularThemes = data;
                    if ($scope.crossCurricularThemes[0].name !== "NOT_RELEVANT") {
                        $scope.crossCurricularThemes.unshift({name: "NOT_RELEVANT"})
                    }
                }
            }

            function setKeyCompetences(data) {
                if (!isEmpty(data)) {
                    $scope.keyCompetences = data;
                    if ($scope.keyCompetences[0].name !== "NOT_RELEVANT") {
                        $scope.keyCompetences.unshift({name: "NOT_RELEVANT"})
                    }
                }
            }

            function saveMaterialSuccess(material) {
                $mdDialog.hide(material);

                if (!$scope.isChapterMaterial) {
                    $location.url('/material?materialId=' + material.id);
                }
            }

            function saveMaterialFail() {
                console.log('Failed to add material.');
            }

            function saveMaterialFinally() {
                $scope.saving = false;
            }

            function setResourceTypes(data) {
                $scope.resourceTypes = data.sort(function (a, b) {
                    if ($filter('translate')(a.name) < $filter('translate')(b.name)) return -1;
                    if ($filter('translate')(a.name) > $filter('translate')(b.name)) return 1;
                    return 0;
                });
            }

            function setDefaultMaterialMetadataLanguage() {
                var userLanguage = translationService.getLanguage();

                preferredLanguage = $scope.languages.filter(function (language) {
                    return language == userLanguage;
                });
            }

            function addNewMetadata() {
                var metadata = {
                    expanded: true,
                    title: ''
                };

                $scope.titleDescriptionGroups.push(metadata);
            }

            function isMetadataStepValid() {
                return $scope.titleDescriptionGroups.filter(function (metadata) {
                        return metadata.title && metadata.title.length !== 0;
                    }).length !== 0;
            }

            $scope.$watchCollection('invalidPicture', function (newValue, oldValue) {
                if (newValue !== oldValue) {
                    if (newValue && newValue.length > 0) {
                        if ($scope.newPicture || $scope.material.picture) {
                            $scope.showErrorOverlay = true;
                            $timeout(function () {
                                $scope.showErrorOverlay = false;
                            }, 6000);
                        }
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

            function getMaxFileSize() {
                serverCallService.makeGet('/rest/uploadedFile/maxSize', {}, getMaxFileSizeSuccess, getMaxFileSizeFail);
            }

            function getMaxFileSizeSuccess(data) {
                $scope.maxPictureSize = data;
            }

            function getMaxFileSizeFail() {
                $scope.maxFileSize = 500;
                console.log('Failed to get max file size, using 500MB as default.');
            }

        }];
});
