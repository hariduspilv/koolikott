'use strict';

angular.module('koolikottApp')
.component('dopChapter', {
    bindings: {
        chapter: '=',
        index: '@',
        onDelete: '&?'
    },
    templateUrl: 'directives/chapter/chapter.html',
    controller: dopChapterController
});

dopChapterController.$inject = ['$rootScope', 'dialogService'];

function dopChapterController ($rootScope, dialogService) {
    let vm = this;

    vm.$onInit = () => {
        vm.isEditable = $rootScope.isEditPortfolioMode;
        vm.isCollapsed = vm.chapter.openCloseChapter;
        vm.subisCollapsed = {};
    };

    // Open/Close Chapter
    vm.ocChapter = () => vm.isCollapsed = !vm.isCollapsed;

    // Open/Close SubChapter
    vm.ocSubChapter = (index) => {
        vm.subisCollapsed[index] = !vm.subisCollapsed[index];
    };

    vm.onDeleteSubChapter = (subChapter) => {
        let deleteSubChapter = () => vm.chapter.subchapters.splice(vm.chapter.subchapters.indexOf(subChapter), 1);

        dialogService.showDeleteConfirmationDialog(
            'PORTFOLIO_DELETE_SUB_CHAPTER_CONFIRM_TITLE',
            'PORTFOLIO_DELETE_SUB_CHAPTER_CONFIRM_MESSAGE',
            deleteSubChapter
        );
    };

    vm.deleteChapter = () => vm.onDelete()(vm.chapter);
}
