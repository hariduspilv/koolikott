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
            }
        }
    };
});