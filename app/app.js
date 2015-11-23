define(['app.routes', 'services/dependencyResolver'], function(config, dependencyResolver)
{
    "use strict";

    var app = angular.module('app', [
      'ngRoute',
      'ngMaterial',
      'ngMdIcons',
      'pascalprecht.translate',
      'youtube-embed',
      'angularScreenfull',
      'duScroll'
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
