define(['routes','services/dependencyResolver'], function(config, dependencyResolver)
{
    var app = angular.module('app', ['ngMaterial', 'ngRoute', 'pascalprecht.translate', 'mouse.utils', 'youtube-embed', 'ngResource', 'ngSanitize', 'ui.select', 'angularScreenfull', 'frapontillo.bootstrap-switch']);

    app.config(
    [
        '$routeProvider',
        '$locationProvider',
        '$controllerProvider',
        '$compileProvider',
        '$filterProvider',
        '$provide',
        '$translateProvider',
        '$sceProvider',

        function($routeProvider, $locationProvider, $controllerProvider, $compileProvider, $filterProvider, $provide, $translateProvider, $sceProvider)
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

            $sceProvider.enabled(false);
        }
    ]);
    
    function configureTranslationService($translateProvider) {    	  
    	$translateProvider.useUrlLoader('rest/translation');
        var language = localStorage.getItem("userPreferredLanguage");
        if (!language) {
            language = 'est';
        }
        
        $translateProvider.preferredLanguage(language);
        $translateProvider.useSanitizeValueStrategy('escaped');
    }

    function concatDependencies(dependencies) {
        return getServicesAndUtilsDependencies().concat(dependencies);
    }

    function getServicesAndUtilsDependencies() { 
        return [
            'utils/commons'
        ];
    }
    
   return app;
});