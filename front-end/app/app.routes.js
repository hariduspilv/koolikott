'use strict';

angular.module('koolikottApp')
    .config([
        '$routeProvider',
        function ($routeProvider) {

            $routeProvider.otherwise('/404');

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
                .when('/material/:id', {
                    templateUrl: 'views/material/material.html',
                    controller: 'materialController',
                    controllerAs: '$ctrl',
                    reloadOnSearch: false
                })
                .when('/material/:id-:name', {
                    templateUrl: 'views/material/material.html',
                    controller: 'materialController',
                    controllerAs: '$ctrl',
                    reloadOnSearch: false
                })
                .when('/material', {
                    templateUrl: 'views/material/material.html',
                    controller: 'materialController',
                    controllerAs: '$ctrl',
                    reloadOnSearch: false
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
                .when('/portfolio/:id', {
                    templateUrl: 'views/portfolio/portfolio.html',
                    controller: 'portfolioController',
                    controllerAs: '$ctrl',
                    reloadOnSearch: false
                })
                .when('/portfolio/:id-:name', {
                    templateUrl: 'views/portfolio/portfolio.html',
                    controller: 'portfolioController',
                    controllerAs: '$ctrl',
                    reloadOnSearch: false
                })
                .when('/portfolio', {
                    templateUrl: 'views/portfolio/portfolio.html',
                    controller: 'portfolioController',
                    controllerAs: '$ctrl',
                    reloadOnSearch: false,
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
                .when('/dashboard', {
                    templateUrl: 'views/dashboard/dashboard.html',
                    controller: 'dashboardController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN', 'MODERATOR']
                })
                .when('/dashboard/improper', {
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
                .when('/dashboard/deleted', {
                    templateUrl: 'views/dashboard/baseTableView.html',
                    controller: 'baseTableViewController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN', 'MODERATOR']
                })
                .when('/dashboard/moderators', {
                    templateUrl: 'views/dashboard/baseTableView.html',
                    controller: 'baseTableViewController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN']
                })
                .when('/dashboard/restrictedUsers', {
                    templateUrl: 'views/dashboard/baseTableView.html',
                    controller: 'baseTableViewController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN']
                })
                .when('/dashboard/changes', {
                    templateUrl: 'views/dashboard/baseTableView.html',
                    controller: 'baseTableViewController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN', 'MODERATOR']
                })
                .when('/dashboard/unReviewed', {
                    templateUrl: 'views/dashboard/baseTableView.html',
                    controller: 'baseTableViewController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN', 'MODERATOR']
                })
                .when('/dashboard/sentEmails', {
                    templateUrl: 'views/dashboard/baseTableView.html',
                    controller: 'baseTableViewController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN', 'MODERATOR']
                })
                .when('/dashboard/stat/expert', {
                    templateUrl: 'views/statistics/expertStatistics.html',
                    controller: 'statisticsController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN']
                })
                .when('/dashboard/gdpr', {
                    templateUrl: 'views/gdpr/gdpr.html',
                    controller: 'gdprController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN']
                })
                .when('/dashboard/usermanualsAdmin', {
                    templateUrl: 'views/userManualsAdmin/userManualsAdmin.html',
                    controller: 'userManualsAdminController',
                    controllerAs: '$ctrl',
                    permissions: ['ADMIN']
                })
                .when('/terms', {
                    templateUrl: 'views/terms/terms.html',
                    controller: 'termsController',
                    controllerAs: '$ctrl',
                })
                .when('/usermanuals', {
                    templateUrl: 'views/userManual/userManual.html',
                    controller: 'userManualsController',
                    controllerAs: '$ctrl',
                })
                .when('/faq', {
                    templateUrl: 'views/faq/faq.html',
                    controller: 'faqController',
                    controllerAs: '$ctrl',
                })
                .when('/profile', {
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
                .when('/:username/materials', {
                    templateUrl: 'views/profile/materials/materials.html',
                    controller: 'userMaterialsController',
                    controllerAs: '$ctrl',
                    resolve: UserPathResolver
                })
                .when('/:username/portfolios', {
                    templateUrl: 'views/profile/portfolios/portfolios.html',
                    controller: 'userPortfoliosController',
                    controllerAs: '$ctrl',
                })
                .when('/:username/oppematerjalid', {
                    templateUrl: 'views/profile/materials/materials.html',
                    controller: 'userMaterialsController',
                    controllerAs: '$ctrl',
                    resolve: UserPathResolver
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
                    resolve: UserPathResolver

                })
                .when('/dev/login/:idCode', {
                    templateUrl: 'views/dev/login/login.html',
                    controller: 'devLoginController',
                    controllerAs: '$ctrl'
                });
        }
    ]);

let UserPathResolver = {
    isLoggedIn: ['authenticatedUserService', '$location', function (authenticatedUserService, $location) {
        if (!authenticatedUserService.isAuthenticated()) {
            $location.path("/");
        }
    }]
};
