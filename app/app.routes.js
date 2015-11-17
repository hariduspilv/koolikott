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
                    'directives/materialBox/materialBox'
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
                    'directives/slideshare/slideshare'
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
                    'directives/materialBox/materialBox'
                ]
            },
            '/addPortfolio': {
                templateUrl: 'views/addPortfolio/addPortfolio.html',
                controller: 'addPortfolioController',
                dependencies: [
                    'views/addPortfolio/addPortfolio'
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
                controller: 'loginController',
                dependencies: [
                    'views/dev/login/login'
                ]
            }
        }
    };
});
