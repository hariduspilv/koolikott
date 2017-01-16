'use strict';

angular.module('koolikottApp')
.component('dopChapterToolbar', {
    bindings: {
        chapter: '=',
        isSub: '<',
        index: '<',
        subIndex: '<'
    },
    templateUrl: 'directives/chapter/chapterToolbar/chapterToolbar.html',
    controller: dopChapterToolbarController
});

dopChapterToolbarController.$inject = ['$scope', '$timeout', 'translationService', '$mdDialog', '$rootScope', 'storageService', 'serverCallService', '$filter', '$window'];

function dopChapterToolbarController ($scope, $timeout, translationService, $mdDialog, $rootScope, storageService, serverCallService, $filter, $window) {
    let vm = this;

    vm.$onInit = () => {
        vm.isEditable = $rootScope.isEditPortfolioMode;
    }

    vm.addMaterial = function() {
        let addMaterialScope = $scope.$new(true);

        addMaterialScope.uploadMode = true;
        addMaterialScope.material = {};
        addMaterialScope.isChapterMaterial = true;
        storageService.setMaterial(null);

        $mdDialog.show({
            templateUrl: 'addMaterialDialog.html',
            controller: 'addMaterialDialogController',
            scope: addMaterialScope
        }).then(closeDialog);
    };

    function closeDialog(material) {
        if (material) {
            if(!vm.chapter.contentRows) vm.chapter.contentRows = [];
            vm.chapter.contentRows.push({learningObjects: [material]});
        }
    }

    vm.addNewSubChapter = function() {
        let subChapters = vm.chapter.subchapters;

        subChapters.push({
            title: '',
            materials: []
        });

        let subChapterID = `chapter-${vm.index}-${subChapters.length - 1}`;

        $timeout(function () {
            focusInput(subChapterID);
        });
    };

    vm.openMenu = function($mdOpenMenu, ev) {
        $mdOpenMenu(ev);
    };

    vm.openDetailedSearch = function () {
        if (vm.subIndex) {
            $rootScope.savedIndexes = vm.index + '_' + vm.subIndex;
        } else {
            $rootScope.savedIndexes = vm.index;
        }

        if ($window.innerWidth >= BREAK_SM) {
            $rootScope.$broadcast("detailedSearch:open");
        } else {
            $rootScope.$broadcast("mobileSearch:open");
        }

        $rootScope.isPlaceholderVisible = true;

        if (vm.isEditable) {
            document.getElementById('header-search-input').focus();
        }
    };
}
