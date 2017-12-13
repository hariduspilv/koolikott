'use strict'

{
class controller extends Controller {
    $onInit() {
        this.currentLanguage = this.translationService.getLanguage()
    }
    $onChanges({ learningObject }) {
        if (learningObject.currentValue !== learningObject.previousValue) {
            const { id, publishers, authors, titles, source, uploadedFile, language, resourceTypes } = learningObject.currentValue

            this.$scope.icon = this.iconService.getMaterialIcon(resourceTypes)
            this.$scope.link = '/material?id=' + id
            this.$scope.title = this.getUserDefinedLanguageString(titles, this.currentLanguage, language)
            this.$scope.publishersAndAuthors = publishers.map(p => p.name).concat(authors.map(a => a.name+' '+a.surname)).join(', ')
            this.setSourceLink()
        }
    }
    setSourceLink() {
        const { embeddable, source, uploadedFile } = this.learningObject
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
        learningObject: '<',
    },
    templateUrl: 'directives/embedFooter/embedFooter.html',
    controller
})
}
