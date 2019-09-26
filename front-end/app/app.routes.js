'use strict';

angular.module('koolikottApp')
    .config([
        '$routeProvider',
        function ($routeProvider) {

            $routeProvider
                .when('/', {
                    templateUrl: 'views/home/home.html',
                    controller: 'homeController',
                    controllerAs: '$ctrl'
                })
                .when('/404', {
                    templateUrl: 'views/404/404.html',
                    controller: '404Controller',
                    controllerAs: '$ctrl'
                })
                .when('/search/result', {
                    templateUrl: 'views/search/result/searchResult.html',
                    controller: 'searchResultController',
                    controllerAs: '$ctrl',
                    reloadOnSearch: false,
                })
                .when('/oppematerjal/:id', {
                    templateUrl: 'views/material/material.html',
                    controller: 'materialController',
                    controllerAs: '$ctrl',
                    reloadOnSearch: false
                })
                .when('/oppematerjal/:id-:name', {
                    templateUrl: 'views/material/material.html',
                    controller: 'materialController',
                    controllerAs: '$ctrl',
                    reloadOnSearch: false
                })
                .when('/oppematerjal', {
                    templateUrl: 'views/material/material.html',
                    controller: 'materialController',
                    controllerAs: '$ctrl',
                    reloadOnSearch: false
                })
                .when('/help', {
                    templateUrl: 'views/static/abstractStaticPage.html',
                    controller: 'helpController',
                    controllerAs: '$ctrl'
                })
                .when('/kogumik/muuda/:id', {
                    templateUrl: 'views/editPortfolio/editPortfolio.html',
                    controller: 'editPortfolioController',
                    controllerAs: '$ctrl',
                    reloadOnSearch: false
                })
                .when('/kogumik/:id', {
                    templateUrl: 'views/portfolio/portfolio.html',
                    controller: 'portfolioController',
                    controllerAs: '$ctrl',
                    reloadOnSearch: false
                })
                .when('/kogumik/:id-:name', {
                    templateUrl: 'views/portfolio/portfolio.html',
                    controller: 'portfolioController',
                    controllerAs: '$ctrl',
                    reloadOnSearch: false
                })
                .when('/kogumik', {
                    templateUrl: 'views/portfolio/portfolio.html',
                    controller: 'portfolioController',
                    controllerAs: '$ctrl',
                    reloadOnSearch: false,
                })
                .when('/toolaud', {
                    templateUrl: 'views/dashboard/dashboard.html',
                    controller: 'dashboardController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN', 'MODERATOR']
                })
                .when('/toolaud/teatatud-oppevara', {
                    templateUrl: 'views/dashboard/baseTableView.html',
                    controller: 'baseTableViewController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN', 'MODERATOR']
                })
                .when('/dashboard/improperPortfolios', {
                    templateUrl: 'views/dashboard/baseTableView.html',
                    controller: 'baseTableViewController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN', 'MODERATOR']
                })
                .when('/toolaud/kustutatud-oppevara', {
                    templateUrl: 'views/dashboard/baseTableView.html',
                    controller: 'baseTableViewController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN']
                })
                .when('/toolaud/aineeksperdid', {
                    templateUrl: 'views/dashboard/baseTableView.html',
                    controller: 'baseTableViewController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN']
                })
                .when('/toolaud/piiratud-kasutajad', {
                    templateUrl: 'views/dashboard/baseTableView.html',
                    controller: 'baseTableViewController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN']
                })
                .when('/toolaud/muudetud-oppevara', {
                    templateUrl: 'views/dashboard/baseTableView.html',
                    controller: 'baseTableViewController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN', 'MODERATOR']
                })
                .when('/toolaud/uus-oppevara', {
                    templateUrl: 'views/dashboard/baseTableView.html',
                    controller: 'baseTableViewController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN', 'MODERATOR']
                })
                .when('/toolaud/saadetud-teated', {
                    templateUrl: 'views/dashboard/baseTableView.html',
                    controller: 'baseTableViewController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN', 'MODERATOR']
                })
                .when('/toolaud/ekspertide-statistika', {
                    templateUrl: 'views/statistics/expertStatistics.html',
                    controller: 'statisticsController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN', 'MODERATOR']
                })
                .when('/toolaud/gdpr', {
                    templateUrl: 'views/gdpr/gdpr.html',
                    controller: 'gdprController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN']
                })
                .when('/toolaud/videojuhendid', {
                    templateUrl: 'views/userManualsAdmin/userManualsAdmin.html',
                    controller: 'userManualsAdminController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN']
                })
                .when('/kasutustingimused', {
                    templateUrl: 'views/terms/terms.html',
                    controller: 'termsController',
                    controllerAs: '$ctrl',
                })
                .when('/videojuhendid', {
                    templateUrl: 'views/userManual/userManual.html',
                    controller: 'userManualsController',
                    controllerAs: '$ctrl',
                })
                .when('/kkk', {
                    templateUrl: 'views/faq/faq.html',
                    controller: 'faqController',
                    controllerAs: '$ctrl',
                })
                .when('/profiil', {
                    templateUrl: 'views/userProfile/userProfile.html',
                    controller: 'userProfileController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN', 'MODERATOR', 'USER', 'RESTRICTED']
                })
                .when('/loginRedirect', {
                    templateUrl: 'views/loginRedirect/loginRedirect.html',
                    controller: 'loginRedirectController',
                    controllerAs: '$ctrl'
                })
                .when('/:username', {
                    templateUrl: 'views/profile/profile.html',
                    controller: 'profileController',
                    controllerAs: '$ctrl',
                })
                .when('/:username/oppematerjalid', {
                    templateUrl: 'views/profile/materials/materials.html',
                    controller: 'userMaterialsController',
                    controllerAs: '$ctrl',
                    resolve: UserPathResolver,
                })
                .when('/:username/kogumikud', {
                    templateUrl: 'views/profile/portfolios/portfolios.html',
                    controller: 'userPortfoliosController',
                    controllerAs: '$ctrl',
                })
                .when('/:username/lemmikud', {
                    templateUrl: 'views/profile/favorites/favorites.html',
                    controller: 'userFavoritesController',
                    controllerAs: '$ctrl',
                    resolve: UserPathResolver,
                })
                .when('/:username/materials', {
                    templateUrl: 'views/profile/materials/materials.html',
                    controller: 'userMaterialsController',
                    controllerAs: '$ctrl',
                    resolve: UserPathResolver,
                })
                .when('/:username/portfolios', {
                    templateUrl: 'views/profile/portfolios/portfolios.html',
                    controller: 'userPortfoliosController',
                    controllerAs: '$ctrl',
                })
                .when('/:username/favorites', {
                    templateUrl: 'views/profile/favorites/favorites.html',
                    controller: 'userFavoritesController',
                    controllerAs: '$ctrl',
                    resolve: UserPathResolver,
                })
                .when('/dev/login/:idCode', {
                    templateUrl: 'views/dev/login/login.html',
                    controller: 'devLoginController',
                    controllerAs: '$ctrl'
                })
                .otherwise({
                    redirectTo: '/404'
                });
        }
    ]);

let UserPathResolver = {
    isLoggedIn: ['authenticatedUserService', '$location', '$route', function (authenticatedUserService, $location, $route) {
        if (!authenticatedUserService.isAuthenticated() || ($route.current.params.username !== authenticatedUserService.getUser().username)) {
            $location.path('/');
        }
    }]
};
