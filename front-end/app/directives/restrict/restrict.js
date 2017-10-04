'use strict'

{
class controller extends Controller {
    $onInit() {
        if (this.$scope.learningObject && this.$scope.learningObject.creator) {
            if (this.authenticatedUserService.isAdmin() ||
                this.authenticatedUserService.isModerator()
            )
                console.info('%c@todo: Creator of this material is admin or moderator. Should “Restrict material creator” button be hidden or disabled?', 'color: blue')

            this.$scope.toggleRestrict = this.toggleRestrict.bind(this)
            this.setState(this.$scope.learningObject.creator)
        } else
            this.$scope.noCreator = true
    }
    setState(user) {
        this.creator = user
        this.$scope.labelTranslationKey = user.role === 'RESTRICTED'
            ? 'BUTTON_REMOVE_CREATORS_RESTRICTION'
            : 'BUTTON_RESTRICT_CREATOR'
    }
    toggleRestrict() {
        const url = this.creator.role === 'RESTRICTED'
            ? 'rest/user/removeRestriction'
            : 'rest/user/restrictUser'

        this.serverCallService.makePost(url, this.creator).then(({ data: user }) => {
            if (user) {
                this.setState(user)
                toastService.show(
                    user.role === 'RESTRICTED'
                        ? 'USER_RESTRICTED'
                        : 'USER_RESTRICTION_REMOVED'
                )
            }
        })
    }
}
controller.$inject = ['$scope', 'serverCallService', 'toastService', 'authenticatedUserService']

/**
 * Declaring this as a directive since we need to use it as an attribute on
 * <md-menu-item> (component usage is restricted to element tagname only).
 */
angular.module('koolikottApp').directive('dopRestrict', () => ({
    scope: {
        learningObject: '<'
    },
    templateUrl: 'directives/restrict/restrict.html',
    controller
}))
}
