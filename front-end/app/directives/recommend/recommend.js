'use strict'

{
class controller extends Controller {
    $onInit() {
        this.learningObject = this.$scope.material || this.$scope.portfolio
        this.restUrlBase = `rest/${this.$scope.material ? 'material' : 'portfolio'}/`

        this.$scope.recommend = () => this.post('recommend')
        this.$scope.removeRecommendation = () => this.post('removeRecommendation')
    }
    post(endpoint) {
        if (this.authenticatedUserService.isAdmin() && this.learningObject) {
            this.serverCallService.makePost(
                this.restUrlBase + endpoint,
                this.learningObject,
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
controller.$inject = ['$scope', 'serverCallService', 'authenticatedUserService', '$rootScope']

/**
 * Declaring this as a directive since we need to use it as an attribute on
 * <md-menu-item> (component usage is restricted to element tagname only).
 */
angular.module('koolikottApp').directive('dopRecommend', () => ({
    scope: {
        material: '<',
        portfolio: '<',
        onChange: '&?'
    },
    templateUrl: 'directives/recommend/recommend.html',
    controller
}))
}
