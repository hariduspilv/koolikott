define(['app.routes', 'services/dependencyResolver'], function(config, dependencyResolver)
{
    "use strict";

    var app = angular.module('app', [
      'ngRoute',
      'ngMaterial',
      'ngMdIcons',
      'pascalprecht.translate',
      'youtube-embed',
      'angularScreenfull'
    ]);

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
        '$mdThemingProvider',

        function($routeProvider, $locationProvider, $controllerProvider, $compileProvider, $filterProvider, $provide, $translateProvider, $sceProvider, $mdThemingProvider)
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
            configureTheme($mdThemingProvider)
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

    function configureTheme($mdThemingProvider) {
        var customBlueMap = $mdThemingProvider.extendPalette('light-blue', {
          'contrastDefaultColor': 'light',
          'contrastDarkColors': ['50'],
          '50': 'ffffff'
        });

        $mdThemingProvider.definePalette('customBlue', customBlueMap);
        $mdThemingProvider.theme('default')
        .primaryPalette('customBlue', {
          'default': '500',
          'hue-1': '50'
        })
        .accentPalette('purple',  {
          'default': '500'
        });

        $mdThemingProvider.theme('input', 'default').primaryPalette('grey');
    }

    app.run(function($rootScope) {
    	class TaxonUtils {
    		constructor() {
    			this.EDUCATIONAL_CONTEXT = '.EducationalContext';
    			this.DOMAIN = '.Domain';
    			this.SUBJECT = '.Subject';
    	    }

    		getEducationalContext(taxon) {
    			this.getTaxon(taxon, EDUCATIONAL_CONTEXT);
    		}
    		
    		getDomain(taxon) {
    			this.getTaxon(taxon, DOMAIN);
    		}

    		getSubject(taxon) {
    			this.getTaxon(taxon, SUBJECT);
    		}
    		
    		getTaxon(taxon, level) {
    			if (!taxon) {
    				return;
    			}

    			if (taxon.level === EDUCATIONAL_CONTEXT) {
    				return taxon.level === level ? taxon : null;
    			}

    			if (taxon.level === DOMAIN) {
    				return taxon.level === level ? taxon : this.getTaxon(taxon.educationalContext, level);
    			}

    			if (taxon.level === SUBJECT) {
    				return taxon.level === level ? taxon : this.getTaxon(taxon.domain, level);
    			}
    		}
    	}

    	$rootScope.taxonUtils = new TaxonUtils();
	});

   return app;
});
