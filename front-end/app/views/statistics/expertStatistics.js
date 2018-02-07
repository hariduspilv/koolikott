'use strict'

{
class controller extends Controller {
    constructor(...args) {
        super(...args)
        
        this.getModerators()

        this.$scope.isSubmitButtonEnabled = false
        this.$scope.isExpertsSelectVisible = true
        this.$scope.isTaxonSelectVisible = true
        this.$scope.params = {}
        this.$scope.filter = {
            from: moment().subtract(1, 'month').startOf('month').toDate(),
            to: moment().subtract(1, 'month').endOf('month').toDate(),
        }
        this.$scope.isInfoTextOpen = false

        this.$scope.$watch('filter', this.onFilterChange.bind(this), true)
        this.$scope.$watch('params', this.onParamsChange.bind(this), true)
        this.$scope.$watch('educationalContext', this.onEducationalContextChange.bind(this), true)

        // Set the info text height in pixels for css-animatable collapse
        this.onResizeWindow = this.onResizeWindow.bind(this)
        window.addEventListener('resize', this.onResizeWindow)
        this.$scope.$on('$destroy', () => window.removeEventListener('resize', this.onResizeWindow))
        this.onResizeWindow()

        this.$scope.$watch('rows', (rows) => console.log('rows:', rows), true)
        this.$scope.$watch('sum', (sum) => console.log('sum:', sum), true)
    }
    getModerators() {
        this.serverCallService
            .makeGet('rest/admin/moderator')
            .then(res => this.$scope.moderators = res.data)
    }
    getStatistics() {
        console.time('statistics request')
        this.serverCallService
            .makePost('rest/admin/statistics', this.$scope.params)
            .then(({ status, data: { rows, sum } }) => {
                console.timeEnd('statistics request')
                if (200 <= status && status < 300) {
                    this.$scope.rows = rows
                    this.$scope.sum = sum
                }
            })
    }
    // Copy filter values to POST params with one exception: params.users = [filter.user].
    onFilterChange(filter) {
        const params = Object.assign({}, filter)

        if (params.user) {
            params.users = [params.user]
            delete params.user
        }

        this.$scope.params = params
    }
    onParamsChange({ from, to, users, taxons }) {
        this.$scope.isSubmitButtonEnabled = from && to && (users || taxons)
        this.$scope.isTaxonSelectVisible = !users
    }
    onEducationalContextChange(educationalContext) {
        this.$scope.isExpertsSelectVisible = !educationalContext
    }
    clearFilter() {
        this.$scope.filter = {}
        this.$scope.educationalContext = undefined
    }
    onSelectTaxons(taxons) {
        this.$scope.filter.taxons = taxons
    }
    onResizeWindow() {
        requestAnimationFrame(() => {
            const infoText = document.querySelector('.statistics-info-text')
            infoText.style.height = infoText.scrollHeight + 'px'
        })
    }
}
controller.$inject = [
    '$scope',
    '$location',
    'serverCallService',
]
angular.module('koolikottApp').controller('statisticsController', controller)
}
