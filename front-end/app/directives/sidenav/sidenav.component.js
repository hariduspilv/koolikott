'use strict'

{
class controller extends Controller {
    $onInit() {
        this.$rootScope.sideNavOpen = window.innerWidth > BREAK_LG

        this.$scope.isAuthenticated = this.authenticatedUserService.isAuthenticated()
        this.$rootScope.isTaxonomyOpen = !this.authenticatedUserService.isAuthenticated()
        this.$scope.isAdmin = this.authenticatedUserService.isAdmin()
        this.$scope.isModerator = this.authenticatedUserService.isModerator()
        this.$scope.toggleSidenav = () => this.$mdSidenav('left').toggle()
        this.getUserManualsCount()


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
            '/dashboard/improper',
            '/dashboard/unReviewed',
            '/dashboard/changes',
            '/dashboard/moderators',
            '/dashboard/restrictedUsers',
            '/dashboard/deleted',
            '/dashboard/stat/expert',
        ];

        this.$scope.isLocationActive = this.isLocationActive.bind(this)
        this.$scope.checkUser = this.checkUser.bind(this)
        this.$scope.updateCount = this.updateCount.bind(this)
        this.$scope.updateUserCounts = this.updateUserCounts.bind(this)
        this.$scope.closeOtherTabs = this.closeOtherTabs.bind(this)
        this.$scope.isAdminOrModerator = this.isAdminOrModerator.bind(this)
        this.$scope.isAdminUser = this.isAdminUser.bind(this)

        this.$scope.$on('dashboard:adminCountsUpdated', this.updateAdminCounts.bind(this))
        this.$rootScope.$on('login:success', this.userChange.bind(this));
        this.$rootScope.$on('logout:success', this.userChange.bind(this));
        this.$scope.$watch(() => this.taxonService.getSidenavTaxons(), (newValue) => {
            if (newValue)
                this.$scope.taxon = newValue
        })

        this.$scope.$watch(() => this.$location.url(), () => {
            this.$rootScope.isViewPortfolioAndEdit = (
                this.$location.url().indexOf('/portfolio') !== -1 ||
                this.$location.url().indexOf('/search') !== -1
            )
        }, true)

        this.$scope.$watch(() => this.authenticatedUserService.getUser(), () => {
            this.userChange()
        }, true)
        this.$scope.$on('header:red', () => this.$scope.isHeaderRed = true)
        this.$scope.$on('header:default', () => this.$scope.isHeaderRed = false)
    }
    userChange() {
        this.$scope.isAuthenticated = this.authenticatedUserService.isAuthenticated();
        this.$scope.user = this.authenticatedUserService.getUser();
        this.$scope.isAdmin = this.authenticatedUserService.isAdmin();
        this.$scope.isModerator = this.authenticatedUserService.isModerator();
        this.$rootScope.isTaxonomyOpen = !this.authenticatedUserService.isAuthenticated();
        this.$scope.updateUserCounts();

        if (!this.$scope.isAuthenticated) {
            this.$rootScope.isUserTabOpen = false
            this.$rootScope.isAdminTabOpen = false
        }

    }
    isLocationActive(menuLocation) {
        if (!this.$scope.user)
            return false

        const currentLocation = this.$location.path()

        if (currentLocation === "/")
            return false

        if (!this.$scope.isAdmin && !this.$scope.isModerator)
            return menuLocation === currentLocation

        const isInMenu = this.adminLocations.includes(currentLocation) || this.isUserLocation(currentLocation)

        return isInMenu
            ? menuLocation === currentLocation
            : this.$rootScope.learningObjectPrivate
                ? false
                : this.$rootScope.learningObjectDeleted
                    ? menuLocation === '/dashboard/deleted'
                    : this.$rootScope.learningObjectImproper
                        ? menuLocation === '/dashboard/improper'
                        : this.$rootScope.learningObjectUnreviewed
                            ? menuLocation === '/dashboard/unReviewed'
                            : this.$rootScope.learningObjectChanged
                                ? menuLocation === '/dashboard/changes'
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
    isAdminUser() {
        return this.authenticatedUserService.isAdmin()
    }
    /**
     * @param {string} type - One of:
     *  - unReviewedLearningObjects
     *  - improper
     *  - changedLearningObject
     *  - deleted
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
            this.updateCount('deleted')
            this.updateCount('improper')
            this.updateCount('changes')
            this.updateCount('unReviewedLearningObjects')
        }
        if (this.authenticatedUserService.isAdmin()) {
            this.updateCount('moderators')
            this.updateCount('restrictedUsers')
        }
    }
    closeOtherTabs(evt) {
        if (evt.currentTarget.id === 'Admin') {
            this.$rootScope.isUserTabOpen = false
            this.$rootScope.isTaxonomyOpen = false
            this.$rootScope.isAboutTabOpen = false
        }
        else if (evt.currentTarget.id === 'myProfile') {
            this.$rootScope.isAdminTabOpen = false
            this.$rootScope.isTaxonomyOpen = false
            this.$rootScope.isAboutTabOpen = false
        } else if (evt.currentTarget.id === 'About') {
            this.$rootScope.isAdminTabOpen = false
            this.$rootScope.isTaxonomyOpen = false
            this.$rootScope.isUserTabOpen = false
        } else {
            this.$rootScope.isAdminTabOpen = false
            this.$rootScope.isUserTabOpen = false
            this.$rootScope.isAboutTabOpen = false
        }
    }

    getUserManualsCount() {
        this.userManualsAdminService.getUserManuals()
            .then(data => {
                this.$scope.userManualsCount = data.data.length
            })
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$location',
    '$mdDialog',
    '$mdSidenav',
    'authenticatedUserService',
    'userDataService',
    'taxonService',
    'userManualsAdminService',
]
component('dopSidenav', {
    bindings: {
        isEditPortfolio: '<'
    },
    templateUrl: 'directives/sidenav/sidenav.html',
    controller
})
}
