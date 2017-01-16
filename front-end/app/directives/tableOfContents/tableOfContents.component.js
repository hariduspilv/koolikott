'use strict'

angular.module('koolikottApp')
.component('dopTableOfContents', {
    bindings: {
        portfolio: '='
    },
    templateUrl: 'directives/tableOfContents/tableOfContents.html',
    controller: dopTableOfContentsController
});

dopTableOfContentsController.$inject = ['$scope', '$rootScope', '$mdSidenav', '$filter', '$document', 'translationService', '$mdToast', '$location', '$timeout'];

function dopTableOfContentsController ($scope, $rootScope, $mdSidenav, $filter, $document, translationService, $mdToast, $location, $timeout) {
    let vm = this;

    vm.$onInit = () => {
        // Scroll to hash
        if ($location.hash()) {
            var listener = $scope.$watch(function() {
                return document.getElementById($location.hash())
            }, function(newValue) {
                if (newValue != null) {
                    $timeout(function() {
                        goToElement($location.hash());
                        listener();
                    });
                }
            });
        }
    }

    vm.gotoChapter = (e, chapterId, subchapterId) => {
        e.preventDefault();

        var combinedId = 'chapter-' + chapterId;
        if (subchapterId != null) {
            combinedId += '-' + subchapterId;
        }

        goToElement(combinedId);
    };

    function goToElement(elementID) {
        var $chapter = angular.element(document.getElementById(elementID));
        $document.scrollToElement($chapter, 60, 200);
    }

    vm.closeSidenav = (id) => {
        if (window.innerWidth < BREAK_LG) {
            $mdSidenav(id).close();
        }
    };
}
