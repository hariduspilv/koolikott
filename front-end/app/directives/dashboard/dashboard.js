'use strict'

/**
 * DIRECTIVES: A template per each dashboard view
 */
{
const lists = {
    dopUnreviewed: 'directives/dashboard/unreviewed.html',
    dopImproper: 'directives/dashboard/improper.html',
    dopChanged: 'directives/dashboard/changed.html',
    dopBroken: 'directives/dashboard/broken.html',
    dopDeleted: 'directives/dashboard/deleted.html',
    dopUsers: 'directives/dashboard/users.html'
}
Object.keys(lists).forEach(key =>
    directive(key, {
        templateUrl: lists[key]
    })
)
}

/**
 * COMPONENT: Learning Object type icon with tooltip for the list views
 */
{
class controller extends Controller {
    $onChanges({ learningObject: { currentValue } }) {
        if (currentValue)
            if (this.isPortfolio(currentValue)) {
                this.$scope.icon = 'book'
                this.$scope.labelKey = 'PORTFOLIO_RESOURCE'
            } else {
                this.$scope.icon = 'description'
                this.$scope.labelKey = 'MATERIAL'
            }
    }
}
controller.$inject = ['$scope']
component('dopDashboardTypeIcon', {
    bindings: {
        learningObject: '<'
    },
    templateUrl: 'directives/dashboard/typeIcon.html',
    controller
})
}
