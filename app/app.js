define(['routes','services/dependencyResolver'], function(config, dependencyResolver)
{
    var app = angular.module('app', ['ngRoute', 'pascalprecht.translate', 'mouse.utils']);

    app.config(
    [
        '$routeProvider',
        '$locationProvider',
        '$controllerProvider',
        '$compileProvider',
        '$filterProvider',
        '$provide',
        '$translateProvider',

        function($routeProvider, $locationProvider, $controllerProvider, $compileProvider, $filterProvider, $provide, $translateProvider)
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
                    $routeProvider.when(path, {templateUrl:route.templateUrl, controller:route.controller, resolve:dependencyResolver(concatDependencies(route.dependencies))});
                });
            }

            if(config.defaultRoutePath !== undefined)
            {
                $routeProvider.otherwise({redirectTo:config.defaultRoutePath});
            }

            configureTranslationService($translateProvider);
        }
    ]);
    
    function configureTranslationService($translateProvider) {    	  
    	$translateProvider.useUrlLoader('/rest/languages');
        $translateProvider.preferredLanguage('est');
        $translateProvider.useSanitizeValueStrategy('escaped');
    }

    function concatDependencies(dependencies) {
        return getServicesAndUtilsDependencies().concat(dependencies);
    }

    function getServicesAndUtilsDependencies() { 
        return [
            'services/serverCallService',
            'utils/commons'
        ];
    }

   return app;
});