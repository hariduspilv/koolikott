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
            '/dashboard/improper/material': {
                templateUrl: 'views/dashboard/improper/material/improperMaterial.html',
                controllerUrl: 'views/dashboard/improper/material/improperMaterial',
                permissions: ['ADMIN']
            },
            '/dashboard/improper/portfolio': {
                templateUrl: 'views/dashboard/improper/portfolio/improperPortfolio.html',
                controllerUrl: 'views/dashboard/improper/portfolio/improperPortfolio',
                permissions: ['ADMIN']
            },
            '/dashboard/broken/material': {
                templateUrl: 'views/dashboard/broken/material/brokenMaterial.html',
                controllerUrl: 'views/dashboard/broken/material/brokenMaterial',
                permissions: ['ADMIN'],
            },
            '/dashboard/deleted/material': {
                templateUrl: 'views/dashboard/deleted/material/deletedMaterial.html',
                controllerUrl: 'views/dashboard/deleted/material/deletedMaterial',
                permissions: ['ADMIN']
            },
            '/dashboard/deleted/portfolio': {
                templateUrl: 'views/dashboard/deleted/portfolio/deletedPortfolio.html',
                controllerUrl: 'views/dashboard/deleted/portfolio/deletedPortfolio',
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
