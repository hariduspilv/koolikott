'use strict'

angular.module('koolikottApp')
    .component('dopFavorite', {
        bindings: {
            learningObject: '<',
            hover: '<'
        },
        templateUrl: 'directives/favorite/favorite.html',
        controller: dopFavoriteController
    });

dopFavoriteController.$inject = ['$rootScope', 'serverCallService', 'authenticatedUserService', 'toastService', '$timeout', 'materialService', 'portfolioService'];

function dopFavoriteController($rootScope, serverCallService, authenticatedUserService, toastService, $timeout, materialService, portfolioService) {
    let vm = this;

    vm.$onInit = () => {
        vm.isEditPortfolioMode = $rootScope.isEditPortfolioMode;
        vm.isViewMaterialOrPortfolioPage = $rootScope.isViewMaterialOrPortfolioPage;

        getFavoriteData();
    };

    vm.$onChanges = (changes) => {
        // When asynchronous learningObject request finishes after component init
        if (changes.learningObject && changes.learningObject.currentValue && !changes.learningObject.previousValue) {
            getFavoriteData();
        }
    };

    function getFavoriteData() {
        if (vm.learningObject && isLoggedIn()) {
            if (vm.learningObject.favorite) {
                vm.hasFavorited = true;
            } else if (vm.learningObject.favorite == null) {
                serverCallService.makeGet("rest/learningObject/favorite", {'id': vm.learningObject.id}, getFavoriteSuccess, getFavoriteFail);
            }
        }
    }

    function getFavoriteSuccess(data) {
        if (data && data.id) {
            vm.hasFavorited = true;
        }
    }

    function getFavoriteFail() {
        console.log("Getting info if the user has favorited failed");
    }

    vm.favorite = ($event) => {
        $event.preventDefault();
        $event.stopPropagation();

        if (isLoggedIn()) {
            if (isPortfolio(vm.learningObject.type)) {
                portfolioService.getPortfolioById(vm.learningObject.id)
                    .then(data => {
                        serverCallService.makePost("rest/learningObject/favorite", data, addFavoriteSuccess, addFavoriteFail);
                    });
            } else if (isMaterial(vm.learningObject.type)) {
                materialService.getMaterialById(vm.learningObject.id)
                    .then(data => {
                        serverCallService.makePost("rest/learningObject/favorite", data, addFavoriteSuccess, addFavoriteFail);
                    });
            }


            vm.hasFavorited = true;
        }
    };

    function addFavoriteSuccess(data) {
        if (data && data.id) {
            toastService.show("ADDED_TO_FAVORITES");
            vm.hasFavorited = true;
        }
    }

    function addFavoriteFail() {
        console.log("Adding as favorite failed");
        vm.hasFavorited = false;
    }

    vm.removeFavorite = ($event) => {
        $event.preventDefault();
        $event.stopPropagation();

        if (isLoggedIn() && vm.hasFavorited) {
            serverCallService.makeDelete("rest/learningObject/favorite", {'id': vm.learningObject.id});
            vm.hasFavorited = false;
            toastService.show("REMOVED_FROM_FAVORITES");
        }
    };

    function isLoggedIn() {
        return authenticatedUserService.isAuthenticated();
    }
}
