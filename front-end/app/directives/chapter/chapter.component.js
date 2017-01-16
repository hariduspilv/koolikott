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
        vm.isCollapsed = false;
        vm.subisCollapsed = [];

        angular.forEach(vm.chapter.subchapters, function (value, key) {
            vm.subisCollapsed[value.$$hashKey] = false;
        }, log);

        if (vm.chapter.openCloseChapter == true) {
            vm.isCollapsed = false;
        }
    }

    // Open/Close Chapter
    function ocChapter () {
        vm.isCollapsed = !vm.isCollapsed;
    };

    // Open/Close SubChapter
    function ocSubChapter (subChapter) {
        vm.subisCollapsed[subChapter.$$hashKey] = !vm.subisCollapsed[subChapter.$$hashKey];
    };

    function onDeleteSubChapter (subChapter) {
        var deleteSubChapter = function () {
            vm.chapter.subchapters.splice(vm.chapter.subchapters.indexOf(subChapter), 1);
        };

        dialogService.showDeleteConfirmationDialog(
            'PORTFOLIO_DELETE_SUB_CHAPTER_CONFIRM_TITLE',
            'PORTFOLIO_DELETE_SUB_CHAPTER_CONFIRM_MESSAGE',
            deleteSubChapter
        );

    };

    function deleteChapter () {
        vm.onDelete()(vm.chapter);
    };

    vm.ocChapter = ocChapter;
    vm.ocSubChapter = ocSubChapter;
    vm.onDeleteSubChapter = onDeleteSubChapter;
    vm.deleteChapter = deleteChapter;
};
