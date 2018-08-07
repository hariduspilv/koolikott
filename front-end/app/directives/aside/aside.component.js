'use strict'

{
/**
 * Can't define as this.SIDE_ITEMS_AMOUNT in $onInit because $onCanges is called
 * before $onInit.. go figure.
 * @see https://github.com/angular/angular/issues/2404
 */
const SIDE_ITEMS_AMOUNT = 5

class controller extends Controller {
    $onInit() {
        this.$scope.$on('recommendations:updated', this.loadRecommendations.bind(this))
        this.$scope.showMoreRecommendations = this.showMoreRecommendations.bind(this)

        /**
         * Looks like this.$scope.mostLikedList is never used.
         * Orphaned code? Commenting it out for the time being.
         */
        /*this.$scope.$watch(
            () => this.$location.search(),
            (newValue, oldValue) => {
                if (newValue !== oldValue && newValue.q && this.isSearchResultPage())
                    this.loadMostLiked()
            },
            true
        )*/
    }
    $onChanges(changes) {
        if (changes.learningObject) {
            if (changes.learningObject.currentValue && changes.learningObject.currentValue.id) {
                this.loadSimilar()
                this.loadRecommendations()
            }
        }
    }
    getObjectIds(objects) {
        return Array.isArray(objects)
            ? objects.map(o => o.id)
            : objects
    }
    loadSimilar() {
        this.serverCallService
            .makeGet('rest/search', {
                q: this.learningObject.tags
                    ? this.learningObject.tags.map(tag => `tag:\"${tag}\"`).join(' ')
                    : '',
                isORSearch: 'true',
                excluded: this.learningObject.id,
                taxon: this.getObjectIds(this.learningObject.taxons),
                targetGroup: this.learningObject.targetGroups,
                crossCurricularTheme: this.getObjectIds(this.learningObject.crossCurricularThemes),
                keyCompetence: this.getObjectIds(this.learningObject.keyCompetences),
                start: 0,
                limit: SIDE_ITEMS_AMOUNT
            })
            .then(({ data }) => {
                if (data)
                    this.$scope.similar = data.items
            })
    }
    loadRecommendations() {
        this.serverCallService
            .makeGet('rest/search', {
                q: 'recommended:true',
                excluded: this.learningObject.id,
                start: 0,
                limit: SIDE_ITEMS_AMOUNT
            })
            .then(({ data }) => {
                if (data)
                    this.$scope.recommendations = data.items
            })
    }
    showMoreRecommendations() {
        this.searchService.setQuery('recommended:true')
        this.searchService.clearFieldsNotInSimpleSearch()
        this.searchService.setIsGrouped(false)

        this.$location.url(this.searchService.getURL())
    }
}
controller.$inject = [
    '$scope',
    'serverCallService',
    '$location',
    'searchService',
    'authenticatedUserService'
]
component('dopAside', {
    bindings: {
        learningObject: '<'
    },
    templateUrl: 'directives/aside/aside.html',
    controller
})
}
