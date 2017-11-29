'use strict';

{
class controller extends Controller {
    constructor(...args) {
        super(...args)

        this.keyCompetences = []
        this.crossCurricularThemes = []
        this.titleAndSummaryLanguages = ['ET', 'EN', 'RU']

        this.$scope.activeTitleAndDescriptionLanguage = this.titleAndSummaryLanguages[0]
        this.$scope.isSaving = false
        this.$scope.showHints = true
        this.$scope.creatorIsPublisher = false
        this.$scope.isUpdateMode = false
        this.$scope.fileUploaded = false
        this.$scope.uploadingFile = false
        this.$scope.review = {}
        this.$scope.isUserAuthor = false
        this.$scope.maxReviewSize = 10
        this.$scope.charactersRemaining = 850
        this.$scope.languages = []
        this.$scope.licenseTypes = []
        this.$scope.resourceTypes = []
        this.$scope.selectedLanguage = this.translationService.getLanguage()

        this.fetchMaxPictureSize()
        this.fetchMaxFileSize()

        const addChapterMaterialUrl = this.$scope.material && this.$scope.material.source

        if (this.$scope.material && !this.$scope.isChapterMaterial) {
            this.preSetMaterial()
        } else {
            this.initEmptyMaterial()
            this.prefillMetadataFromPortfolio()

            if (addChapterMaterialUrl)
                this.$scope.material.source = addChapterMaterialUrl
        }

        if (this.$scope.material.uploadedFile) {
            this.$scope.material.uploadedFile.displayName = decodeUTF8(this.$scope.material.uploadedFile.name)
            this.$scope.fileUploaded = true
        }

        this.metadataService.loadLanguages(this.setMaterialSourceLangugeges.bind(this))
        this.metadataService.loadLicenseTypes(this.setLicenseTypes.bind(this))
        this.metadataService.loadResourceTypes(this.setResourceTypes.bind(this))
        this.metadataService.loadKeyCompetences(this.setKeyCompetences.bind(this))
        this.metadataService.loadCrossCurricularThemes(this.setCrossCurricularThemes.bind(this))
        this.setPublisher()

        this.$scope.$watch('newPicture', this.onNewPictureChange.bind(this))
        this.$scope.$watch('newFile', this.onNewFileChange.bind(this))
        this.$scope.$watch('material.taxons[0]', this.onTaxonsChange.bind(this), false)
        this.$scope.$watch('material.source', this.onMaterialSourceChange.bind(this), true)
        this.$scope.$watchCollection('invalidPicture', this.onInvalidPictureChange.bind(this))
        this.$scope.$watch(
            () => (document.querySelector('.md-datepicker-input') || {}).value,
            this.onDatePickerChange.bind(this),
            true
        )

        // fix for https://github.com/angular/material/issues/6905
        this.$timeout(() =>
            angular.element(document.querySelector('html')).css('overflow-y', '')
        )
    }
    onNewPictureChange(currentValue) {
        if (currentValue)
            this.pictureUpload = this.pictureUploadService
                .upload(currentValue)
                .then(({ data: id, data: name }) => {
                    this.$scope.material.picture.id = id
                    this.$scope.material.picture.id = name
                    this.$scope.showErrorOverlay = false
                }, () =>
                    this.$scope.showErrorOverlay = false
                )
    }
    onNewFileChange(currentValue) {
        if (currentValue) {
            this.$scope.uploadingFile = true
            this.fileUpload = this.fileUploadService
                .upload(currentValue)
                .then(({ data }) => {
                    this.$scope.addMaterialForm.source.$setValidity('filenameTooLong', true)
                    this.$scope.material.source = null
                    this.$scope.fileUploaded = true
                    this.$scope.uploadingFile = false
                    this.$scope.material.uploadedFile = data
                    this.$scope.material.uploadedFile.displayName = decodeUTF8(this.$scope.material.uploadedFile.name)
                    this.$scope.uploadingFile = false
                }, ({ data }) => {
                    if (data.cause == 'filename too long') {
                        this.$scope.addMaterialForm.source.$setValidity('filenameTooLong', false)
                        this.$scope.addMaterialForm.source.$setTouched()
                    }
                    this.$scope.uploadingFile = false
                })
        }
    }
    onTaxonsChange(currentValue, previousValue) {
        if (currentValue &&
            currentValue !== previousValue &&
            currentValue.level === this.taxonService.constants.EDUCATIONAL_CONTEXT
        )
            this.$scope.educationalContextId = currentValue.id
    }
    onMaterialSourceChange(currentValue, previousValue) {
        if (this.$scope.addMaterialForm.source) {
            this.$scope.addMaterialForm.source.$setValidity('exists', true)
            this.$scope.addMaterialForm.source.$setValidity('deleted', true)
        }
        if (currentValue &&
            currentValue !== previousValue &&
            this.$scope.addMaterialForm.source &&
            this.$scope.addMaterialForm.source.$error.url !== true &&
            (!this.locals.isEditMode || currentValue !== this.originalSource)
        )
            this.serverCallService
                .makeGet('rest/material/getOneBySource?source=' + encodeURIComponent(currentValue))
                .then(({ data: material }) => {
                    if (!material)
                        return this.processSource($scope.material.source)

                    if (material.deleted) {
                        this.$scope.addMaterialForm.source.$setValidity('deleted', false)
                        this.toastService.show('MATERIAL_WITH_SAME_SOURCE_IS_DELETED')
                    } else {
                        this.$scope.addMaterialForm.source.$setTouched()
                        this.$scope.addMaterialForm.source.$setValidity('exists', false)
                        this.$scope.existingMaterial = material
                    }
                })
    }
    onInvalidPictureChange(currentValue) {
        if (currentValue && currentValue.$error) {
            this.$scope.showErrorOverlay = true
            this.$timeout(() => {
                this.$scope.showErrorOverlay = false
            }, 6000)
        }
    }
    onDatePickerChange(currentDate, previousDate) {
        currentDate = currentDate || previousDate

        if (currentDate !== previousDate || !this.$scope.material.issueDate) {
            const dateObj = this.$mdDateLocale.parseDate(currentDate)
            this.$scope.material.issueDate = this.getIssueDate(dateObj)

            //Set date for datepicker, which needs a full date
            if (this.$scope.material.issueDate && this.$scope.material.issueDate.year)
                this.$scope.issueDate = dateObj
        }
    }
    isTypeSelected({ id }) {
        const { resourceTypes } = this.$scope.material
        return Array.isArray(resourceTypes) && !!resourceTypes.find(t => t.id === id)
    }
    addNewAuthor() {
        this.$scope.material.authors.push({})
        this.$timeout(() =>
            angular
                .element(`#material-author-${this.$scope.material.authors.length - 1}-name`)
                .focus()
        )
    }
    setAuthorToUser() {
        const { name } = this.authenticatedUserService.getUser()

        if (this.$scope.material.picture.author !== name) {
            this.$scope.material.picture.author = name
            this.$scope.isUserAuthor = true
        } else {
            this.$scope.material.picture.author = ''
            this.$scope.isUserAuthor = false
        }
    }
    addNewTaxon() {
        this.$scope.material.taxons.push(
            this.taxonService.getEducationalContext(
                this.$scope.material.taxons &&
                this.$scope.material.taxons[0]
            )
        )
    }
    addNewPeerReview() {
        this.$scope.material.peerReviews.push({})
        this.$timeout(() =>
            angular
                .element(`#material-peerReview-${this.$scope.material.peerReviews.length - 1}`)
                .focus()
        )
    }
    toggleTitleAndDescriptionLanguageInputs(lang) {
        this.$scope.activeTitleAndDescriptionLanguage = lang
    }
    isTouchedOrSubmitted(element) {
        return (element && element.$touched)
            || (this.$scope.addMaterialForm && this.$scope.addMaterialForm.$submitted)
    }
    showCompetencesWarning(element) {
        if (this.isTouchedOrSubmitted(element) && this.$scope.material.keyCompetences)
            return this.$scope.material.keyCompetences.length === 0
    }
    showThemesWarning(element) {
        if (this.isTouchedOrSubmitted(element) && this.$scope.material.crossCurricularThemes)
            return this.$scope.material.crossCurricularThemes.length === 0
    }
    isAuthorOrPublisherSet() {
        return !!(this.$scope.material.authors[0] && this.$scope.material.authors[0].name && this.$scope.material.authors[0].surname)
            || !!(this.$scope.material.publishers[0] && this.$scope.material.publishers[0].name)
    }
    isAdmin () {
        return this.authenticatedUserService.isAdmin();
    }
    getIssueDate(date) {
        return {
            day: date.getDate(),
            month: date.getMonth() + 1,
            year: date.getFullYear()
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
    translate(prefix, { name }) {
        return this.$translate.instant(prefix + name.toUpperCase())
    }
    searchList(listName, query) {
        const prefixMap = {
            keyCompetences: 'KEY_COMPETENCE_',
            crossCurricularThemes: 'CROSS_CURRICULAR_THEME_'
        }
        return !query
            ? this[listName]
            : this[listName].filter(item =>
                this.translate(prefixMap[listName], item)
                    .toLowerCase()
                    .startsWith(query.toLowerCase())
            )
    }
    removeFocus(elementId) {
        document.getElementById(elementId).blur()
    }
    autocompleteItemSelected(item, listName, elementId) {
        if (this.shouldRemoveNotRelevantFromList(listName))
            this.$scope.material[listName] = this.removeLastElement(listName)

        if (!item)
            return this.closeAutocomplete(elementId)

        // If 'NOT_RELEVANT' chip exists and new item is selected, replace it
        if (item.name !== 'NOT_RELEVANT' &&
            this.$scope.material[listName].some(item => item.name === 'NOT_RELEVANT')
        ) {
            this.$scope.material[listName] = this.$scope.material[listName].filter(item => item.name !== 'NOT_RELEVANT')
            this.$scope.material[listName].push(item)
        }
    }
    shouldRemoveNotRelevantFromList(listName) {
        return this.$scope.material[listName].length > 1
            && this.$scope.material[listName][this.$scope.material[listName].length - 1].name === 'NOT_RELEVANT';
    }
    removeLastElement(listName) {
        return this.$scope.material[listName].splice(0, this.$scope.material[listName].length - 1)
    }
    closeAutocomplete(elementId) {
        // Hide suggestions and blur input to avoid triggering new search
        const input = document.getElementById(elementId)
        angular.element(input).controller('mdAutocomplete').hidden = true
        input.blur()
    }
    isBasicOrSecondaryEducation () {
        return this.$scope.educationalContextId === 2 || this.$scope.educationalContextId === 3
    }
    isURLInvalid() {
        if (this.$scope.addMaterialForm && this.$scope.addMaterialForm.source && this.$scope.addMaterialForm.source.$viewValue) {
            this.$scope.addMaterialForm.source.$setTouched();
            return !!this.$scope.addMaterialForm.source.$error.url && (this.$scope.addMaterialForm.source.$viewValue.length > 0);
        }
    }
    sourceIsFocused() {
        this.$scope.addMaterialForm.source.$setValidity("filenameTooLong", true);
    }
    isEmpty (object) {
        return _.isEmpty(object)
    }
    initEmptyMaterial() {
        this.$scope.material = {}
        this.$scope.material.taxons = [{}]
        this.$scope.material.authors = [{}]
        this.$scope.material.peerReviews = [{}]
        this.$scope.material.keyCompetences = []
        this.$scope.material.crossCurricularThemes = []
        this.$scope.material.publishers = []
        this.$scope.material.resourceTypes = []
        this.$scope.issueDate = new Date()
        this.setTitlesAndDescriptions()
    }
    setTitlesAndDescriptions() {
        const languageCodeMap = {
            ET: 'est',
            EN: 'eng',
            RU: 'rus'
        }
        const findText = (prop, lang) =>
            ((this.$scope.material[prop] || []).find(c => c.language === languageCodeMap[lang]) || {}).text

        this.$scope.titlesAndDescriptions = this.titleAndSummaryLanguages.reduce(
            (titlesAndDescriptions, lang) =>
                Object.assign(titlesAndDescriptions, {
                    [lang]: {
                        title: findText('titles', lang) || '',
                        /**
                         * @todo this.stripHtml is a temporary solution until
                         * existing content is migrated in the DB
                         */
                        description: this.stripHtml(findText('descriptions', lang)) || '',
                    }
                }),
            {}
        )
    }
    getTitlesAndDescriptions() {
        const titles = [];
        const descriptions = [];
        const languageCodeMap = {
            ET: 'est',
            EN: 'eng',
            RU: 'rus'
        }

        Object.keys(this.$scope.titlesAndDescriptions).forEach(lang => {
            const item = this.$scope.titlesAndDescriptions[lang];

            if (item.title) {
                titles.push({
                    language: languageCodeMap[lang],
                    text: item.title
                });
            }
            if (item.description) {
                descriptions.push({
                    language: languageCodeMap[lang],
                    text: item.description
                });
            }
        });

        return { titles, descriptions };
    }
    isLangFilled(lang) {
        let isFilled = false;

        Object.keys(this.$scope.titlesAndDescriptions).forEach(key => {
            if (lang === key && this.$scope.titlesAndDescriptions[key].title !== "") {
                isFilled = true;
            }
        });

        return isFilled;
    }
    setPublisher() {
        if (this.authenticatedUserService.isPublisher()) {
            this.$scope.material.publishers = [{}]
            this.$scope.material.publishers[0].name = this.authenticatedUserService.getUser().publisher.name
            this.$scope.creatorIsPublisher = true
        }
    }
    processSource(source) {
        if (this.isYoutubeVideo(source))
            this.youtubeService
                .getYoutubeData(source)
                .then(({ data: { snippet, status }}) => {
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
                     */
                    if (snippet.thumbnails)
                        this.setThumbnail(snippet.thumbnails)

                    if (!this.$scope.material.publishers.find(p => p.name === snippet.channelTitle))
                        this.$scope.material.publishers.push({ name: snippet.channelTitle })

                    this.$scope.material.tags = snippet.tags
                    this.$scope.issueDate = new Date(snippet.publishedAt)
                    this.$scope.material.resourceTypes = [this.getResourceTypeByName('VIDEO')]
                    this.$scope.material.licenseType = this.getLicenseTypeByName(
                        status.license.toLowerCase() === 'creativecommon'
                            ? 'CCBY'
                            : 'Youtube'
                    )
                    this.$scope.titlesAndDescriptions = [{
                        title: snippet.title,
                        description: snippet.description
                    }]
                })
    }
    setThumbnail(thumbnails) {
        let thumbnailUrl

        if (thumbnails.maxres) thumbnailUrl = thumbnails.maxres.url
        else if (thumbnails.standard) thumbnailUrl = thumbnails.standard.url
        else if (thumbnails.high) thumbnailUrl = thumbnails.high.url
        else if (thumbnails.medium) thumbnailUrl = thumbnails.medium.url
        else if (thumbnails.default) thumbnailUrl = thumbnails.default.url

        this.pictureUploadService.uploadFromUrl(thumbnailUrl)
            .then(data =>
                this.$scope.material.picture = data
            )
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
    uploadReview(index, file) {
        if (file) {
            this.$scope.material.peerReviews[index].uploading = true
            this.$scope.uploadingReviewId = index
            this.fileUploadService.uploadReview(file)
                .then(({ data }) => {
                    this.$scope.material.peerReviews[this.$scope.uploadingReviewId].name = data.name;
                    this.$scope.material.peerReviews[this.$scope.uploadingReviewId].url = data.url;
                    this.$scope.material.peerReviews[this.$scope.uploadingReviewId].uploaded = true;

                    this.$scope.material.peerReviews[this.$scope.uploadingReviewId].uploading = false;
                }, ({ data }) => {
                    this.toastService.show(`Error: ${data}`);
                    this.$scope.material.peerReviews[this.$scope.uploadingReviewId].uploading = false;
                })
        }
    }
    preSetMaterial() {
        const { material } = this.$scope

        this.$scope.isUpdateMode = true

        if (material.issueDate)
            this.$scope.issueDate = this.issueDateToDate(material.issueDate)

        this.$scope.material.source = this.getMaterialSource(material)

        /**
         * so we could compare user input and prevent the "already exists" error when user manually
         * reverts the source link.
         */
        if (this.$scope.material.source)
            this.originalSource = this.$scope.material.source

        if (!this.$scope.material.authors[0])
            this.$scope.material.authors = [{}]

        if (!this.$scope.material.taxons[0])
            this.$scope.material.taxons = [{}]

        if (!this.$scope.material.peerReviews[0])
            this.$scope.material.peerReviews = [{}]

        const educationalContext = this.taxonService.getEducationalContext(this.$scope.material.taxons[0])

        if (educationalContext)
            this.$scope.educationalContextId = educationalContext.id

        if (!this.$scope.material.crossCurricularThemes)
            this.$scope.material.crossCurricularThemes = []

        if (!this.$scope.material.keyCompetences)
            this.$scope.material.keyCompetences = []

        this.setTitlesAndDescriptions()
    }
    prefillMetadataFromPortfolio() {
        const storedPortfolio = this.storageService.getPortfolio()

        if (storedPortfolio) {
            if (storedPortfolio.taxons) {
                this.$scope.material.taxons = storedPortfolio.taxons.slice()

                const educationalContext = this.taxonService.getEducationalContext(storedPortfolio.taxons[0])

                if (educationalContext)
                    this.$scope.educationalContextId = educationalContext.id
            }

            if (Array.isArray(storedPortfolio.tags))
                this.$scope.material.tags = storedPortfolio.tags.slice()

            if (Array.isArray(storedPortfolio.targetGroups))
                this.$scope.material.targetGroups = storedPortfolio.targetGroups.slice()
        }
    }
    setMaterialSourceLangugeges(data) {
        this.$scope.languages = data
        this.setDefaultMaterialMetadataLanguage()
        this.setMaterialSourceLanguage()
    }
    setMaterialSourceLanguage() {
        if (!this.$scope.material.language && this.preferredLanguage !== null && this.preferredLanguage !== undefined) {
            if (this.$scope.titlesAndDescriptions[0] && !this.$scope.titlesAndDescriptions[0].language)
                this.$scope.titlesAndDescriptions[0].language = this.preferredLanguage[0]

            this.$scope.material.language = this.preferredLanguage[0]
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
        if (list[list.length - 1].name !== 'NOT_RELEVANT')
            list.move(
                list.findIndex(i => i.name === 'NOT_RELEVANT'),
                list.length - 1
            )
    }
    setCrossCurricularThemes(crossCurricularThemes) {
        if (crossCurricularThemes) {
            this.crossCurricularThemes = crossCurricularThemes
            this.moveNotRelevantIfNecessary(this.crossCurricularThemes)
        }
    }
    setKeyCompetences(keyCompetences) {
        if (keyCompetences) {
            this.keyCompetences = keyCompetences
            this.moveNotRelevantIfNecessary(this.keyCompetences)
        }
    }
    setResourceTypes(data) {
        this.$scope.resourceTypes = data;
    }
    setDefaultMaterialMetadataLanguage() {
        var userLanguage = this.translationService.getLanguage();

        this.preferredLanguage = this.$scope.languages.filter(function (language) {
            return language == userLanguage;
        });
    }
    isMetadataStepValid() {
        return this.$scope.titlesAndDescriptions.filter(function (metadata) {
            return metadata.title && metadata.title.length !== 0;
        }).length !== 0;
    }
    fetchMaxPictureSize() {
        this.serverCallService
            .makeGet('rest/picture/maxSize')
            .then(({ data }) =>
                this.$scope.maxPictureSize = data
            )
    }
    fetchMaxFileSize() {
        this.serverCallService
            .makeGet('/rest/uploadedFile/maxSize')
            .then(({ data }) =>
                this.$scope.maxFileSize = data
            )
    }
    setChangeable() {
        Object.keys(changeable).forEach(key =>
            changeable[key].value = $scope.material[key]
        )
    }
    isChanged(material) {
        return Object.keys(changeable).reduce(
            (isChanged, key) =>
                isChanged || changeable[key].isChanged(material[key], changeable[key].value),
            false
        )
    }
    isSubmitDisabled() {
        return !this.$scope.addMaterialForm.$valid
            || (this.isBasicOrSecondaryEducation() && this.$scope.material.keyCompetences.length === 0)
            || (this.isBasicOrSecondaryEducation() && this.$scope.material.crossCurricularThemes.length === 0)
            || this.$scope.isSaving
            || this.$scope.uploadingFile;
    }
    save() {
        const save = () => {
            this.$scope.isSaving = true

            const { titles, descriptions } = this.getTitlesAndDescriptions()

            this.$scope.material.titles = titles
            this.$scope.material.descriptions = descriptions
            this.$scope.material.type = '.Material'

            if (this.$scope.material.source)
                this.$scope.material.uploadedFile = null

            if (this.$scope.material.publishers[0] && !this.$scope.material.publishers[0].name)
                this.$scope.material.publishers[0] = null

            this.$scope.material.peerReviews.forEach((peerReview, i) => {
                if (!peerReview || !peerReview.url)
                    this.$scope.material.peerReviews.splice(i, 1)
            })

            console.log('send this:', this.$scope.material)

            this.serverCallService
                [this.locals.isEditMode ? 'makePut' : 'makePost'](this.locals.isEditMode ? 'rest/material' : 'rest/material/create', this.$scope.material)
                .then(({ data: material }) => {
                    const done = () => {
                        this.$scope.isUpdateMode
                            ? this.$rootScope.learningObjectChanged = this.isChanged(material)
                            : this.$rootScope.learningObjectUnreviewed = true
                        this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
                    }
                    if (material) {
                        material.source = this.getMaterialSource(material)
                        this.$mdDialog.hide(material)
                        this.storageService.setMaterial(material)

                        if (!this.$scope.isChapterMaterial) {
                            const url = '/material?id=' + material.id

                            if (this.$location.url() === url)
                                return done()

                            const unsubscribe = this.$rootScope.$on('$locationChangeSuccess', () => {
                                unsubscribe()
                                this.$timeout(done)
                            })
                            this.$location.url(url)
                        }
                    }
                    this.$scope.isSaving = false
                }, () =>
                    this.$scope.isSaving = false
                )
        }
        Promise.all(
            ['fileUpload', 'pictureUpload'].reduce(
                (promises, u) => this[u] && this[u].then
                    ? promises.concat(this[u])
                    : promises,
                []
            )
        ).then(save)
    }
}
controller.$inject = [
  '$scope',
  '$rootScope',
  '$filter',
  '$mdDateLocale',
  '$mdDialog',
  '$location',
  '$timeout',
  '$translate',
  'authenticatedUserService',
  'fileUploadService',
  'locals',
  'metadataService',
  'pictureUploadService',
  'serverCallService',
  'storageService',
  'suggestService',
  'taxonService',
  'toastService',
  'translationService',
  'youtubeService',
]
angular.module('koolikottApp').controller('addMaterialDialogController', controller)
}
