'use strict'

angular.module('koolikottApp')
.component('dopRating', {
    bindings: {
        material: '=?',
        portfolio: '=?',
        likeMessage: '@',
        dislikeMessage: '@'
    },
    templateUrl: 'directives/rating/rating.html',
    controller: dopRatingController
});

dopRatingController.$inject = ['translationService', '$mdToast', '$translate', 'serverCallService', 'authenticatedUserService'];

function dopRatingController (translationService, $mdToast, $translate, serverCallService, authenticatedUserService) {
    let vm = this;

    vm.$onInit = () => {
        vm.allowRequests = false;
        vm.likeFunction = function() {};
        vm.dislikeFunction = function() {};
        vm.rating = {};

        if (vm.portfolio) {
            vm.rating.likes = vm.portfolio.likes;
            vm.rating.dislikes = vm.portfolio.dislikes;

            vm.entity = vm.portfolio;
            vm.url = "rest/portfolio/";
        }
        if (vm.material) {
            vm.rating.likes = vm.material.likes;
            vm.rating.dislikes = vm.material.dislikes;

            vm.entity = vm.material;
            vm.url = "rest/material/";
        }

        if (vm.isAuthenticated() && vm.entity && vm.entity.type && !vm.isRestricted()) {
            getUserLike();
        }
    }

    vm.isAuthenticated = () => authenticatedUserService.isAuthenticated();
    vm.isRestricted = () => authenticatedUserService.isRestricted();

    vm.like = function() {
        if (vm.allowRequests) {
            vm.allowRequests = false;
            stateMachine();
            vm.likeFunction();
        }
    };

    vm.dislike = function() {
        if (vm.allowRequests) {
            vm.allowRequests = false;
            stateMachine();
            vm.dislikeFunction();
        }
    };

    function getUserLike() {
        serverCallService.makePost(vm.url + "getUserLike", vm.entity, getUserLikeSuccess, function() {});
    }

    function getUserLikeSuccess(userlike) {
        if (userlike) {
            if (userlike.liked) {
                vm.isLiked = true;
            } else {
                vm.isDisliked = true;
            }
        }
        requestSuccessful();
    }

    function like() {
        serverCallService.makePost(vm.url + "like", vm.entity, requestSuccessful, requestFailed);
        vm.isLiked = true;
        vm.rating.likes += 1;
    }

    function dislike() {
        serverCallService.makePost(vm.url + "dislike", vm.entity, requestSuccessful, requestFailed);
        vm.isDisliked = true;
        vm.rating.dislikes += 1;
    }

    function removeLike() {
        serverCallService.makePost(vm.url + "removeUserLike", vm.entity, requestSuccessful, requestFailed);
        vm.isLiked = false;
        vm.isDisliked = false;
        vm.rating.likes -= 1;
    }

    function removeDislike() {
        serverCallService.makePost(vm.url + "removeUserLike", vm.entity, requestSuccessful, requestFailed);
        vm.isLiked = false;
        vm.isDisliked = false;
        vm.rating.dislikes -= 1;
    }

    function switchRating() {
        if (vm.isLiked) {
            serverCallService.makePost(vm.url + "dislike", vm.entity, requestSuccessful, requestFailed);
            vm.isLiked = false;
            vm.isDisliked = true;
            vm.rating.likes -= 1;
            vm.rating.dislikes += 1;
        } else {
            serverCallService.makePost(vm.url + "like", vm.entity, requestSuccessful, requestFailed);
            vm.isLiked = true;
            vm.isDisliked = false;
            vm.rating.likes += 1;
            vm.rating.dislikes -= 1;
        }
    }


    function stateMachine() {
        if (vm.isLiked) {
            vm.likeFunction = removeLike;
            vm.dislikeFunction = switchRating;
        } else if (vm.isDisliked) {
            vm.likeFunction = switchRating;
            vm.dislikeFunction = removeDislike;
        } else {
            vm.likeFunction = like;
            vm.dislikeFunction = dislike;
        }
    }

    function requestSuccessful() {
        vm.allowRequests = true;
    }

    function requestFailed() {
        vm.allowRequests = true;
    }
}
