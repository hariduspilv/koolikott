"use strict";

define(['routes','services/dependencyResolver'], function(config, dependencyResolver)
{
    var app = angular.module('app', ['ngMaterial', 'ngRoute', 'pascalprecht.translate', 'mouse.utils', 'youtube-embed', 'ngResource', 'ngSanitize', 'ui.select', 'angularScreenfull', 'frapontillo.bootstrap-switch', 'ngMdIcons']);

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
    
    app.run(function($rootScope) {
    	class TaxonUtils {

    		getEducationalContext(taxon) {
    			if (!taxon) {
    				return;
    			}
    			
    			if (taxon.level === '.EducationalContext') {
    				return taxon;
    			}
    			
    			if (taxon.level === '.Domain') {
    				return this.getEducationalContext(taxon.educationalContext);
    			}
    			
    			if (taxon.level === '.Subject') {
    				return this.getEducationalContext(taxon.domain);
    			}
    		}
    		
    		getSubject(taxon) {
    			if (taxon && taxon.level === '.Subject') {
    				return taxon;
    			}
    		}
    	}
    	
    	$rootScope.taxonUtils = new TaxonUtils();
	});
    
   return app;
});