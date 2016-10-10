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
                controllerUrl: 'views/search/result/searchResult',
                reloadOnSearch : false
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
                controllerUrl: 'views/portfolio/portfolio',
                reloadOnSearch: false
            },
            '/portfolio/edit': {
                templateUrl: 'views/editPortfolio/editPortfolio.html',
                controllerUrl: 'views/editPortfolio/editPortfolio',
                reloadOnSearch: false
            },
            // Dashboard links
            '/dashboard/improperMaterials': {
                templateUrl: 'views/dashboard/pages/improperMaterials.html',
                controllerUrl: 'views/dashboard/dashboard',
                permissions: ['ADMIN', 'MODERATOR']
            },
            '/dashboard/improperPortfolios': {
                templateUrl: 'views/dashboard/pages/improperPortfolios.html',
                controllerUrl: 'views/dashboard/dashboard',
                permissions: ['ADMIN', 'MODERATOR']
            },
            '/dashboard/brokenMaterials': {
                templateUrl: 'views/dashboard/pages/brokenMaterials.html',
                controllerUrl: 'views/dashboard/dashboard',
                permissions: ['ADMIN', 'MODERATOR']
            },
            '/dashboard/deletedMaterials': {
                templateUrl: 'views/dashboard/pages/deletedMaterials.html',
                controllerUrl: 'views/dashboard/dashboard',
                permissions: ['ADMIN', 'MODERATOR']
            },
            '/dashboard/deletedPortfolios': {
                templateUrl: 'views/dashboard/pages/deletedPortfolios.html',
                controllerUrl: 'views/dashboard/dashboard',
                permissions: ['ADMIN', 'MODERATOR']
            },

            '/loginRedirect': {
                templateUrl: 'views/loginRedirect/loginRedirect.html',
                controllerUrl: 'views/loginRedirect/loginRedirect'
            },
            '/:username': {
                templateUrl: 'views/profile/portfolios.html',
                controllerUrl: 'views/profile/profile'
            },
            '/:username/materials': {
                templateUrl: 'views/profile/materials.html',
                controllerUrl: 'views/profile/profile'
            },
            '/:username/favorites': {
                templateUrl: 'views/profile/favorites.html',
                controllerUrl: 'views/profile/profile'
            },
            '/dev/login/:idCode': {
                templateUrl: 'views/dev/login/login.html',
                controllerUrl: 'views/dev/login/login'
            }
        }
    };
});
