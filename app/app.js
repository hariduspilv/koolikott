define(['routes','services/dependencyResolver'], function(config, dependencyResolver)
{
    var app = angular.module('app', ['ngRoute', 'pascalprecht.translate', 'mouse.utils', 'youtube-embed', 'ngResource', 'ngSanitize', 'ui.select']);

    app.config(
    [
        '$routeProvider',
        '$locationProvider',
        '$controllerProvider',
        '$compileProvider',
        '$filterProvider',
        '$provide',
        '$translateProvider',
        '$httpProvider',

        function($routeProvider, $locationProvider, $controllerProvider, $compileProvider, $filterProvider, $provide, $translateProvider, $httpProvider, $sceDelegateProvider)
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

            $httpProvider.defaults.transformResponse.splice(0, 0, parseJSONResponse);
            $sceDelegateProvider.resourceUrlWhitelist([
                                                       // Allow same origin resource loads.
                                                       'self',
                                                       // Allow loading from our assets domain.  Notice the difference between * and **.
                                                       'https://172.33.45.51'
                                                       ]);
        }
        
        
        
        
    ]);

     function parseJSONResponse(data, headersGetter) {
        if (data && (headersGetter()['content-type'] === 'application/json')) {
            return JSOG.parse(data);
        } else {
            return data;
        }
    } 
    
    function configureTranslationService($translateProvider) {    	  
    	$translateProvider.useUrlLoader('rest/translation');
        $translateProvider.preferredLanguage('est');
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