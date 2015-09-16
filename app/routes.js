define([], function()
{
    return {
        defaultRoutePath: '/',
        routes: {
            '/': {
                templateUrl: 'app/views/home/home.html',
                controller: 'homeController',
                dependencies: [
                    'views/home/home',
                    'directives/materialBox/materialBox'
                ]
            },
            '/search/result': {
                templateUrl: 'app/views/search/result/searchResult.html',
                controller: 'searchResultController',
                dependencies: [
                    'views/search/result/searchResult',
                    'directives/materialBox/materialBox'
                ]
            },
            '/material': {
                templateUrl: 'app/views/material/material.html',
                controller: 'materialController',
                dependencies: [
                    'views/material/material'
                ]
            },
            '/help': {
                templateUrl: 'app/views/static/abstractStaticPage.html',
                controller: 'helpController',
                dependencies: [
                    'views/static/help/help',
                    'views/static/abstractStaticPage'
                ]
            },
            '/about': {
                templateUrl: 'app/views/static/abstractStaticPage.html',
                controller: 'aboutController',
                dependencies: [
                    'views/static/about/about',
                    'views/static/abstractStaticPage'
                ]
            },
            '/portfolio': {
                templateUrl: 'app/views/portfolio/portfolio.html',
                controller: 'portfolioController',
                dependencies: [
                    'views/portfolio/portfolio',
                    'directives/chapter/chapter'
                ]
            },
            '/loginRedirect': {
                templateUrl: 'app/views/loginRedirect/loginRedirect.html',
                controller: 'loginRedirectController',
                dependencies: [
                    'views/loginRedirect/loginRedirect'
                ]
            },
            '/:username': {
                templateUrl: 'app/views/profile/profile.html',
                controller: 'profileController',
                dependencies: [
                    'views/profile/profile',
                    'directives/materialBox/materialBox',
                    'directives/portfolioBox/portfolioBox'
                ]
            },
            '/dev/login/:idCode': {
                templateUrl: 'app/views/dev/login/login.html',
                controller: 'loginController',
                dependencies: [
                    'views/dev/login/login'
                ]
            }
        }
    };
});
