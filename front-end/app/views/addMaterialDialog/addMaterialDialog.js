'use strict';

{
// const DASHBOARD_VIEW_STATE_MAP = {
//     creativeCommon: [
//         'CREATIVE_COMMON_LOWER'
//     ],
//     LICENSE_TYPES: [
//         'LICENSETYPE_YOUTUBE'
//     ],
//     CCBY: [
//         'LICENSETYPE_CCBY'
//     ],
//     VIDEO: [
//         'RESOURCETYPE_VIDEO'
//     ]
// }

class controller extends Controller {
    constructor(...args) {
        super(...args)

        const CREATIVE_COMMON_LOWER = "creativecommon";
        const LICENSETYPE_YOUTUBE = "Youtube";
        const LICENSETYPE_CCBY = "CCBY";
        const RESOURCETYPE_VIDEO = "VIDEO";

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
        this.$scope.resourceTypeDTO = {};
        this.$scope.languages = [];
        this.$scope.licenseTypes = [];
        this.$scope.resourceTypes = [];
        this.$scope.keyCompetences = [];
        this.$scope.crossCurricularThemes = [];


        this.metadataService.loadLanguages(this.setLangugeges.bind(this));
        this.metadataService.loadLicenseTypes(this.setLicenseTypes.bind(this));
        this.metadataService.loadResourceTypes(this.setResourceTypes.bind(this));
        this.metadataService.loadKeyCompetences(this.setKeyCompetences.bind(this));
        this.metadataService.loadCrossCurricularThemes(this.setCrossCurricularThemes.bind(this));

        // Watches

        // this.$scope.$watch(function () {
        //     return this.$scope.newPicture;
        // }, function (newPicture) {
        //     if (newPicture) {
        //         uploadingPicture = true;
        //         this.pictureUploadService.upload(newPicture, pictureUploadSuccess, pictureUploadFailed, pictureUploadFinally);
        //     }
        // });
        //
        // this.$scope.$watch(function () {
        //     return this.$scope.newFile;
        // }, function (newFile) {
        //     if (newFile) {
        //         this.$scope.uploadingFile = true;
        //         this.fileUploadService.upload(newFile, fileUploadSuccess, fileUploadFailed, fileUploadFinally);
        //     }
        // });
        //
        // this.$scope.$watchCollection('invalidPicture', function (newValue, oldValue) {
        //     if (newValue && newValue.$error) {
        //         this.$scope.showErrorOverlay = true;
        //         this.$timeout(function () {
        //             this.$scope.showErrorOverlay = false;
        //         }, 6000);
        //     }
        // });
        //
        // this.$scope.$watch('material.taxons[0]', function (newValue, oldValue) {
        //     if (newValue && newValue.level === this.taxonService.constants.EDUCATIONAL_CONTEXT && newValue !== oldValue) {
        //         this.$scope.educationalContextId = newValue.id;
        //     }
        // }, false);
        //
        // this.$scope.$watch(function () {
        //     let a = document.getElementsByClassName("md-datepicker-input");
        //     if (a[0]) return a[0].value;
        // }, function (newDate, oldDate) {
        //     // if newDate is undefiend, use oldDate
        //     newDate = newDate ? newDate : oldDate;
        //
        //     if (newDate !== oldDate || !this.$scope.material.issueDate) {
        //         var dateObj = this.$mdDateLocale.parseDate(newDate);
        //         this.$scope.material.issueDate = getIssueDate(dateObj);
        //
        //         //Set date for datepicker, which needs a full date
        //         if (this.$scope.material.issueDate && this.$scope.material.issueDate.year) {
        //             this.$scope.issueDate = dateObj;
        //         }
        //     }
        // }, true);
        //
        // this.$scope.$watch('material.source', function (newValue) {
        //     if (this.$scope.addMaterialForm.source) {
        //         this.$scope.addMaterialForm.source.$setValidity("exists", true);
        //         this.$scope.addMaterialForm.source.$setValidity("deleted", true);
        //     }
        //
        //
        //     if (newValue && this.$scope.addMaterialForm.source && (this.$scope.addMaterialForm.source.$error.url !== true)) {
        //         let encodedUrl = encodeURIComponent(newValue);
        //         this.serverCallService.makeGet("rest/material/getOneBySource?source=" + encodedUrl, {},
        //             getByUrlSuccess, getByUrlFail);
        //     }
        //
        // }, true);

        let preferredLanguage;
        let uploadingPicture = false;

        if (this.$scope.material && this.$scope.material.uploadedFile) {
            this.$scope.material.uploadedFile.displayName = decodeUTF8(this.$scope.material.uploadedFile.name);
        }

        if (this.$scope.isChapterMaterial) {
            var addChapterMaterialUrl = this.$scope.material.source;
        }

        if (this.$scope.material && !this.$scope.isChapterMaterial) {
            preSetMaterial(this.$scope.material);
        } else {
            this.initEmptyMaterial();
            this.prefillMetadataFromPortfolio();
            this.$scope.material.source = addChapterMaterialUrl;
        }

        if (this.$scope.material.uploadedFile && this.$scope.material.uploadedFile.id)
            this.$scope.fileUploaded = true;
            this.setPublisher();

        // fix for https://github.com/angular/material/issues/6905
         this.$timeout(function () {
             angular.element(document.querySelector('html')).css('overflow-y', '');
        });
    }

        isTypeSelected(resourceType) {
            let materialResourceTypes = this.$scope.material.resourceTypes;

            var isFound = materialResourceTypes.filter(function (mResourceType) {
                return mResourceType.id == resourceType.id;
            });

            return isFound.length > 0;
        };

        addNewMetadata() {
            this.$scope.titleDescriptionGroups.forEach(function (item) {
                item.expanded = false
            });

            this.addNewMetadata();
        };

        addNewAuthor() {
            this.$scope.material.authors.push({});

            var authorsLength = this.$scope.material.authors.length;

            this.$timeout(function () {
                angular.element('#material-author-' + (authorsLength - 1) + '-name').focus();
            });
        };

        deleteAuthor(index) {
            this.$scope.material.authors.splice(index, 1);
        };

        deleteMetadata(index) {
            this.$scope.titleDescriptionGroups.splice(index, 1);
        };

        addNewTaxon() {
            var educationalContext = this.taxonService.getEducationalContext(this.$scope.material.taxons[0]);

            this.$scope.material.taxons.push(educationalContext);
        };

        deleteTaxon(index) {
            this.$scope.material.taxons.splice(index, 1);
        };

        getLanguageById(id) {
            return this.$scope.languages.filter(function (language) {
                return language.id == id;
            })[0].name;
        };

        cancel() {
            this.$mdDialog.hide();
        };

        addNewPeerReview() {
            this.$scope.material.peerReviews.push({});

            var peerReviewLength = this.$scope.material.peerReviews.length;

            this.$timeout(function () {
                angular.element('#material-peerReview-' + (peerReviewLength - 1)).focus();
            });
        };

        deletePeerReview(index) {
            this.$scope.material.peerReviews.splice(index, 1);
        };

        save() {
            this.$scope.material.resourceTypes = this.$scope.resourceTypeDTO;
            this.$scope.isSaving = true;
            if (this.uploadingPicture) {
                this.$timeout(this.$scope.save, 500, false);
            } else {
                let metadata = this.getTitlesAndDecriptions();
                this.$scope.material.titles = metadata.titles;
                this.$scope.material.descriptions = metadata.descriptions;
                this.$scope.material.type = ".Material";
                if (this.$scope.material.source) {
                    this.$scope.material.uploadedFile = null;
                }

                if (this.$scope.material.publishers[0] && !this.$scope.material.publishers[0].name) {
                    this.$scope.material.publishers[0] = null;
                }

                this.$scope.material.peerReviews.forEach(function (peerReview, i) {
                    if (!peerReview || !peerReview.url) {
                        this.$scope.material.peerReviews.splice(i, 1);
                    }
                });
                this.serverCallService.makePut('rest/material', this.$scope.material, saveMaterialSuccess, saveMaterialFail, saveMaterialFinally);
            }
        };

        isTouchedOrSubmitted (element) {
            return (element && element.$touched) || (this.$scope.addMaterialForm && this.$scope.addMaterialForm.$submitted);
        };

        showCompetencesWarning (element) {
            if (this.$scope.isTouchedOrSubmitted(element) && this.$scope.material.keyCompetences) {
                return this.$scope.material.keyCompetences.length === 0;
            }
        };

        showThemesWarning (element) {
            if (this.$scope.isTouchedOrSubmitted(element) && this.$scope.material.crossCurricularThemes) {
                return this.$scope.material.crossCurricularThemes.length === 0;
            }
        };

        isAuthorOrPublisherSet () {
            return (this.$scope.material.authors[0].name && this.$scope.material.authors[0].surname) || (this.$scope.material.publishers[0] ? this.$scope.material.publishers[0].name : false);
        };

        isAdmin () {
            return this.authenticatedUserService.isAdmin();
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
            return this.$scope.material.taxons && this.$scope.material.taxons[index] && this.$scope.material.taxons[index].level && this.$scope.material.taxons[index].level !== ".EducationalContext";
        }

        hasPublisher() {
            return this.$scope.material.publishers[0] && this.$scope.material.publishers[0].name;
        }

        hasAuthors() {
            return this.$scope.material.authors.length > 0 && this.$scope.material.authors[0].surname;
        }

        areAuthorsValid() {
            var res = true;

            this.$scope.material.authors.forEach(function (author) {
                if (author.name && !author.surname) res = false;
                else if (author.surname && !author.name) res = false;
            });

            return res;
        }

        translate (item, prefix) {
            return this.$filter("translate")(prefix + item.toUpperCase());
        };

        /**
         * Search for keyCompetences.
         */
        searchKeyCompetences (query) {
            return query ? this.$scope.keyCompetences
                .filter(this.searchFilter(query, "KEY_COMPETENCE_")) : this.$scope.keyCompetences;
        };

        /**
         * Search for CrossCurricularThemes.
         */
        searchCrossCurricularThemes (query) {
            return query ? this.$scope.crossCurricularThemes
                .filter(this.searchFilter(query, "CROSS_CURRICULAR_THEME_")) : this.$scope.crossCurricularThemes;
        };

        removeFocus (elementId) {
            document.getElementById(elementId).blur();
        };

        autocompleteItemSelected (item, listName, elementId) {

            if (this.shouldRemoveNotRelevantFromList(listName)) {
                this.$scope.material[listName] = this.removeLastElement(listName)
            }

            if (!item) {
                this.closeAutocomplete(elementId);
            } else {
                // If 'NOT_RELEVANT' chip exists and new item is selected, replace it
                if (this.listContains(this.$scope.material[listName], 'name', 'NOT_RELEVANT') && item.name !== 'NOT_RELEVANT') {
                    this.$scope.material[listName] = this.$scope.material[listName].filter(function (e) {
                        return e.name !== "NOT_RELEVANT";
                    });

                    this.$scope.material[listName].push(item);
                }
            }
        };

        doSuggest (tagName) {
            return this.suggestService.suggest(tagName, this.suggestService.getSuggestSystemTagURLbase());
        };

        shouldRemoveNotRelevantFromList(listName) {
            return this.$scope.material[listName].length > 1 && this.$scope.material[listName][this.$scope.material[listName].length - 1].name === 'NOT_RELEVANT';
        }

        removeLastElement(listName) {
            return this.$scope.material[listName].splice(0, this.$scope.material[listName].length - 1);
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
                var lowercaseItem = this.$scope.translate(filterSearchObject.name, translationPrefix);
                lowercaseItem = angular.lowercase(lowercaseItem);

                if (lowercaseItem.indexOf(lowercaseQuery) === 0) {
                    return filterSearchObject;
                }
            };
        }

        isBasicOrSecondaryEducation () {
            return this.$scope.educationalContextId === 2 || this.$scope.educationalContextId === 3;
        };

        isURLInvalid () {
            if (this.$scope.addMaterialForm && this.$scope.addMaterialForm.source && this.$scope.addMaterialForm.source.$viewValue) {
                this.$scope.addMaterialForm.source.$setTouched();
                return !!this.$scope.addMaterialForm.source.$error.url && (this.$scope.addMaterialForm.source.$viewValue.length > 0);
            }
        };

        sourceIsFocused () {
            this.$scope.addMaterialForm.source.$setValidity("filenameTooLong", true);
        };

        isEmpty (object) {
            return _.isEmpty(object)
        };

        initEmptyMaterial() {
            this.$scope.material = {};
            this.$scope.material.tags = [];
            this.$scope.material.taxons = [{}];
            this.$scope.material.authors = [{}];
            this.$scope.material.peerReviews = [{}];
            this.$scope.material.keyCompetences = [];
            this.$scope.material.crossCurricularThemes = [];
            this.$scope.material.publishers = [];
            this.$scope.material.resourceTypes = [];
            this.$scope.issueDate = new Date();

            this.addNewMetadata();
        }

        setPublisher() {
            if (this.authenticatedUserService.isPublisher()) {
                this.$scope.material.publishers = [{}];
                this.$scope.material.publishers[0].name = this.authenticatedUserService.getUser().publisher.name;
                this.$scope.creatorIsPublisher = true;
            }
        }

        getByUrlSuccess(material) {
            if (material && (material.id !== this.$scope.material.id)) {
                if (material.deleted) {
                    this.$scope.addMaterialForm.source.$setValidity("deleted", false);
                    this.toastService.show("MATERIAL_WITH_SAME_SOURCE_IS_DELETED");
                } else {
                    this.$scope.addMaterialForm.source.$setTouched();
                    this.$scope.addMaterialForm.source.$setValidity("exists", false);
                    this.$scope.existingMaterial = material;
                }
            } else {
                this.processSource(this.$scope.material.source);
            }
        }

        getByUrlFail() {
        }

        processSource(source) {
            if (this.isYoutubeVideo(source)) {
                this.youtubeService.getYoutubeData(source)
                    .then((data) => {
                        this.populateMetadata(data);
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
            this.$scope.titleDescriptionGroups = [{
                title: data.snippet.title,
                description: data.snippet.description
            }];

            if (data.snippet.thumbnails) this.setThumbnail(data.snippet.thumbnails);

            this.$scope.material.tags = data.snippet.tags;

            let find = this.$scope.material.publishers.find(p => p.name === data.snippet.channelTitle);
            if (!find) {
                this.$scope.material.publishers.push({na5me: data.snippet.channelTitle});
            }

            this.$scope.issueDate = new Date(data.snippet.publishedAt);

            this.$scope.material.resourceTypes = [(this.getResourceTypeByName(RESOURCETYPE_VIDEO))];

            if (data.status.license.toLowerCase() === CREATIVE_COMMON_LOWER) this.$scope.material.licenseType = this.getLicenseTypeByName(LICENSETYPE_CCBY);
            else this.$scope.material.licenseType = this.getLicenseTypeByName(LICENSETYPE_YOUTUBE);
        }

        setThumbnail(thumbnails) {
            let thumbnailUrl;

            if (thumbnails.maxres) thumbnailUrl = thumbnails.maxres.url;
            else if (thumbnails.standard) thumbnailUrl = thumbnails.standard.url;
            else if (thumbnails.high) thumbnailUrl = thumbnails.high.url;
            else if (thumbnails.medium) thumbnailUrl = thumbnails.medium.url;
            else if (thumbnails.default) thumbnailUrl = thumbnails.default.url;

            this.pictureUploadService.uploadFromUrl(thumbnailUrl)
                .then(data => {
                    this.pictureUploadSuccess(data);
                });
        }

        getResourceTypeByName(name) {
            if (!this.$scope.resourceTypes) return;
            return this.$scope.resourceTypes.filter(type => {
                return type.name === name
            })[0];
        }

        getLicenseTypeByName(name) {
            if (!this.$scope.licenseTypes) return;
            return this.$scope.licenseTypes.filter(license => {
                return license.name === name
            })[0];
        }

        uploadReview (index, file) {
            if (file) {
                this.$scope.material.peerReviews[index].uploading = true;
                this.$scope.uploadingReviewId = index;
                this.fileUploadService.uploadReview(file, reviewUploadSuccess, reviewUploadFailed, reviewUploadFinally);
            }
        };

        pictureUploadSuccess(picture) {
            this.$scope.material.picture = picture;
        }

        pictureUploadFailed() {
            log('Picture upload failed.');
        }

        pictureUploadFinally() {
            this.$scope.showErrorOverlay = false;
            this.uploadingPicture = false;
        }

        fileUploadSuccess(file) {
            this.$scope.addMaterialForm.source.$setValidity("filenameTooLong", true);
            this.$scope.material.source = null;
            this.$scope.fileUploaded = true;
            this.$scope.uploadingFile = false;
            this.$scope.material.uploadedFile = file;
            this.$scope.material.uploadedFile.displayName = decodeUTF8(this.$scope.material.uploadedFile.name);
        }

        fileUploadFailed(response) {
            console.log("File upload failed");
            if (response.data.cause == "filename too long") {
                this.$scope.addMaterialForm.source.$setValidity("filenameTooLong", false);
                this.$scope.addMaterialForm.source.$setTouched();
            }
        }

        fileUploadFinally() {
            this.$scope.uploadingFile = false;
        }

        reviewUploadSuccess(file) {
            this.$scope.material.peerReviews[this.$scope.uploadingReviewId].name = file.name;
            this.$scope.material.peerReviews[this.$scope.uploadingReviewId].url = file.url;
            this.$scope.material.peerReviews[this.$scope.uploadingReviewId].uploaded = true;
        }

        reviewUploadFailed() {
            log('Review upload failed.');
        }

        reviewUploadFinally() {
            this.$scope.material.peerReviews[this.$scope.uploadingReviewId].uploading = false;
        }

        preSetMaterial(material) {
            this.$scope.isUpdateMode = true;
            this.$scope.material = material;

            for (var i = 0; i < material.titles.length; i++) {
                if (material.descriptions[i]) {
                    var desc = material.descriptions[i].text;
                }

                var meta = {
                    title: material.titles[i].text,
                    description: desc,
                    language: material.titles[i].language
                };

                this.$scope.titleDescriptionGroups.push(meta);
            }
            if (!this.$scope.titleDescriptionGroups[0]) this.$scope.titleDescriptionGroups.push({});

            if (material.issueDate) {
                this.$scope.issueDate = this.issueDateToDate(material.issueDate);
            }

            if (this.$scope.material.uploadedFile) {
                this.$scope.material.source = "";
            }

            if (this.$scope.material) {
                this.$scope.material.source = this.getSource(this.$scope.material);
            }

            if (!this.$scope.material.authors[0]) {
                this.$scope.material.authors = [{}];
            }

            if (!this.$scope.material.taxons[0]) {
                this.$scope.material.taxons = [{}];
            }

            if (!this.$scope.material.peerReviews[0]) {
                this.$scope.material.peerReviews = [{}];
            }

            var educationalContext = this.taxonService.getEducationalContext(this.$scope.material.taxons[0]);

            if (educationalContext) {
                this.$scope.educationalContextId = educationalContext.id;
            }

            if (!this.$scope.material.crossCurricularThemes) this.$scope.material.crossCurricularThemes = [];
            if (!this.$scope.material.keyCompetences) this.$scope.material.keyCompetences = [];
        }

        prefillMetadataFromPortfolio() {
            if (this.storageService.getPortfolio()) {
                if (this.storageService.getPortfolio().taxons) {
                    var taxons = this.storageService.getPortfolio().taxons.slice();
                    this.$scope.material.taxons = taxons;
                    var educationalContext = this.taxonService.getEducationalContext(taxons[0]);

                    if (educationalContext) {
                        this.$scope.educationalContextId = educationalContext.id;
                    }
                }

                if (this.storageService.getPortfolio().tags) {
                    this.$scope.material.tags = this.storageService.getPortfolio().tags.slice();
                }

                if (this.storageService.getPortfolio().targetGroups) {
                    this.$scope.material.targetGroups = this.storageService.getPortfolio().targetGroups.slice();
                }
            }
        }

        setLangugeges(data) {
            this.$scope.languages = data;

            this.setDefaultMaterialMetadataLanguage();
            this.setMaterialLanguage();
        }

        setMaterialLanguage() {
            if (!this.$scope.material.language && this.preferredLanguage !== null && this.preferredLanguage !== undefined) {

                if (this.$scope.titleDescriptionGroups[0] && !this.$scope.titleDescriptionGroups[0].language) {
                    this.$scope.titleDescriptionGroups[0].language = this.preferredLanguage[0];
                }
                this.$scope.material.language = this.preferredLanguage[0];
            }
        }

        setLicenseTypes(data) {
            var array = data.filter(function (type) {
                return type.name.toUpperCase() === "ALLRIGHTSRESERVED"
            });
            this.$scope.licenseTypes = data;
            this.$scope.allRightsReserved = array[0];
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
                this.$scope.crossCurricularThemes = data;
                this.moveNotRelevantIfNecessary(this.$scope.crossCurricularThemes);
            }
        }

        setKeyCompetences(data) {
            if (!isEmpty(data)) {
                this.$scope.keyCompetences = data;
                this.moveNotRelevantIfNecessary(this.$scope.keyCompetences);
            }
        }

        saveMaterialSuccess(material) {
            if (!material) {
                this.saveMaterialFail();
            } else {
                this.storageService.setMaterial(material)

                //Pass saved material back to material view
                material.source = getSource(material);
                this.$mdDialog.hide(material);
                if (!this.$scope.isChapterMaterial) {
                    this.$location.url('/material?id=' + material.id)
                    this.$timeout(() => {
                        this.$scope.isUpdateMode
                            ? this.$rootScope.learningObjectChanged = true
                            : this.$rootScope.learningObjectUnreviewed = true
                        this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
                    })
                }
            }
        }

        saveMaterialFail() {
            console.log('Failed to add material.');
        }

        saveMaterialFinally() {
            this.$scope.isSaving = false;
        }

        setResourceTypes(data) {
            this.$scope.resourceTypes = data.sort(function (a, b) {
                if (this.$filter('translate')(a.name) < this.$filter('translate')(b.name)) return -1;
                if (this.$filter('translate')(a.name) > this.$filter('translate')(b.name)) return 1;
                return 0;
            });
        }

        setDefaultMaterialMetadataLanguage() {
            var userLanguage = this.translationService.getLanguage();

            this.preferredLanguage = this.$scope.languages.filter(function (language) {
                return language == userLanguage;
            });
        }

        addNewMetadata() {
            var metadata = {
                expanded: true,
                title: ''
            };

            this.$scope.titleDescriptionGroups.push(metadata);
        }

        isMetadataStepValid() {
            return this.$scope.titleDescriptionGroups.filter(function (metadata) {
                return metadata.title && metadata.title.length !== 0;
            }).length !== 0;
        }

        getMaxPictureSize() {
            this.serverCallService.makeGet('/rest/picture/maxSize', {}, getMaxPictureSizeSuccess, getMaxPictureSizeFail);
        }

        getMaxPictureSizeSuccess(data) {
            this.$scope.maxPictureSize = data;
        }

        getMaxPictureSizeFail() {
            this.$scope.maxPictureSize = 10;
            console.log('Failed to get max picture size, using 10MB as default.');
        }

        getMaxFileSize() {
            this.serverCallService.makeGet('/rest/uploadedFile/maxSize', {}, getMaxFileSizeSuccess, getMaxFileSizeFail);
        }

        getMaxFileSizeSuccess(data) {
            this.$scope.maxFileSize = data;
        }

        getMaxFileSizeFail() {
            this.$scope.maxFileSize = 500;
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
