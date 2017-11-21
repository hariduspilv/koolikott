'use strict'

{
class controller extends Controller {
    $onChanges({ isEditMode } = {}) {
        if (isEditMode && isEditMode.currentValue !== isEditMode.previousValue)
            this.$scope.isEditMode = isEditMode
    }
    $onInit() {
        this.$scope.authUser = false;
        this.isAdminOrModerator();
        this.$scope.$watch(
            () => this.storageService.getPortfolio(),
            (portfolio) => this.$scope.portfolio = portfolio
        )

        if (this.$location.hash()) {
            const unsubscribe = this.$scope.$watch(
                () => document.getElementById(this.$location.hash()),
                (newValue) => {
                    if (newValue != null)
                        this.$timeout(() => {
                            this.goToElement(this.$location.hash())
                            unsubscribe()
                        })
                })
        }

        this.$scope.onSortChapters = this.updateChapterEditorsFromState.bind(this)
    }
    gotoChapter(evt, chapterId, subchapterId) {
        evt.preventDefault()
        this.goToElement(
            subchapterId != null
                ? 'chapter-' + chapterId + '-' + subchapterId
                : 'chapter-' + chapterId
        )
    }
    goToElement(elementID) {
        const $chapter = angular.element(document.getElementById(elementID))
        this.$document.scrollToElement($chapter, 60, 200)
    }
    closeSidenav(id) {
        if (window.innerWidth < BREAK_LG)
            this.$mdSidenav(id).close()
    }
    isAdminOrModerator() {
        if (this.authenticatedUserService.isAdmin() || this.authenticatedUserService.isModerator()) {
            this.$scope.authUser = true;
        } else {
            this.$scope.authUser = false;
        }
    }
}
controller.$inject = [
    '$scope',
    '$mdSidenav',
    '$document',
    '$location',
    '$timeout',
    'storageService',
    'authenticatedUserService'
]
component('dopTableOfContents', {
    bindings: {
        isEditMode: '<'
    },
    templateUrl: 'directives/tableOfContents/tableOfContents.html',
    controller
})
}
