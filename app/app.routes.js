define(function() {
    return {
        defaultRoutePath: '/',
        routes: {
            '/': {
                templateUrl: 'views/home/home.html',
                controllerUrl: 'views/home/home'
            },
            '/search/result': {
                templateUrl: 'views/search/result/searchResult.html',
                controllerUrl: 'views/search/result/searchResult'
            },
            '/material': {
                templateUrl: 'views/material/material.html',
                controllerUrl: 'views/material/material'
            },
            '/help': {
                templateUrl: 'views/static/abstractStaticPage.html',
                controllerUrl: 'views/static/help/help'
            },
            '/about': {
                templateUrl: 'views/static/abstractStaticPage.html',
                controllerUrl: 'views/static/about/about'
            },
            '/portfolio': {
                templateUrl: 'views/portfolio/portfolio.html',
                controllerUrl: 'views/portfolio/portfolio'
            },
            '/portfolio/edit': {
                templateUrl: 'views/editPortfolio/editPortfolio.html',
                controllerUrl: 'views/editPortfolio/editPortfolio'
            },
            '/dashboard': {
                templateUrl: 'views/dashboard/dashboard.html',
                controllerUrl: 'views/dashboard/dashboard',
                permissions: ['ADMIN']
            },
            '/loginRedirect': {
                templateUrl: 'views/loginRedirect/loginRedirect.html',
                controllerUrl: 'views/loginRedirect/loginRedirect'
            },
            '/:username': {
                templateUrl: 'views/profile/profile.html',
                controllerUrl: 'views/profile/profile'
            },
            '/dev/login/:idCode': {
                templateUrl: 'views/dev/login/login.html',
                controllerUrl: 'views/dev/login/login'
            }
        }
    };
});
