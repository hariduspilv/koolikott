'use strict';

angular.module('koolikottApp')
    .config([
        '$routeProvider',
        function ($routeProvider) {

            $routeProvider.otherwise('/');

            $routeProvider
                .when('/', {
                    templateUrl: 'views/home/home.html',
                    controller: 'homeController',
                    controllerAs: '$ctrl'
                })
                .when('/search/result', {
                    templateUrl: 'views/search/result/searchResult.html',
                    controller: 'searchResultController',
                    controllerAs: '$ctrl',
                    reloadOnSearch: false,
                })
                .when('/material', {
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
                .when('/about', {
                    templateUrl: 'views/static/abstractStaticPage.html',
                    controller: 'aboutController',
                    controllerAs: '$ctrl',
                })
                .when('/portfolio', {
                    templateUrl: 'views/portfolio/portfolio.html',
                    controller: 'portfolioController',
                    controllerAs: '$ctrl',
                    reloadOnSearch: false
                })
                .when('/portfolio/edit', {
                    templateUrl: 'views/editPortfolio/editPortfolio.html',
                    controller: 'editPortfolioController',
                    controllerAs: '$ctrl',
                    reloadOnSearch: false
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
                .when('/usermanuals', {
                    templateUrl: 'views/userManual/userManual.html',
                    controller: 'userManualsController',
                    controllerAs: '$ctrl',
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
                .when('/:username/favorites', {
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
