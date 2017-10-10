'use strict'

{
class controller extends Controller {
    $onInit() {
        this.isEditable = this.$rootScope.isEditPortfolioMode
        this.isCollapsed = this.chapter.openCloseChapter
        this.subisCollapsed = {}
    }
    onClickChapter() {
        this.isCollapsed = !this.isCollapsed
    }
    onClickSubChapter(index) {
        this.subisCollapsed[index] = !this.subisCollapsed[index]
    }
    onDeleteSubChapter(subChapter) {
        this.dialogService.showDeleteConfirmationDialog(
            'PORTFOLIO_DELETE_SUB_CHAPTER_CONFIRM_TITLE',
            'PORTFOLIO_DELETE_SUB_CHAPTER_CONFIRM_MESSAGE',
            () => this.chapter.subchapters.splice(this.chapter.subchapters.indexOf(subChapter), 1)
        )
    }
    deleteChapter() {
        if (typeof this.onDelete === 'function')
            this.onDelete({ chapter: this.chapter })
    }
}
controller.$inject = ['$rootScope', 'dialogService']

angular.module('koolikottApp').component('dopChapter', {
    bindings: {
        chapter: '=',
        index: '@',
        onDelete: '&?'
    },
    templateUrl: 'directives/chapter/chapter.html',
    controller
})
}
