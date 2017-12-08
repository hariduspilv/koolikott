'use strict'

{
class controller extends Controller {
    addMaterial() {
        const scope = this.$scope.$new(true)

        scope.uploadMode = true
        scope.material = {}
        scope.isChapterMaterial = true
        this.storageService.setMaterial(null)

        this.$mdDialog.show({
            templateUrl: 'addMaterialDialog.html',
            controller: 'addMaterialDialogController',
            controllerAs: '$ctrl',
            scope
        }).then(material => {
            if (material) {
                if (!this.chapter.contentRows)
                    this.chapter.contentRows = []

                this.chapter.contentRows.push({
                    learningObjects: [material]
                })
            }
        })
    }
    addNewSubChapter() {
        this.chapter.subchapters.push({
            title: '',
            materials: []
        })
        this.$timeout(() => {
            const el = document.getElementById(`chapter-${this.index}-${this.chapter.subchapters.length - 1}`)
            if (el)
                el.querySelector('input').focus()
        })
    }
    openDetailedSearch() {
        this.$rootScope.savedIndexes = this.subIndex != null
            ? this.index + '_' + this.subIndex
            : this.index

        this.$rootScope.$broadcast(
            this.$window.innerWidth >= BREAK_SM
                ? 'detailedSearch:open'
                : 'mobileSearch:open'
        )

        if (this.$rootScope.isEditPortfolioMode)
            document.getElementById('header-search-input').focus()
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$timeout',
    '$mdDialog',
    'storageService',
    '$window'
]
component('dopChapterToolbar', {
    bindings: {
        chapter: '=',
        isSub: '<',
        index: '<',
        subIndex: '<'
    },
    templateUrl: 'directives/chapter/chapterToolbar/chapterToolbar.html',
    controller
})
}
