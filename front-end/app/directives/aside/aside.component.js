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
        this.$scope.similar = [{}, {}, {}, {}, {}]
        this.$scope.recommendations = [{}, {}, {}, {}, {}]

        this.$scope.$on('recommendations:updated', this.loadRecommendations.bind(this))
        this.loadRecommendations()

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
            if (changes.learningObject.currentValue && changes.learningObject.currentValue.id)
                this.loadSimilar()
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
        const cache = !this.authenticatedUserService.isAdmin() && !this.authenticatedUserService.isModerator()

        this.serverCallService
            .makeGet('rest/search', {
                q: 'recommended:true',
                start: 0,
                sort: 'recommendation_timestamp',
                sortDirection: 'desc',
                limit: SIDE_ITEMS_AMOUNT
            }, null, null, null, null, cache)
            .then(({ data }) => {
                if (data)
                    this.$scope.recommendations = data.items
            })
    }
    showMoreRecommendations() {
        this.searchService.setSearch('recommended:true')
        this.searchService.clearFieldsNotInSimpleSearch()
        this.searchService.setSort('recommendation_timestamp')
        this.searchService.setSortDirection('desc')

        this.$location.url('/' + this.searchService.getSearchURLbase() + this.searchService.getQueryURL())
    }
    /*loadMostLiked() {
        this.isSearchResultPage()
            ? this.loadMostLikedOrderedByLikes()
            : this.serverCallService
                .makeGet('rest/search/mostLiked', {
                    maxResults: SIDE_ITEMS_AMOUNT
                })
                .then(({ data }) => {
                    if (data)
                        this.$scope.mostLikedList = data
                })
    }
    loadMostLikedOrderedByLikes() {
        // What is the point of all these sort ops??
        const originalSort = this.searchService.getSort()
        const originalSortDirection = this.searchService.getSortDirection()
        this.searchService.setSort('like_score')
        this.searchService.setSortDirection('desc')
        this.searchService.setSort(originalSort)
        this.searchService.setSortDirection(originalSortDirection)

        this.serverCallService
            .makeGet('rest/search?'+this.searchService.getQueryURL(true), {
                limit: SIDE_ITEMS_AMOUNT
            })
            .then(({ data }) => {
                if (data)
                    this.$scope.mostLikedList = data.items
            })
    }
    isSearchResultPage() {
        return this.$location.url().startsWith('/' + this.searchService.getSearchURLbase())
    }*/
}
controller.$inject = [
    '$scope',
    'serverCallService',
    '$location',
    'searchService',
    'authenticatedUserService'
]

angular.module('koolikottApp').component('dopAside', {
    bindings: {
        learningObject: '<'
    },
    templateUrl: 'directives/aside/aside.html',
    controller
})
}
