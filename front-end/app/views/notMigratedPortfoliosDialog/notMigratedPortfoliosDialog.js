'use strict';

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.portfolios = this.locals.portfolios

            this.$scope.agree = () => {
                this.$mdDialog.hide({agreed: true})
            }
        }
    }

    controller.$inject = ['$scope', '$mdDialog', 'locals']

    angular.module('koolikottApp').controller('notMigratedPortfoliosController', controller)
}
