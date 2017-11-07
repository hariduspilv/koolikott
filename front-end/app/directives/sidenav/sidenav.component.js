'use strict'

{
class controller extends Controller {
    $onInit() {
        this.$rootScope.sideNavOpen = window.innerWidth > BREAK_LG

        this.$scope.isTaxonomyOpen = !this.authenticatedUserService.isAuthenticated()
        this.$scope.isAdmin = this.authenticatedUserService.isAdmin()

        // List of taxon icons
        this.$scope.taxonIcons = [
            'extension',
            'accessibility',
            'school',
            'build',
            'palette'
        ]
        // List of sidenav adminLocations
        this.adminLocations = [
            '/dashboard/improperMaterials',
            '/dashboard/improperPortfolios',
            '/dashboard/unReviewed',
            '/dashboard/changedLearningObjects',
            '/dashboard/moderators',
            '/dashboard/restrictedUsers',
            '/dashboard/deletedMaterials',
            '/dashboard/deletedPortfolios',
            '/dashboard/brokenMaterials',
        ]

        this.$scope.isLocationActive = this.isLocationActive.bind(this)
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
    isLocationActive(menuLocation) {
        if (!this.$scope.user)
            return false

        const currentLocation = $location.path()

        if (currentLocation === "/")
            return false

        if (!this.$scope.modUser())
            return menuLocation === currentLocation

        const isInMenu = this.adminLocations.includes(currentLocation) || this.isUserLocation(currentLocation)
        
        return isInMenu
            ? menuLocation === currentLocation
            : $rootScope.private
                ? false
                : $rootScope.learningObjectDeleted
                    ? menuLocation === '/dashboard/deletedPortfolios'
                    : $rootScope.learningObjectImproper
                        ? menuLocation === '/dashboard/improperPortfolios'
                        : $rootScope.learningObjectUnreviewed
                            ? menuLocation === '/dashboard/unReviewed'
                            : $rootScope.learningObjectChanged
                                ? menuLocation === '/dashboard/changedLearningObjects'
                                : false
    }
    isUserLocation(location) {
        const { username } = this.$scope.user
        const userLocations = [
            `/${username}/portfolios`,
            `/${username}/materials`,
            `/${username}/favorites`
        ]
        return userLocations.includes(location)
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
                    count = parseFloat(count)

                    if (!isPersonal || (!isNaN(count) && count >= 0))
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
            this.updateCount('changedLearningObjects')
            this.updateCount('unReviewedLearningObjects')
        }
        if (this.authenticatedUserService.isAdmin()) {
            this.updateCount('moderators')
            this.updateCount('restrictedUsers')
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
component('dopSidenav', {
    templateUrl: 'directives/sidenav/sidenav.html',
    controller
})
}
