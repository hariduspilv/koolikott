define([], function()
{
    return {
        defaultRoutePath: '/',
        routes: {
            '/': {
                templateUrl: 'app/home/home.html',
                controller: 'homeController',
                dependencies: [
                    'home/home'
                ]
            },
            '/search/result': {
                templateUrl: 'app/search/result/searchResult.html',
                controller: 'searchResultController',
                dependencies: [
                    'search/result/searchResult'
                ]
            },
            '/material': {
                templateUrl: 'app/material/material.html',
                controller: 'materialController',
                dependencies: [
                    'material/material'
                ]
            }
        }
    };
});