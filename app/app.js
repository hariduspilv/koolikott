define(['routes','services/dependencyResolver'], function(config, dependencyResolver)
{
    var app = angular.module('app', ['ngRoute']);

    app.config(
    [
        '$routeProvider',
        '$locationProvider',
        '$controllerProvider',
        '$compileProvider',
        '$filterProvider',
        '$provide',

        function($routeProvider, $locationProvider, $controllerProvider, $compileProvider, $filterProvider, $provide)
        {
	        app.controller = $controllerProvider.register;
	        app.directive  = $compileProvider.directive;
	        app.filter     = $filterProvider.register;
	        app.factory    = $provide.factory;
	        app.service    = $provide.service;

            if(config.routes !== undefined)
            {
                angular.forEach(config.routes, function(route, path)
                {
                    $routeProvider.when(path, {templateUrl:route.templateUrl, controller:route.controller, resolve:dependencyResolver(route.dependencies)});
                });
            }

            if(config.defaultRoutePath !== undefined)
            {
                $routeProvider.otherwise({redirectTo:config.defaultRoutePath});
            }
        }
    ]);

   return app;
});