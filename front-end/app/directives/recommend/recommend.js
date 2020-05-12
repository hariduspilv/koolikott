'use strict'

{
class controller extends Controller {
    $onInit() {
        this.learningObject = this.$scope.material || this.$scope.portfolio
        this.restUrlBase = "rest/admin/learningObject/";

        this.$scope.recommend = () => this.post('recommend')
        this.$scope.removeRecommendation = () => this.post('removeRecommendation')
    }
    post(endpoint) {
        if (this.authenticatedUserService.isAdmin() && (this.$scope.material || this.$scope.portfolio)) {
            const {id, type} = this.$scope.material || this.$scope.portfolio;
            this.serverCallService.makePost(
                this.restUrlBase + endpoint,
                {id, type},
                this.querySuccess.bind(this),
                () => console.log('Request failed')
            )
        }
    }
    querySuccess(recommendation) {
        if (typeof this.$scope.onChange === 'function')
            this.$scope.onChange({
                recommendation: recommendation || null
            })

        this.$rootScope.$broadcast('recommendations:updated')
    }
}
controller.$inject = [
    '$scope',
    'serverCallService',
    'authenticatedUserService',
    '$rootScope'
]
/**
 * Declaring this as a directive since we need to use it as an attribute on
 * <md-menu-item> (component usage is restricted to element tagname only).
 */
directive('dopRecommend', {
    scope: {
        material: '<',
        portfolio: '<',
        onChange: '&?'
    },
    templateUrl: '/directives/recommend/recommend.html',
    controller
})
}
