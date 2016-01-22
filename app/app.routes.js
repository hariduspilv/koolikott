define([], function()
{
    return {
        defaultRoutePath: '/',
        routes: {
            '/': {
                templateUrl: 'views/home/home.html',
                controller: 'homeController',
                dependencies: [
                    'views/home/home',
                    'directives/materialBox/materialBox',
                    'directives/preloaderCard/preloaderCard'
                ]
            },
            '/search/result': {
                templateUrl: 'views/search/result/searchResult.html',
                controller: 'searchResultController',
                dependencies: [
                    'views/search/result/searchResult',
                    'directives/materialBox/materialBox',
                    'directives/portfolioBox/portfolioBox'
                ]
            },
            '/material': {
                templateUrl: 'views/material/material.html',
                controller: 'materialController',
                dependencies: [
                    'views/material/material',
                    'directives/slideshare/slideshare',
                    'services/dialogService'
                ]
            },
            '/help': {
                templateUrl: 'views/static/abstractStaticPage.html',
                controller: 'helpController',
                dependencies: [
                    'views/static/help/help',
                    'views/static/abstractStaticPage'
                ]
            },
            '/about': {
                templateUrl: 'views/static/abstractStaticPage.html',
                controller: 'aboutController',
                dependencies: [
                    'views/static/about/about',
                    'views/static/abstractStaticPage'
                ]
            },
            '/portfolio': {
                templateUrl: 'views/portfolio/portfolio.html',
                controller: 'portfolioController',
                dependencies: [
                    'views/portfolio/portfolio',
                    'directives/chapter/chapter',
                    'directives/materialBox/materialBox',
                    'directives/portfolioSummaryCard/portfolioSummaryCard',
                    'services/dialogService'
                ]
            },
            '/portfolio/edit': {
                templateUrl: 'views/editPortfolio/editPortfolio.html',
                controller: 'editPortfolioController',
                dependencies: [
                    'views/editPortfolio/editPortfolio',
                    'directives/chapter/chapter',
                    'directives/materialBox/materialBox',
                    'directives/portfolioSummaryCard/portfolioSummaryCard',
                    'services/dialogService'
                ]
            },
            '/dashboard': {
                templateUrl: 'views/dashboard/dashboard.html',
                controller: 'dashboardController',
                permissions: ['ADMIN'],
                dependencies: [
                   'views/dashboard/dashboard'
               ]
            },
            '/dashboard/improper/material': {
                templateUrl: 'views/dashboard/improper/material/improperMaterial.html',
                controller: 'improperMaterialController',
                permissions: ['ADMIN'],
                dependencies: [
                    'views/dashboard/dashboard',
                    'views/dashboard/improper/material/improperMaterial'
                ]
            },
            '/dashboard/improper/portfolio': {
                templateUrl: 'views/dashboard/improper/portfolio/improperPortfolio.html',
                controller: 'improperPortfolioController',
                permissions: ['ADMIN'],
                dependencies: [
                    'views/dashboard/dashboard',
                    'views/dashboard/improper/portfolio/improperPortfolio'
                ]
            },
            '/dashboard/broken/material': {
                templateUrl: 'views/dashboard/broken/material/brokenMaterial.html',
                controller: 'brokenMaterialController',
                permissions: ['ADMIN'],
                dependencies: [
                    'views/dashboard/dashboard',
                    'views/dashboard/broken/material/brokenMaterial'
                ]
            },
            '/dashboard/deleted/material': {
                templateUrl: 'views/dashboard/deleted/material/deletedMaterial.html',
                controller: 'deletedMaterialController',
                permissions: ['ADMIN'],
                dependencies: [
                    'views/dashboard/dashboard',
                    'views/dashboard/deleted/material/deletedMaterial'
                ]
            },
            '/dashboard/deleted/portfolio': {
                templateUrl: 'views/dashboard/deleted/portfolio/deletedPortfolio.html',
                controller: 'deletedPortfolioController',
                permissions: ['ADMIN'],
                dependencies: [
                    'views/dashboard/dashboard',
                    'views/dashboard/deleted/portfolio/deletedPortfolio'
                ]
            },
            '/loginRedirect': {
                templateUrl: 'views/loginRedirect/loginRedirect.html',
                controller: 'loginRedirectController',
                dependencies: [
                    'views/loginRedirect/loginRedirect'
                ]
            },
            '/:username': {
                templateUrl: 'views/profile/profile.html',
                controller: 'profileController',
                dependencies: [
                    'views/profile/profile',
                    'directives/materialBox/materialBox',
                    'directives/portfolioBox/portfolioBox'
                ]
            },
            '/dev/login/:idCode': {
                templateUrl: 'views/dev/login/login.html',
                controller: 'devLoginController',
                dependencies: [
                    'views/dev/login/login'
                ]
            }
        }
    };
});
