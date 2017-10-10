'use strict'

{
class controller extends Controller {
    $onInit() {
        if (!this.$scope.url)
            this.$scope.url = this.$location.absUrl()

        this.$scope.showToast = () => this.toastService.show('COPY_PERMALINK_SUCCESS')
    }
}
controller.$inject = ['$scope', '$location', 'toastService']

/**
 * Declaring this as a directive since we need to use it as an attribute on
 * <md-menu-item> (component usage is restricted to element tagname only).
 */
angular.module('koolikottApp').directive('dopCopyPermalink', () => ({
    scope: {
        url: '='
    },
    templateUrl: 'directives/copyPermalink/copyPermalink.html',
    link(scope, element) {
        const button = element.find('button')
        let _id = button.attr('id')
        
        if (!_id) {
            _id = 'ngclipboard' + Date.now()
            button.attr('id', _id)
        }

        new Clipboard('#' + _id)
    },
    controller
}))
}
