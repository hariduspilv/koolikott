'use strict'

{
class controller extends Controller {
    $onInit() {
        this.isEditable = this.$rootScope.isEditPortfolioMode;
    }
    addMaterial() {
        const scope = this.$scope.$new(true)

        scope.uploadMode = true
        scope.material = {}
        scope.isChapterMaterial = true
        this.storageService.setMaterial(null)

        this.$mdDialog.show({
            templateUrl: 'addMaterialDialog.html',
            controller: 'addMaterialDialogController',
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
        this.$timeout(() =>
            focusInput(`chapter-${this.index}-${subChapters.length - 1}`)
        )
    }
    openMenu($mdOpenMenu, ev) {
        if (typeof $mdOpenMenu === 'function')
            $mdOpenMenu(ev)
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

        if (this.isEditable)
            document.getElementById('header-search-input').focus()
    }
}
controller.$inject = [
    '$scope',
    '$timeout',
    '$mdDialog',
    '$rootScope',
    'storageService',
    '$window'
]

angular.module('koolikottApp').component('dopChapterToolbar', {
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
