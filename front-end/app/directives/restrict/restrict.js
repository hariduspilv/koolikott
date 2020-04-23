'use strict'

{
class controller extends Controller {
    $onInit() {
        if (this.$scope.learningObject && this.$scope.learningObject.creator) {
            if (this.authenticatedUserService.isAdmin() &&
                !this.authenticatedUserService.isOwner(this.$scope.learningObject)
            )
                this.$scope.toggleRestrict = this.toggleRestrict.bind(this)

            this.serverCallService.makeGet('rest/user/getUser',
                {id: this.$scope.learningObject.creator.id}).then(({ data: user }) => {
                if (user) {
                    this.setState(user)
                }
            })
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
controller.$inject = [
    '$scope',
    'serverCallService',
    'toastService',
    'authenticatedUserService'
]
/**
 * Declaring this as a directive since we need to use it as an attribute on
 * <md-menu-item> (component usage is restricted to element tagname only).
 */
directive('dopRestrict', {
    scope: {
        learningObject: '<'
    },
    templateUrl: 'directives/restrict/restrict.html',
    controller
})
}
