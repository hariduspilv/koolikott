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
            '/ekoolikott': {
                templateUrl: 'app/views/ekoolikott/ekoolikott.html',
                controller: 'ekoolikottController',
                dependencies: [
                    'views/ekoolikott/ekoolikott'
                ]
            }
        }
    };
});
