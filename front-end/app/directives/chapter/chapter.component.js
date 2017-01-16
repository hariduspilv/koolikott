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

dopChapterController.$inject = ['$rootScope', 'translationService', 'dialogService'];

function dopChapterController ($rootScope, translationService, dialogService) {
    let vm = this;

    vm.$onInit = () => {
        vm.isEditable = $rootScope.isEditPortfolioMode;
        vm.isCollapsed = vm.chapter.openCloseChapter ? true : false;
        vm.subisCollapsed = [];

        angular.forEach(vm.chapter.subchapters, (value, key) => {
            vm.subisCollapsed[value.$$hashKey] = false;
        }, log);
    }

    // Open/Close Chapter
    vm.ocChapter = () => vm.isCollapsed = !vm.isCollapsed;

    // Open/Close SubChapter
    vm.ocSubChapter = (subChapter) => vm.subisCollapsed[subChapter.$$hashKey] = !vm.subisCollapsed[subChapter.$$hashKey];

    vm.onDeleteSubChapter = (subChapter) => {
        var deleteSubChapter = () => vm.chapter.subchapters.splice(vm.chapter.subchapters.indexOf(subChapter), 1);

        dialogService.showDeleteConfirmationDialog(
            'PORTFOLIO_DELETE_SUB_CHAPTER_CONFIRM_TITLE',
            'PORTFOLIO_DELETE_SUB_CHAPTER_CONFIRM_MESSAGE',
            deleteSubChapter
        );
    };

    vm.deleteChapter = () => vm.onDelete()(vm.chapter);
};
