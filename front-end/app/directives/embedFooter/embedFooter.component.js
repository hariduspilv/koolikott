'use strict'

{
class controller extends Controller {
    $onInit() {
        this.currentLanguage = this.translationService.getLanguage()
    }
    $onChanges({ data }) {
        if (data.currentValue !== data.previousValue) {
            this.isMaterial(data.currentValue)
                ? this.setMaterialFooterData(data.currentValue)
                : this.setMediaFooterData(data.currentValue)
        }
    }
    setMaterialFooterData({ id, publishers, authors, titles, source, uploadedFile, language, resourceTypes }) {
        this.$scope.icon = this.iconService.getMaterialIcon(resourceTypes)
        this.$scope.link = '/material?id=' + id
        this.$scope.title = this.getUserDefinedLanguageString(titles, this.currentLanguage, language)
        this.$scope.publishersAndAuthors = publishers.map(p => p.name).concat(authors.map(a => a.name+' '+a.surname)).join(', ')
        this.setMaterialSourceLink()
    }
    setMaterialSourceLink() {
        const { embeddable, source, uploadedFile } = this.data
        const set = (url) => this.$scope.sourceLink = {
            url: url || source || uploadedFile.url,
            text: source || uploadedFile.url
        }

        !embeddable || !this.authenticatedUserService.isAuthenticated()
            ? set()
            : this.serverCallService
                .makeGet('rest/user/getSignedUserData')
                .then(
                    ({ data }) =>
                        set(`${source}${source.includes('?') ? '&' : '?'}dop_token=${encodeURIComponent(data)}`),
                    set
                )
    }
    setMediaFooterData(media) {
        this.$scope.icon = this.iconService.getMediaIcon(this.getEmbeddedMediaType(media))
        this.$scope.link = media.url
        this.$scope.title = media.title
        this.$scope.publishersAndAuthors = media.author
        this.$scope.sourceLink = {
            url: media.url,
            text: media.source
        }
    }
}
controller.$inject = [
    '$scope',
    '$window',
    'authenticatedUserService',
    'iconService',
    'serverCallService',
    'translationService',
]
component('dopEmbedFooter', {
    bindings: {
        data: '<',
    },
    templateUrl: 'directives/embedFooter/embedFooter.html',
    controller
})
}
