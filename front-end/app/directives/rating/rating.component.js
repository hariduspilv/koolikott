'use strict'

{
class controller extends Controller {
    $onInit() {
        this.allowRequests = false
        this.likeFunction = () => {}
        this.dislikeFunction = () => {}
        this.entity = this.portfolio || this.material
        this.url = this.portfolio ? 'rest/portfolio/' : 'rest/material/'
        this.rating = {
            likes: (this.portfolio || this.material).likes,
            dislikes: (this.portfolio || this.material).dislikes
        }

        if (
            this.authenticatedUserService.isAuthenticated() &&
            !this.authenticatedUserService.isRestricted() &&
            this.entity &&
            this.entity.type
        )
        if (!this.entity.deleted){
            this.getUserLike()
        }
    }
    like() {
        if (this.allowRequests) {
            this.allowRequests = false
            this.stateMachine()
            this.likeFunction()
        }
    }
    dislike() {
        if (this.allowRequests) {
            this.allowRequests = false
            this.stateMachine()
            this.dislikeFunction()
        }
    }
    getUserLike() {
        this.serverCallService
            .makePost(this.url + 'getUserLike', this.entity)
            .then(({ data }) => {
                if (data) {
                    this.isLiked = data.liked
                    this.isDisliked = !data.liked
                }
                this.allowRequests = true
            })
    }
    sendLike() {
        this.serverCallService
            .makePost(this.url + 'like', this.entity)
            .then(
                () => this.allowRequests = true,
                () => this.allowRequests = true
            )

        this.isLiked = true
        this.rating.likes += 1
    }
    sendDislike() {
        this.serverCallService
            .makePost(this.url + 'dislike', this.entity)
            .then(
                () => this.allowRequests = true,
                () => this.allowRequests = true
            )
        this.isDisliked = true
        this.rating.dislikes += 1
    }
    removeLike() {
        this.serverCallService
            .makePost(this.url + 'removeUserLike', this.entity)
            .then(
                () => this.allowRequests = true,
                () => this.allowRequests = true
            )
        this.isLiked = false
        this.isDisliked = false
        this.rating.likes -= 1
    }
    removeDislike() {
        this.serverCallService
            .makePost(this.url + 'removeUserLike', this.entity)
            .then(
                () => this.allowRequests = true,
                () => this.allowRequests = true
            )
        this.isLiked = false
        this.isDisliked = false
        this.rating.dislikes -= 1
    }
    switchRating() {
        if (this.isLiked) {
            this.serverCallService
                .makePost(this.url + 'dislike', this.entity)
                .then(
                    () => this.allowRequests = true,
                    () => this.allowRequests = true
                )
            this.isLiked = false
            this.isDisliked = true
            this.rating.likes -= 1
            this.rating.dislikes += 1
        } else {
            this.serverCallService
                .makePost(this.url + 'like', this.entity)
                .then(
                    () => this.allowRequests = true,
                    () => this.allowRequests = true
                )
            this.isLiked = true
            this.isDisliked = false
            this.rating.likes += 1
            this.rating.dislikes -= 1
        }
    }
    stateMachine() {
        if (this.isLiked) {
            this.likeFunction = this.removeLike
            this.dislikeFunction = this.switchRating
        } else if (this.isDisliked) {
            this.likeFunction = this.switchRating
            this.dislikeFunction = this.removeDislike
        } else {
            this.likeFunction = this.sendLike
            this.dislikeFunction = this.sendDislike
        }
    }
}
controller.$inject = [
    'serverCallService',
    'authenticatedUserService'
]
component('dopRating', {
    bindings: {
        material: '=?',
        portfolio: '=?',
        likeMessage: '@',
        dislikeMessage: '@'
    },
    templateUrl: 'directives/rating/rating.html',
    controller
})
}
