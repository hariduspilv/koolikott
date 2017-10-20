'use strict'

{
class controller extends Controller {
    $onInit() {
        this.$rootScope.sideNavOpen = window.innerWidth > BREAK_LG

        this.$scope.isTaxonomyOpen = true
        this.$scope.dashboardOpen = this.$location.path().startsWith("/dashboard")

        // List of taxon icons
        this.$scope.taxonIcons = [
            'extension',
            'accessibility',
            'school',
            'build',
            'palette'
        ]

        this.$scope.checkUser = this.checkUser.bind(this)
        this.$scope.updateCount = this.updateCount.bind(this)
        this.$scope.updateUserCounts = this.updateUserCounts.bind(this)

        this.$scope.$on('dashboard:adminCountsUpdated', this.updateAdminCounts.bind(this))
        this.$scope.$watch(() => this.taxonService.getSidenavTaxons(), (newValue) => {
            if (newValue)
                this.$scope.taxon = newValue
        })
        this.$scope.$watch(() => this.$location.url(), () => {
            this.$rootScope.isViewPortfolioAndEdit = (
                this.$location.url().indexOf('/portfolio') != -1 ||
                this.$location.url().indexOf('/search') != -1
            )
        }, true)
        this.$scope.$watch(() => this.authenticatedUserService.getUser(), (user) => {
            this.$scope.user = user
            this.$scope.updateUserCounts()
        }, true)

        this.$scope.$on('header:red', () => this.$scope.isHeaderRed = true)
        this.$scope.$on('header:default', () => this.$scope.isHeaderRed = false)
    }
    checkUser(evt, redirectURL) {
        if (this.$scope.user)
            return this.$location.url('/' + this.$scope.user.username + redirectURL)

        this.$rootScope.afterAuthRedirectURL = redirectURL
        this.$rootScope.sidenavLogin = redirectURL
        this.$mdDialog.show({
            templateUrl: 'views/loginDialog/loginDialog.html',
            controller: 'loginDialogController',
            targetEvent: evt,
            clickOutsideToClose: true,
            escapeToClose: true
        })
    }
    isAdminOrModerator() {
        return (
            this.authenticatedUserService.isModerator() ||
            this.authenticatedUserService.isAdmin()
        )
    }
    /**
     * @param {string} type - One of:
     *  - unReviewedLearningObjects
     *  - improperMaterials
     *  - improperPortfolios
     *  - changedLearningObject
     *  - brokenMaterials
     *  - deletedMaterials
     *  - deletedPortfolios
     *  - moderators
     *  - restrictedUsers
     *  - userPortfolios
     *  - userMaterials
     *  - userFavorites
     */
    updateCount(type) {
        const isPersonal = type.startsWith('user')

        if (!isPersonal || this.authenticatedUserService.isAuthenticated()) {
            const methodName = `load${type[0].toUpperCase() + type.slice(1)}Count`

            if (typeof this.userDataService[methodName] === 'function')
                this.userDataService[methodName](count => {
                    if (!isPersonal || count >= 0)
                        this.$scope[type+'Count'] = count
                })
        }
    }
    updateUserCounts() {
        if (this.authenticatedUserService.isAuthenticated()) {
            this.updateCount('userFavorites')
            this.updateCount('userMaterials')
            this.updateCount('userPortfolios')
            this.updateAdminCounts()
        }
    }
    updateAdminCounts() {
        if (
            this.authenticatedUserService.isAdmin() ||
            this.authenticatedUserService.isModerator()
        ) {
            this.updateCount('brokenMaterials')
            this.updateCount('deletedMaterials')
            this.updateCount('deletedPortfolios')
            this.updateCount('improperMaterials')
            this.updateCount('improperPortfolios')
            this.updateCount('changedLearningObject')
            this.updateCount('unReviewedLearningObject')
        }
        if (this.authenticatedUserService.isAdmin()) {
            this.updateCount('moderators')
            this.updateCount('restrictedUsers')
        }
    }
    dashboardSearch() {
        if (this.$scope.dashboardOpen === false) {
            this.$location.url("/dashboard")
            this.$scope.dashboardOpen = true
        } else {
            this.$scope.dashboardOpen = false
        }
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$location',
    '$mdDialog',
    'authenticatedUserService',
    'userDataService',
    'taxonService'
]

angular.module('koolikottApp').component('dopSidenav', {
    templateUrl: 'directives/sidenav/sidenav.html',
    controller
})
}
