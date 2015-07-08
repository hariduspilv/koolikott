define([], function()
{
    return {
        defaultRoutePath: '/',
        routes: {
            '/': {
                templateUrl: 'app/views/home/home.html',
                controller: 'homeController',
                dependencies: [
                    'views/home/home'
                ]
            },
            '/search/result': {
                templateUrl: 'app/views/search/result/searchResult.html',
                controller: 'searchResultController',
                dependencies: [
                    'views/search/result/searchResult'
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
                templateUrl: 'app/views/static/help/help.html',
                controller: 'helpController',
                dependencies: [
                    'views/static/help/help'
                ]
            },
            '/about': {
                templateUrl: 'app/views/static/about/about.html',
                controller: 'aboutController',
                dependencies: [
                    'views/static/about/about'
                ]
            }
        }
    };
});
