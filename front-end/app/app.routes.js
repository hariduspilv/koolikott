define(function () {
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
                reloadOnSearch: false
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
            '/dashboard': {
                templateUrl: 'views/dashboard/dashboard.html',
                controllerUrl: 'views/dashboard/dashboard',
                permissions: ['ADMIN', 'MODERATOR']
            },
            //TODO: Same template and controller for everything, can this be refactored to js?
            '/dashboard/improperMaterials': {
                templateUrl: 'views/dashboard/baseTableView.html',
                controllerUrl: 'views/dashboard/baseTableView',
                permissions: ['ADMIN', 'MODERATOR']
            },
            '/dashboard/improperPortfolios': {
                templateUrl: 'views/dashboard/baseTableView.html',
                controllerUrl: 'views/dashboard/baseTableView',
                permissions: ['ADMIN', 'MODERATOR']
            },
            '/dashboard/brokenMaterials': {
                templateUrl: 'views/dashboard/baseTableView.html',
                controllerUrl: 'views/dashboard/baseTableView',
                permissions: ['ADMIN', 'MODERATOR']
            },
            '/dashboard/deletedMaterials': {
                templateUrl: 'views/dashboard/baseTableView.html',
                controllerUrl: 'views/dashboard/baseTableView',
                permissions: ['ADMIN', 'MODERATOR']
            },
            '/dashboard/deletedPortfolios': {
                templateUrl: 'views/dashboard/baseTableView.html',
                controllerUrl: 'views/dashboard/baseTableView',
                permissions: ['ADMIN', 'MODERATOR']
            },
            '/dashboard/moderators': {
                templateUrl: 'views/dashboard/baseTableView.html',
                controllerUrl: 'views/dashboard/baseTableView',
                permissions: ['ADMIN']
            },
            '/dashboard/restrictedUsers': {
                templateUrl: 'views/dashboard/baseTableView.html',
                controllerUrl: 'views/dashboard/baseTableView',
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
            '/:username/materials': {
                templateUrl: 'views/profile/materials/materials.html',
                controllerUrl: 'views/profile/materials/materials',
                permissions: ['USER']
            },
            '/:username/portfolios': {
                templateUrl: 'views/profile/portfolios/portfolios.html',
                controllerUrl: 'views/profile/portfolios/portfolios',
                permissions: ['USER']
            },
            '/:username/favorites': {
                templateUrl: 'views/profile/favorites/favorites.html',
                controllerUrl: 'views/profile/favorites/favorites',
                permissions: ['USER']
            },
            '/dev/login/:idCode': {
                templateUrl: 'views/dev/login/login.html',
                controllerUrl: 'views/dev/login/login'
            }
        }
    };
});
