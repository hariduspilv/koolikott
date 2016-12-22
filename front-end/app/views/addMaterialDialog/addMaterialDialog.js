'use strict';

angular.module('koolikottApp')
    .controller('addMaterialDialogController',
        [
            '$scope', '$mdDialog', '$mdDateLocale', 'serverCallService', 'translationService', 'metadataService', '$filter', '$location', '$rootScope', 'authenticatedUserService', '$timeout', 'pictureUploadService', 'fileUploadService', 'toastService', 'suggestService', 'taxonService', 'storageService',
            function ($scope, $mdDialog, $mdDateLocale, serverCallService, translationService, metadataService, $filter, $location, $rootScope, authenticatedUserService, $timeout, pictureUploadService, fileUploadService, toastService, suggestService, taxonService, storageService) {
                $scope.isSaving = false;
                $scope.showHints = true;
                $scope.creatorIsPublisher = false;

                // fix for https://github.com/angular/material/issues/6905
                $timeout(function () {
                    angular.element(document.querySelector('html')).css('overflow-y', '');
                });

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
                $scope.review = {};
                $scope.maxReviewSize = 10;

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
                    var educationalContext = taxonService.getEducationalContext($scope.material.taxons[0]);

                    $scope.material.taxons.push(educationalContext);
                };

                $scope.deleteTaxon = function (index) {
                    $scope.material.taxons.splice(index, 1);
                };

                $scope.getLanguageById = function (id) {
                    return $scope.languages.filter(function (language) {
                        return language.id == id;
                    })[0].name;
                };

                $scope.cancel = function () {
                    $mdDialog.hide();
                };

                $scope.addNewPeerReview = function () {
                    $scope.material.peerReviews.push({});
                    $timeout(function () {
                        angular.element('#material-peerReview-' + ($scope.material.authors.length - 1)).focus();
                    });
                };

                $scope.deletePeerReview = function (index) {
                    $scope.material.peerReviews.splice(index, 1);
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
                        if ($scope.material.source) {
                            $scope.material.uploadedFile = null;
                        }

                        if ($scope.material.publishers[0] && !$scope.material.publishers[0].name) {
                            $scope.material.publishers[0] = null;
                        }

                        $scope.material.peerReviews.forEach(function (peerReview, i) {
                            if (!peerReview || !peerReview.url) {
                                $scope.material.peerReviews.splice(i, 1);
                            }
                        });

                        serverCallService.makePut('rest/material', $scope.material, saveMaterialSuccess, saveMaterialFail, saveMaterialFinally);
                    }
                };

                $scope.isTouchedOrSubmitted = function (element) {
                    return (element && element.$touched) || ($scope.addMaterialForm && $scope.addMaterialForm.$submitted);
                };

                $scope.showCompetencesWarning = function (element) {
                    if ($scope.isTouchedOrSubmitted(element) && $scope.material.keyCompetences) {
                        return $scope.material.keyCompetences.length === 0;
                    }
                };

                $scope.showThemesWarning = function (element) {
                    if ($scope.isTouchedOrSubmitted(element) && $scope.material.crossCurricularThemes) {
                        return $scope.material.crossCurricularThemes.length === 0;
                    }
                };

                $scope.isAuthorOrPublisherSet = function () {
                    return ($scope.material.authors[0].name && $scope.material.authors[0].surname) || $scope.material.publishers[0];
                };

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

                $scope.isTabOneValid = function () {
                    return ( $scope.step.isMaterialUrlStepValid || $scope.fileUploaded) && isMetadataStepValid();
                };

                $scope.isTabTwoValid = function () {
                    return $scope.educationalContextId === 4 || ($scope.material.targetGroups && $scope.material.targetGroups.length > 0)
                        && ($scope.isBasicOrSecondaryEducation() ? $scope.material.keyCompetences.length > 0 && $scope.material.crossCurricularThemes.length > 0 : true);
                };

                $scope.isTabThreeValid = function () {
                    return areAuthorsValid() && (hasAuthors() || hasPublisher()) && $scope.material.issueDate.year;
                };

                function hasPublisher() {
                    return $scope.material.publishers[0] && $scope.material.publishers[0].name;
                }

                function hasAuthors() {
                    return $scope.material.authors.length > 0 && $scope.material.authors[0].surname;
                }

                function areAuthorsValid() {
                    var res = true;

                    $scope.material.authors.forEach(function (author) {
                        if (author.name && !author.surname) res = false;
                        else if (author.surname && !author.name) res = false;
                    });

                    return res;
                }


                function isStepValid(index) {
                    switch (index) {
                        case 0:
                            return $scope.isTabOneValid();
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

                $scope.removeFocus = function (elementId) {
                    document.getElementById(elementId).blur();
                };

                $scope.autocompleteItemSelected = function (item, listName, elementId) {

                    if (shouldRemoveNotRelevantFromList(listName)) {
                        $scope.material[listName] = removeLastElement(listName)
                    }

                    if (!item) {
                        closeAutocomplete(elementId);
                    } else {
                        // If 'NOT_RELEVANT' chip exists and new item is selected, replace it
                        if (listContains($scope.material[listName], 'name', 'NOT_RELEVANT') && item.name !== 'NOT_RELEVANT') {
                            $scope.material[listName] = $scope.material[listName].filter(function (e) {
                                return e.name !== "NOT_RELEVANT";
                            });

                            $scope.material[listName].push(item);
                        }
                    }
                };

                $scope.doSuggest = function (tagName) {
                    return suggestService.suggest(tagName, suggestService.getSuggestSystemTagURLbase());
                };

                function shouldRemoveNotRelevantFromList(listName) {
                    return $scope.material[listName].length > 1 && $scope.material[listName][$scope.material[listName].length - 1].name === 'NOT_RELEVANT';
                }

                function removeLastElement(listName) {
                    return $scope.material[listName].splice(0, $scope.material[listName].length - 1);
                }

                function closeAutocomplete(elementId) {
                    // Hide suggestions and blur input to avoid triggering new search
                    angular.element(document.querySelector('#' + elementId)).controller('mdAutocomplete').hidden = true;
                    document.getElementById(elementId).blur();
                }


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

                $scope.isBasicOrSecondaryEducation = function () {
                    return $scope.educationalContextId === 2 || $scope.educationalContextId === 3;
                };

                $scope.isURLInvalid = function () {
                    if ($scope.addMaterialForm && $scope.addMaterialForm.source && $scope.addMaterialForm.source.$viewValue) {
                        $scope.addMaterialForm.source.$setTouched();
                        return !!$scope.addMaterialForm.source.$error.url && ($scope.addMaterialForm.source.$viewValue.length > 0);
                    }
                };

                function loadMetadata() {
                    metadataService.loadResourceTypes(setResourceTypes);
                    metadataService.loadKeyCompetences(setKeyCompetences);
                    metadataService.loadCrossCurricularThemes(setCrossCurricularThemes);
                }

                $scope.isEmpty = function (object) {
                    return _.isEmpty(object)
                };

                function initEmptyMaterial() {
                    $scope.material = {};
                    $scope.material.tags = [];
                    $scope.material.taxons = [{}];
                    $scope.material.authors = [{}];
                    $scope.material.peerReviews = [{}];
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
                    // TODO: Decode cyrillic letters
                    // if ($scope.material && $scope.material.uploadedFile) {
                    //     $scope.material.uploadedFile.name = decodeURIComponent(escape(decodeURIComponent($scope.material.uploadedFile.name)));
                    // }

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

                    if ($scope.material.uploadedFile && $scope.material.uploadedFile.id) $scope.fileUploaded = true;
                    setPublisher();
                    metadataService.loadLanguages(setLangugeges);
                    metadataService.loadLicenseTypes(setLicenseTypes);

                    //Can be loaded later, as they are not in the first tab
                    $timeout(getMaxPictureSize);
                    $timeout(loadMetadata);
                    $timeout(getMaxFileSize);
                    $timeout(setWatches);
                }

                function setWatches() {
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

                    $scope.$watch('addMaterialForm.source.$valid', function (isValid) {
                        $scope.step.isMaterialUrlStepValid = isValid;
                    });

                    $scope.$watch('material.taxons[0]', function (newValue, oldValue) {
                        if (newValue && newValue.level === taxonService.constants.EDUCATIONAL_CONTEXT && newValue !== oldValue) {
                            $scope.educationalContextId = newValue.id;
                        }
                    }, false);
                }

                $scope.$watch(function () {
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

                $scope.$watch('material.source', function (newValue) {
                    if ($scope.addMaterialForm.source) {
                        $scope.addMaterialForm.source.$setValidity("exists", true);
                        $scope.addMaterialForm.source.$setValidity("deleted", true);
                    }

                    if (newValue && $scope.addMaterialForm.source && ($scope.addMaterialForm.source.$error.url !== true)) {
                        var encodedUrl = encodeURIComponent(newValue);
                        serverCallService.makeGet("rest/material/getOneBySource?source=" + encodedUrl, {},
                            getByUrlSuccess, getByUrlFail);
                    }

                }, true);

                function getByUrlSuccess(material) {
                    if (material && (material.id !== $scope.material.id)) {
                        if (material.deleted) {
                            $scope.addMaterialForm.source.$setValidity("deleted", false);
                            toastService.show("MATERIAL_WITH_SAME_SOURCE_IS_DELETED");
                        } else {
                            $scope.addMaterialForm.source.$setTouched();
                            $scope.addMaterialForm.source.$setValidity("exists", false);
                            $scope.existingMaterial = material;
                        }
                    }
                }

                function getByUrlFail() {
                }

                $scope.uploadReview = function (index, file) {
                    if (file) {
                        $scope.material.peerReviews[index].uploading = true;
                        $scope.uploadingReviewId = index;
                        fileUploadService.uploadReview(file, reviewUploadSuccess, reviewUploadFailed, reviewUploadFinally);
                    }
                };

                function pictureUploadSuccess(picture) {
                    $scope.material.picture = picture;
                }

                function pictureUploadFailed() {
                    log('Picture upload failed.');
                }

                function pictureUploadFinally() {
                    $scope.showErrorOverlay = false;
                    uploadingPicture = false;
                }

                function fileUploadSuccess(file) {
                    $scope.material.source = null;
                    $scope.fileUploaded = true;
                    $scope.uploadingFile = false;
                    $scope.material.uploadedFile = file;
                    $scope.step.isMaterialUrlStepValid = true;
                }

                function fileUploadFailed() {
                    log('File upload failed.');
                }

                function fileUploadFinally() {
                    $scope.uploadingFile = false;
                }

                function reviewUploadSuccess(file) {
                    $scope.material.peerReviews[$scope.uploadingReviewId].name = file.name;
                    $scope.material.peerReviews[$scope.uploadingReviewId].url = file.url;
                    $scope.material.peerReviews[$scope.uploadingReviewId].uploaded = true;
                }

                function reviewUploadFailed() {
                    log('Review upload failed.');
                }

                function reviewUploadFinally() {
                    $scope.material.peerReviews[$scope.uploadingReviewId].uploading = false;
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

                    if ($scope.material.uploadedFile) {
                        $scope.material.source = "";
                    }

                    if ($scope.material) {
                        $scope.material.source = getSource($scope.material);
                    }

                    if (!$scope.material.authors[0]) {
                        $scope.material.authors = [{}];
                    }

                    if (!$scope.material.taxons[0]) {
                        $scope.material.taxons = [{}];
                    }

                    if (!$scope.material.peerReviews[0]) {
                        $scope.material.peerReviews = [{}];
                    }

                    var educationalContext = taxonService.getEducationalContext($scope.material.taxons[0]);

                    if (educationalContext) {
                        $scope.educationalContextId = educationalContext.id;
                    }

                    if (!$scope.crossCurricularThemes) $scope.crossCurricularThemes = [];
                    if (!$scope.keyCompetences) $scope.keyCompetences = [];
                }

                function prefillMetadataFromPortfolio() {
                    if (storageService.getPortfolio()) {
                        if (storageService.getPortfolio().taxons) {
                            var taxons = storageService.getPortfolio().taxons;
                            $scope.material.taxons = taxons;
                            var educationalContext = taxonService.getEducationalContext(taxons[0]);

                            if (educationalContext) {
                                $scope.educationalContextId = educationalContext.id;
                            }
                        }

                        if (storageService.getPortfolio().tags) {
                            $scope.material.tags = storageService.getPortfolio().tags.slice();
                        }

                        if (storageService.getPortfolio().targetGroups) {
                            $scope.material.targetGroups = storageService.getPortfolio().targetGroups.slice();
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
                    var array = data.filter(function (type) {
                        return type.name.toUpperCase() === "ALLRIGHTSRESERVED"
                    });
                    $scope.licenceTypes = data;
                    $scope.allRightsReserved = array[0];
                }

                /**
                 * If 'NOT_RELEVANT' is not last item in list
                 * then move it
                 * @param list
                 */
                function moveNotRelevantIfNecessary(list) {
                    if (list[list.length - 1].name !== "NOT_RELEVANT") {
                        var notRelevantIndex = list.map(function (e) {
                            return e.name;
                        }).indexOf("NOT_RELEVANT");
                        list.move(notRelevantIndex, list.length - 1);
                    }
                }

                function setCrossCurricularThemes(data) {
                    if (!isEmpty(data)) {
                        $scope.crossCurricularThemes = data;
                        moveNotRelevantIfNecessary($scope.crossCurricularThemes);
                    }
                }

                function setKeyCompetences(data) {
                    if (!isEmpty(data)) {
                        $scope.keyCompetences = data;
                        moveNotRelevantIfNecessary($scope.keyCompetences);
                    }
                }

                function saveMaterialSuccess(material) {
                    if (!material) {
                        saveMaterialFail();
                    } else {
                        // Tell errorMessage to update changes
                        $rootScope.$broadcast("errorMessage:updateChanged");

                        //Pass saved material back to material view
                        material.source = getSource(material);
                        $mdDialog.hide(material);
                        if (!$scope.isChapterMaterial) {
                            $location.url('/material?id=' + material.id);
                        }
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
                    $scope.maxFileSize = data;
                }

                function getMaxFileSizeFail() {
                    $scope.maxFileSize = 500;
                    console.log('Failed to get max file size, using 500MB as default.');
                }

            }
        ]);
