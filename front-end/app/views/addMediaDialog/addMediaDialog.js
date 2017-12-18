'use strict';

{
class controller extends Controller {
    constructor(...args) {
        super(...args)

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

        this.metadataService.loadLicenseTypes(data => {
            this.$scope.licenseTypes = data
            this.$scope.allRightsReserved = data.find(t => t.name.toUpperCase() === 'ALLRIGHTSRESERVED')
        })

        this.$scope.$watch('media.url', this.processURL.bind(this))

        // fix for https://github.com/angular/material/issues/6905
        this.$timeout(() =>
            angular.element(document.querySelector('html')).css('overflow-y', '')
        )
    }
    setAuthorToUser() {
        const { name, surname } = this.authenticatedUserService.getUser()

        if (this.$scope.media.author !== `${name} ${surname}`) {
            this.$scope.media.author = `${name} ${surname}`
            this.$scope.isUserAuthor = true
        } else {
            this.$scope.media.author = ''
            this.$scope.isUserAuthor = false
        }
    }
    isTouchedOrSubmitted(element) {
        return (element && element.$touched)
            || (this.$scope.addMediaForm && this.$scope.addMediaForm.$submitted)
    }
    isURLInvalid() {
        if (this.$scope.addMediaForm && this.$scope.addMediaForm.source && this.$scope.addMediaForm.source.$viewValue) {
            this.$scope.addMediaForm.source.$setTouched();
            return !!this.$scope.addMediaForm.source.$error.url && (this.$scope.addMediaForm.source.$viewValue.length > 0);
        }
    }
    processURL(currentValue, previousValue) {
        if (currentValue && currentValue !== previousValue && this.isYoutubeVideo(currentValue))
            this.youtubeService
                .getYoutubeData(currentValue)
                .then(({ snippet, status }) => {
                    const licenseTypeName = status.license.toLowerCase() === 'creativecommon' ? 'CCBY' : 'Youtube'
                    this.$scope.media.licenseType = this.$scope.licenseTypes.find(l => l.name === licenseTypeName)
                    this.$scope.media.title = snippet.title
                    this.$scope.media.source = snippet.channelTitle
                })
    }
    isSubmitDisabled() {
        return !this.$scope.addMediaForm.$valid || this.$scope.isSaving
    }
    save() {
        this.$scope.isSaving = true
        console.log('make post')
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
        console.log('cancel!')
        console.time('cancel media dialog')
        this.$mdDialog.cancel(true)
    }
}
controller.$inject = [
  '$scope',
  '$mdDialog',
  '$timeout',
  'authenticatedUserService',
  'locals',
  'metadataService',
  'serverCallService',
  'youtubeService',
]
angular.module('koolikottApp').controller('addMediaDialogController', controller)
}
