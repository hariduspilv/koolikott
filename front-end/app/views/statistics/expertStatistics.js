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
        this.$scope.data = {}
        this.$scope.sortBy = 'byEducationalContext'
        this.$scope.onSort = this.onSort.bind(this)

        this.$scope.$watch('filter', this.onFilterChange.bind(this), true)
        this.$scope.$watch('params', this.onParamsChange.bind(this), true)
        this.$scope.$watch('educationalContext', this.onEducationalContextChange.bind(this), true)

        // Set the info text height in pixels for css-animatable collapse
        this.onResizeWindow = this.onResizeWindow.bind(this)
        window.addEventListener('resize', this.onResizeWindow)
        this.$scope.$on('$destroy', () => window.removeEventListener('resize', this.onResizeWindow))
        setTimeout(this.onResizeWindow, 1000)
    }
    getModerators() {
        this.serverCallService
            .makeGet('rest/admin/moderator')
            .then(res => this.$scope.moderators = res.data)
    }
    getStatistics() {
        this.$scope.fetching = true
        this.serverCallService
            .makePost('rest/admin/statistics', this.getPostParams())
            .then(({ status, data: { rows, sum } }) => {
                this.$scope.fetching = false
                
                if (200 <= status && status < 300) {
                    Object.assign(this.$scope.data, { rows: this.getFlattenedRows(rows), sum })
                    this.onSort(this.$scope.sortBy)
                }
            })
    }
    downloadStatistics(format) {
        this.$scope.fetchingDownload = true
        this.serverCallService
            .makePost(`rest/admin/statistics/export/`, Object.assign(this.getPostParams(), { format }))
            .then(({ status, data: filename }) => {
                this.$scope.fetchingDownload = false

                if (200 <= status && status < 300) {
                    const downloadLink = document.createElement('a')
                    downloadLink.href = `/rest/admin/statistics/export/download/${filename}`
                    downloadLink.dispatchEvent(new MouseEvent('click'))
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
        this.$scope.sortBy = educationalContext ? 'byDomainOrSubject' : 'byEducationalContext'
    }
    clear() {
        this.$scope.filter = {}
        this.$scope.educationalContext = undefined
        this.$scope.data = {}
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

        if (params.users) {
            params.users = params.users.map(({ id }) => ({ id }))
        }

        return params
    }
    getFlattenedRows(rows) {
        return rows.reduce((flattenedRows, educationalContextRow) => {
            const rows = educationalContextRow.rows.reduce((flattenedDomainRows, domainOrSubjectRow) => {
                if (domainOrSubjectRow.domainUsed || domainOrSubjectRow.noUsersFound)
                    flattenedDomainRows.push(domainOrSubjectRow)

                if (domainOrSubjectRow.subjects.length)
                    [].push.apply(flattenedDomainRows, domainOrSubjectRow.subjects)

                return flattenedDomainRows
            }, [])

            ;[].push.apply(flattenedRows, rows)

            return flattenedRows
        }, [])
    }
    onSort(order) {
        this.$scope.sortBy = order
        this.sortService.orderItems(this.$scope.data.rows, order)
    }
    openDownloadMenu($mdMenu, evt) {
        $mdMenu.open(evt)
    }
}
controller.$inject = [
    '$scope',
    'serverCallService',
    'sortService',
]
angular.module('koolikottApp').controller('statisticsController', controller)
}
