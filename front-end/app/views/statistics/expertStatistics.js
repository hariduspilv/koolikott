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
            from: moment().subtract(1, 'year').startOf('year').toDate(), // moment().subtract(1, 'month').startOf('month').toDate(),
            to: moment().subtract(1, 'year').endOf('year').toDate(), // moment().subtract(1, 'month').endOf('month').toDate(),
        }
        this.$scope.isInfoTextOpen = false
        this.$scope.data = {}

        this.$scope.$watch('filter', this.onFilterChange.bind(this), true)
        this.$scope.$watch('params', this.onParamsChange.bind(this), true)
        this.$scope.$watch('educationalContext', this.onEducationalContextChange.bind(this), true)

        // Set the info text height in pixels for css-animatable collapse
        this.onResizeWindow = this.onResizeWindow.bind(this)
        window.addEventListener('resize', this.onResizeWindow)
        this.$scope.$on('$destroy', () => window.removeEventListener('resize', this.onResizeWindow))
        setTimeout(this.onResizeWindow, 1000)

        this.$scope.$watch('data', (data) => console.log('data:', data), true)
    }
    getModerators() {
        this.serverCallService
            .makeGet('rest/admin/moderator')
            .then(res => this.$scope.moderators = res.data)
    }
    getStatistics() {
        const params = this.getPostParams()
        console.log('POST rest/admin/statistics', JSON.stringify(params, null, 2))
        console.time('statistics request')
        this.serverCallService
            .makePost('rest/admin/statistics', params)
            .then(({ status, data: { rows, sum } }) => {
                console.timeEnd('statistics request')
                console.log('status:', status, angular.equals(this.$scope.data, { rows, sum }) ? 'fetched data is equal' : 'fetched data is different', rows, sum)
                if (200 <= status && status < 300)
                    Object.assign(this.$scope.data, {
                        rows: this.getFlattenedRows(rows),
                        sum,
                    })
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
    getPostParams() {
        const params = Object.assign({}, this.$scope.params)

        if (params.taxons) {
            params.taxons = params.taxons.map(({ id, level }) => ({ id, level }))
        }

        return params
    }
    getFlattenedRows(rows) {
        return rows.map(educationalContextRow => {
            const rows = educationalContextRow.rows.reduce((flattenedDomainRows, domainRow) => {
                if (domainRow.domainUsed)
                    flattenedDomainRows.push(domainRow)

                else if (domainRow.subjects.length)
                    [].push.apply(flattenedDomainRows, domainRow.subjects)

                return flattenedDomainRows
            }, [])

            return Object.assign({}, educationalContextRow, { rows })
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
