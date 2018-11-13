'use strict'

{
class controller extends Controller {
    $onInit() {
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
