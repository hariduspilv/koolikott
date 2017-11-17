'use strict';

{
const DASHBOARD_VIEW_STATE_MAP = {
    creativeCommon: [
        'CREATIVE_COMMON_LOWER'
    ],
    Youtube: [
        'LICENSETYPE_YOUTUBE'
    ],
    CCBY: [
        'LICENSETYPE_CCBY'
    ],
    VIDEO: [
        'RESOURCETYPE_VIDEO'
    ]
}

class controller extends Controller {
    constructor(...args) {
        super(...args)

        this.$scope.isSaving = false;
        this.$scope.showHints = true;
        this.$scope.creatorIsPublisher = false;
        this.$scope.isUpdateMode = false;
        this.$scope.titleDescriptionGroups = [];
        this.$scope.fileUploaded = false;
        this.$scope.uploadingFile = false;
        this.$scope.review = {};
        this.$scope.additionalInfo = {};
        this.$scope.maxReviewSize = 10;
        this.$scope.charactersRemaining = 850;
        this.$scope.resourceTypeDTO = [];

        let preferredLanguage;
        let uploadingPicture = false;
    }

        /* $timeout is not a function
        // fix for https://github.com/angular/material/issues/6905
        $timeout(function () {
            angular.element(document.querySelector('html')).css('overflow-y', '');
        }); */

        isTypeSelected(resourceType) {
            let materialResourceTypes = $scope.material.resourceTypes;

            var isFound = materialResourceTypes.filter(function (mResourceType) {
                return mResourceType.id == resourceType.id;
            });

            return isFound.length > 0;
        };

        addNewMetadata() {
            $scope.titleDescriptionGroups.forEach(function (item) {
                item.expanded = false
            });

            addNewMetadata();
        };

        addNewAuthor() {
            $scope.material.authors.push({});
            $timeout(function () {
                angular.element('#material-author-' + ($scope.material.authors.length - 1) + '-name').focus();
            });
        };

        deleteAuthor(index) {
            $scope.material.authors.splice(index, 1);
        };

        deleteMetadata(index) {
            $scope.titleDescriptionGroups.splice(index, 1);
        };

        addNewTaxon() {
            var educationalContext = taxonService.getEducationalContext($scope.material.taxons[0]);

            $scope.material.taxons.push(educationalContext);
        };

        deleteTaxon(index) {
            $scope.material.taxons.splice(index, 1);
        };

        getLanguageById(id) {
            return $scope.languages.filter(function (language) {
                return language.id == id;
            })[0].name;
        };

        cancel() {
            $mdDialog.hide();
        };

        addNewPeerReview () {
            $scope.material.peerReviews.push({});
            $timeout(function () {
                angular.element('#material-peerReview-' + ($scope.material.authors.length - 1)).focus();
            });
        };

        deletePeerReview (index) {
            $scope.material.peerReviews.splice(index, 1);
        };

        save () {
            $scope.material.resourceTypes = $scope.resourceTypeDTO;
            $scope.isSaving = true;
            if (uploadingPicture) {
                $timeout($scope.save, 500, false);
            } else {
                let metadata = getTitlesAndDecriptions();
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

        isTouchedOrSubmitted (element) {
            return (element && element.$touched) || ($scope.addMaterialForm && $scope.addMaterialForm.$submitted);
        };

        showCompetencesWarning (element) {
            if ($scope.isTouchedOrSubmitted(element) && $scope.material.keyCompetences) {
                return $scope.material.keyCompetences.length === 0;
            }
        };

        showThemesWarning (element) {
            if ($scope.isTouchedOrSubmitted(element) && $scope.material.crossCurricularThemes) {
                return $scope.material.crossCurricularThemes.length === 0;
            }
        };

        isAuthorOrPublisherSet () {
            return ($scope.material.authors[0].name && $scope.material.authors[0].surname) || ($scope.material.publishers[0] ? $scope.material.publishers[0].name : false);
        };

        isAdmin () {
            return authenticatedUserService.isAdmin();
        };

        getIssueDate(date) {
            return {
                day: date.getDate(),
                month: date.getMonth() + 1,
                year: date.getFullYear()
            };
        }

        getTitlesAndDecriptions() {
            var titles = [];
            var descriptions = [];

            this.titleDescriptionGroups.forEach(function (item) {
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

        isTaxonSet(index) {
            return $scope.material.taxons && $scope.material.taxons[index] && $scope.material.taxons[index].level && $scope.material.taxons[index].level !== ".EducationalContext";
        }

        hasPublisher() {
            return $scope.material.publishers[0] && $scope.material.publishers[0].name;
        }

        hasAuthors() {
            return $scope.material.authors.length > 0 && $scope.material.authors[0].surname;
        }

        areAuthorsValid() {
            var res = true;

            $scope.material.authors.forEach(function (author) {
                if (author.name && !author.surname) res = false;
                else if (author.surname && !author.name) res = false;
            });

            return res;
        }

        translate (item, prefix) {
            return $filter("translate")(prefix + item.toUpperCase());
        };

        /**
         * Search for keyCompetences.
         */
        searchKeyCompetences (query) {
            return query ? $scope.keyCompetences
                .filter(searchFilter(query, "KEY_COMPETENCE_")) : $scope.keyCompetences;
        };

        /**
         * Search for CrossCurricularThemes.
         */
        searchCrossCurricularThemes (query) {
            return query ? $scope.crossCurricularThemes
                .filter(searchFilter(query, "CROSS_CURRICULAR_THEME_")) : $scope.crossCurricularThemes;
        };

        removeFocus (elementId) {
            document.getElementById(elementId).blur();
        };

        autocompleteItemSelected (item, listName, elementId) {

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

        doSuggest (tagName) {
            return suggestService.suggest(tagName, suggestService.getSuggestSystemTagURLbase());
        };

        shouldRemoveNotRelevantFromList(listName) {
            return $scope.material[listName].length > 1 && $scope.material[listName][$scope.material[listName].length - 1].name === 'NOT_RELEVANT';
        }

        removeLastElement(listName) {
            return $scope.material[listName].splice(0, $scope.material[listName].length - 1);
        }

        closeAutocomplete(elementId) {
            // Hide suggestions and blur input to avoid triggering new search
            angular.element(document.querySelector('#' + elementId)).controller('mdAutocomplete').hidden = true;
            document.getElementById(elementId).blur();
        }


        /**
         * Create filter function for a query string
         */
         searchFilter(query, translationPrefix) {
            var lowercaseQuery = angular.lowercase(query);

            return function filterFn(filterSearchObject) {
                var lowercaseItem = $scope.translate(filterSearchObject.name, translationPrefix);
                lowercaseItem = angular.lowercase(lowercaseItem);

                if (lowercaseItem.indexOf(lowercaseQuery) === 0) {
                    return filterSearchObject;
                }
            };
        }

        isBasicOrSecondaryEducation () {
            return $scope.educationalContextId === 2 || $scope.educationalContextId === 3;
        };

        isURLInvalid () {
            if ($scope.addMaterialForm && $scope.addMaterialForm.source && $scope.addMaterialForm.source.$viewValue) {
                $scope.addMaterialForm.source.$setTouched();
                return !!$scope.addMaterialForm.source.$error.url && ($scope.addMaterialForm.source.$viewValue.length > 0);
            }
        };

        sourceIsFocused () {
            $scope.addMaterialForm.source.$setValidity("filenameTooLong", true);
        };

        loadMetadata() {
            metadataService.loadResourceTypes(setResourceTypes);
            metadataService.loadKeyCompetences(setKeyCompetences);
            metadataService.loadCrossCurricularThemes(setCrossCurricularThemes);
        }

        isEmpty (object) {
            return _.isEmpty(object)
        };

        initEmptyMaterial() {
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

        setPublisher() {
            if (authenticatedUserService.isPublisher()) {
                $scope.material.publishers = [{}];
                $scope.material.publishers[0].name = authenticatedUserService.getUser().publisher.name;
                $scope.creatorIsPublisher = true;
            }
        }

        init() {
            if ($scope.material && $scope.material.uploadedFile) {
                $scope.material.uploadedFile.displayName = decodeUTF8($scope.material.uploadedFile.name);
            }

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

        setWatches() {
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
                if (newValue && newValue.$error) {
                    $scope.showErrorOverlay = true;
                    $timeout(function () {
                        $scope.showErrorOverlay = false;
                    }, 6000);
                }
            });

            $scope.$watch('material.taxons[0]', function (newValue, oldValue) {
                if (newValue && newValue.level === taxonService.constants.EDUCATIONAL_CONTEXT && newValue !== oldValue) {
                    $scope.educationalContextId = newValue.id;
                }
            }, false);

            $scope.$watch(function () {
                let a = document.getElementsByClassName("md-datepicker-input");
                if (a[0]) return a[0].value;
            }, function (newDate, oldDate) {
                // if newDate is undefiend, use oldDate
                newDate = newDate ? newDate : oldDate;

                if (newDate !== oldDate || !$scope.material.issueDate) {
                    var dateObj = $mdDateLocale.parseDate(newDate);
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
                    let encodedUrl = encodeURIComponent(newValue);
                    serverCallService.makeGet("rest/material/getOneBySource?source=" + encodedUrl, {},
                        getByUrlSuccess, getByUrlFail);
                }

            }, true);
        }

        getByUrlSuccess(material) {
            if (material && (material.id !== $scope.material.id)) {
                if (material.deleted) {
                    $scope.addMaterialForm.source.$setValidity("deleted", false);
                    toastService.show("MATERIAL_WITH_SAME_SOURCE_IS_DELETED");
                } else {
                    $scope.addMaterialForm.source.$setTouched();
                    $scope.addMaterialForm.source.$setValidity("exists", false);
                    $scope.existingMaterial = material;
                }
            } else {
                processSource($scope.material.source);
            }
        }

        getByUrlFail() {
        }

        processSource(source) {
            if (isYoutubeVideo(source)) {
                youtubeService.getYoutubeData(source)
                    .then((data) => {
                        populateMetadata(data);
                    })
            }
        }

        /**
         * Populated fields:
         *  - title
         *  - description
         *  - picture
         *  - tags
         *  - publisher
         *  - issueDate
         *  - resourceType
         *  - licenseType
         * @param data
         */
        populateMetadata(data) {
            $scope.titleDescriptionGroups = [{
                title: data.snippet.title,
                description: data.snippet.description
            }];

            if (data.snippet.thumbnails) setThumbnail(data.snippet.thumbnails);

            $scope.material.tags = data.snippet.tags;

            let find = $scope.material.publishers.find(p => p.name === data.snippet.channelTitle);
            if (!find) {
                $scope.material.publishers.push({name: data.snippet.channelTitle});
            }

            $scope.issueDate = new Date(data.snippet.publishedAt);

            $scope.material.resourceTypes = [(getResourceTypeByName(RESOURCETYPE_VIDEO))];

            if (data.status.license.toLowerCase() === CREATIVE_COMMON_LOWER) $scope.material.licenseType = getLicenseTypeByName(LICENSETYPE_CCBY);
            else $scope.material.licenseType = getLicenseTypeByName(LICENSETYPE_YOUTUBE);
        }

        setThumbnail(thumbnails) {
            let thumbnailUrl;

            if (thumbnails.maxres) thumbnailUrl = thumbnails.maxres.url;
            else if (thumbnails.standard) thumbnailUrl = thumbnails.standard.url;
            else if (thumbnails.high) thumbnailUrl = thumbnails.high.url;
            else if (thumbnails.medium) thumbnailUrl = thumbnails.medium.url;
            else if (thumbnails.default) thumbnailUrl = thumbnails.default.url;

            pictureUploadService.uploadFromUrl(thumbnailUrl)
                .then(data => {
                    pictureUploadSuccess(data);
                });
        }

        getResourceTypeByName(name) {
            if (!$scope.resourceTypes) return;
            return $scope.resourceTypes.filter(type => {
                return type.name === name
            })[0];
        }

        getLicenseTypeByName(name) {
            if (!$scope.licenseTypes) return;
            return $scope.licenseTypes.filter(license => {
                return license.name === name
            })[0];
        }

        uploadReview (index, file) {
            if (file) {
                $scope.material.peerReviews[index].uploading = true;
                $scope.uploadingReviewId = index;
                fileUploadService.uploadReview(file, reviewUploadSuccess, reviewUploadFailed, reviewUploadFinally);
            }
        };

        pictureUploadSuccess(picture) {
            $scope.material.picture = picture;
        }

        pictureUploadFailed() {
            log('Picture upload failed.');
        }

        pictureUploadFinally() {
            $scope.showErrorOverlay = false;
            uploadingPicture = false;
        }

        fileUploadSuccess(file) {
            $scope.addMaterialForm.source.$setValidity("filenameTooLong", true);
            $scope.material.source = null;
            $scope.fileUploaded = true;
            $scope.uploadingFile = false;
            $scope.material.uploadedFile = file;
            $scope.material.uploadedFile.displayName = decodeUTF8($scope.material.uploadedFile.name);
        }

        fileUploadFailed(response) {
            console.log("File upload failed");
            if (response.data.cause == "filename too long") {
                $scope.addMaterialForm.source.$setValidity("filenameTooLong", false);
                $scope.addMaterialForm.source.$setTouched();
            }
        }

        fileUploadFinally() {
            $scope.uploadingFile = false;
        }

        reviewUploadSuccess(file) {
            $scope.material.peerReviews[$scope.uploadingReviewId].name = file.name;
            $scope.material.peerReviews[$scope.uploadingReviewId].url = file.url;
            $scope.material.peerReviews[$scope.uploadingReviewId].uploaded = true;
        }

        reviewUploadFailed() {
            log('Review upload failed.');
        }

        reviewUploadFinally() {
            $scope.material.peerReviews[$scope.uploadingReviewId].uploading = false;
        }

        preSetMaterial(material) {
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

        prefillMetadataFromPortfolio() {
            if (storageService.getPortfolio()) {
                if (storageService.getPortfolio().taxons) {
                    var taxons = storageService.getPortfolio().taxons.slice();
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

        setLangugeges(data) {
            $scope.languages = data;

            setDefaultMaterialMetadataLanguage();
            setMaterialLanguage();
        }

        setMaterialLanguage() {
            if (!$scope.material.language && preferredLanguage !== null && preferredLanguage !== undefined) {

                if ($scope.titleDescriptionGroups[0] && !$scope.titleDescriptionGroups[0].language) {
                    $scope.titleDescriptionGroups[0].language = preferredLanguage[0];
                }

                $scope.material.language = preferredLanguage[0];
            }
        }

        setLicenseTypes(data) {
            var array = data.filter(function (type) {
                return type.name.toUpperCase() === "ALLRIGHTSRESERVED"
            });
            $scope.licenseTypes = data;
            $scope.allRightsReserved = array[0];
        }

        /**
         * If 'NOT_RELEVANT' is not last item in list
         * then move it
         * @param list
         */
         moveNotRelevantIfNecessary(list) {
            if (list[list.length - 1].name !== "NOT_RELEVANT") {
                var notRelevantIndex = list.map(function (e) {
                    return e.name;
                }).indexOf("NOT_RELEVANT");
                list.move(notRelevantIndex, list.length - 1);
            }
        }

        setCrossCurricularThemes(data) {
            if (!isEmpty(data)) {
                $scope.crossCurricularThemes = data;
                moveNotRelevantIfNecessary($scope.crossCurricularThemes);
            }
        }

        setKeyCompetences(data) {
            if (!isEmpty(data)) {
                $scope.keyCompetences = data;
                moveNotRelevantIfNecessary($scope.keyCompetences);
            }
        }

        saveMaterialSuccess(material) {
            if (!material) {
                saveMaterialFail();
            } else {
                storageService.setMaterial(material)

                //Pass saved material back to material view
                material.source = getSource(material);
                $mdDialog.hide(material);
                if (!$scope.isChapterMaterial) {
                    $location.url('/material?id=' + material.id)
                    $timeout(() => {
                        $scope.isUpdateMode
                            ? $rootScope.learningObjectChanged = true
                            : $rootScope.learningObjectUnreviewed = true
                        $rootScope.$broadcast('dashboard:adminCountsUpdated')
                    })
                }
            }
        }

        saveMaterialFail() {
            console.log('Failed to add material.');
        }

        saveMaterialFinally() {
            $scope.isSaving = false;
        }

        setResourceTypes(data) {
            $scope.resourceTypes = data.sort(function (a, b) {
                if ($filter('translate')(a.name) < $filter('translate')(b.name)) return -1;
                if ($filter('translate')(a.name) > $filter('translate')(b.name)) return 1;
                return 0;
            });
        }

        setDefaultMaterialMetadataLanguage() {
            var userLanguage = translationService.getLanguage();

            preferredLanguage = $scope.languages.filter(function (language) {
                return language == userLanguage;
            });
        }

        addNewMetadata() {
            var metadata = {
                expanded: true,
                title: ''
            };

            $scope.titleDescriptionGroups.push(metadata);
        }

        isMetadataStepValid() {
            return $scope.titleDescriptionGroups.filter(function (metadata) {
                return metadata.title && metadata.title.length !== 0;
            }).length !== 0;
        }

        getMaxPictureSize() {
            serverCallService.makeGet('/rest/picture/maxSize', {}, getMaxPictureSizeSuccess, getMaxPictureSizeFail);
        }

        getMaxPictureSizeSuccess(data) {
            $scope.maxPictureSize = data;
        }

        getMaxPictureSizeFail() {
            $scope.maxPictureSize = 10;
            console.log('Failed to get max picture size, using 10MB as default.');
        }

        getMaxFileSize() {
            serverCallService.makeGet('/rest/uploadedFile/maxSize', {}, getMaxFileSizeSuccess, getMaxFileSizeFail);
        }

        getMaxFileSizeSuccess(data) {
            $scope.maxFileSize = data;
        }

        getMaxFileSizeFail() {
            $scope.maxFileSize = 500;
            console.log('Failed to get max file size, using 500MB as default.');
        }
}

controller.$inject = [
  '$scope',
  '$mdDialog',
  '$mdDateLocale',
  'serverCallService',
  'translationService',
  'metadataService',
  '$filter',
  '$location',
  '$rootScope',
  'authenticatedUserService',
  '$timeout',
  'pictureUploadService',
  'fileUploadService',
  'toastService',
  'suggestService',
  'taxonService',
  'storageService',
  'youtubeService'
]

window.addMaterialDialogController = controller

angular.module('koolikottApp').controller('addMaterialDialogController', controller)
}
