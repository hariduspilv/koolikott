'use strict';

{
class controller extends Controller {
    constructor(...args) {
        super(...args)

        if (!this.$scope.media)
            this.$scope.media = {
                url: '',
                title: '',
                source: '',
                author: '',
                licenseType: null
            }

        this.$scope.isSaving = false
        this.$scope.isUserAuthor = false
        this.$scope.licenseTypes = []

        const { name, surname } = this.authenticatedUserService.getUser()
        this.userFullName = `${name} ${surname}`

        this.metadataService.loadLicenseTypes(data => {
            this.$scope.licenseTypes = data
            this.$scope.doNotKnow = { id: 'doNotKnow' }
            this.$scope.allRightsReserved = data.find(t => t.name === 'allRightsReserved')
        })

        this.$scope.$watch('media.url', this.processURL.bind(this))
        this.$timeout(() => {
            this.$scope.addMediaForm.url.$validators.accepted = (url) =>
                // Let's not display ‘not accepted’ error before the URL is in valid shape
                this.$scope.addMediaForm.url.$error.url || this.isAcceptedEmbeddableMediaLink(url)
        })
        this.$translate('MEDIA_URL_IS_NOT_ACCEPTED_BY_EXTENSION').then(message => {
            const set = (and) => this.$scope.urlIsNotAcceptedMessage = message.replace(
                '%s',
                `jpg (jpeg), png, gif, mp3, ogg, wav, mp4, ogv, webm, YouTube ${and} SoundCloud`
            )
            switch (this.translationService.getLanguage()) {
                case 'est': return set('ja')
                case 'eng': return set('and')
                case 'rus': return set('и')
            }
        }, () =>
            this.$scope.urlIsNotAcceptedMessage = 'MEDIA_URL_IS_NOT_ACCEPTED_BY_EXTENSION'
        )

        this.$scope.$watch('media.author', (currentValue, previousValue) => {
            this.$scope.isUserAuthor = currentValue === this.userFullName
        })

        /**
         * Set license type to “All rights reserved” if user chooses “Do not know” option.
         */
        this.$scope.$watch('media.licenseType', (selectedValue) => {
            if (selectedValue && selectedValue.id === 'doNotKnow')
                this.$scope.media.licenseType = this.$scope.allRightsReserved
        })

        // fix for https://github.com/angular/material/issues/6905
        this.$timeout(() =>
            angular.element(document.querySelector('html')).css('overflow-y', '')
        )
    }
    setAuthorToUser() {
        this.$scope.media.author = this.$scope.media.author !== this.userFullName
            ? this.userFullName
            : ''
    }
    processURL(currentValue, previousValue) {
        if (currentValue && currentValue !== previousValue) {
            if (this.isYoutubeLink(currentValue))
                this.youtubeService
                    .getYoutubeData(currentValue)
                    .then(({ snippet, status }) => {
                        const licenseTypeName = status.license.toLowerCase() === 'creativecommon' ? 'CCBY' : 'Youtube'
                        this.$scope.media.licenseType = this.$scope.licenseTypes.find(l => l.name === licenseTypeName)
                        this.$scope.media.title = snippet.title
                        this.$scope.media.source = snippet.channelTitle
                    })
            /**
             * Soundcloud has temporarily suspended registering new apps at http://soundcloud.com/you/apps/new
             * so fetching some details to auto-fill some fields cannot be completed at this time.
             */
            // else if (this.isSoundcloudLink(currentValue)) {}
        }
    }
    isSubmitDisabled() {
        return !this.$scope.addMediaForm.$valid || this.$scope.isSaving
    }
    save() {
        this.$scope.isSaving = true
        this.serverCallService
            .makePost(this.locals.isEditMode ? 'rest/media/update' : 'rest/media/create', this.$scope.media)
            .then(({ status, data: media }) => {
                200 <= status && status < 300
                    ? this.$mdDialog.hide(media)
                    : this.$mdDialog.cancel()

                this.$scope.isSaving = false
            }, () =>
                this.$scope.isSaving = false
            )
    }
    cancel() {
        /**
         * There's a known issue that delays the actual closing of the dialog in Google Chrome on Mac OS
         * and even after it closes in 3-4 seconds it leaves the whole app unresponsive for another 5-7 seconds.
         * @see https://github.com/angular/material/issues/8598
         */
        this.$mdDialog.cancel()
    }
    setLicenseTypeSelectedText() {
        this.$translate('LICENSETYPE_LONG_NAME_' + this.$scope.media.licenseType.name.toUpperCase()).then(translation =>
            this.$scope.licenseTypeSelectedText = translation
        )
    }
}
controller.$inject = [
  '$scope',
  '$mdDialog',
  '$timeout',
  '$translate',
  'authenticatedUserService',
  'locals',
  'metadataService',
  'serverCallService',
  'translationService',
  'youtubeService',
]
angular.module('koolikottApp').controller('addMediaDialogController', controller)
}
