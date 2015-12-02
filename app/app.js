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
      'duScroll',
      'infinite-scroll',
      'ngFileUpload',
      'ngclipboard'
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
        '$httpProvider',

        function($routeProvider, $locationProvider, $controllerProvider, $compileProvider, $filterProvider, $provide, $translateProvider, $sceProvider, $mdThemingProvider, $httpProvider)
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

            configureTranslationService($translateProvider);
            configureTheme($mdThemingProvider)
            $sceProvider.enabled(false);

            $httpProvider.defaults.transformResponse.splice(0, 0, parseJSONResponse);
        }
    ]);

     function parseJSONResponse(data, headersGetter) {
        if (data && (headersGetter()['content-type'] === 'application/json')) {
            return JSOG.parse(data);
        }

        return data;
    }

    function configureTranslationService($translateProvider) {
    	$translateProvider.useUrlLoader('rest/translation');
        var language = localStorage.getItem("userPreferredLanguage");
        if (!language) {
            language = 'est';
        }

        $translateProvider.preferredLanguage(language);
        $translateProvider.useSanitizeValueStrategy('escaped');
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
    
    app.run(function($rootScope, $location) {        
        $rootScope.$on('$routeChangeSuccess', function() {
            var path = $location.path();
            
            $rootScope.isViewPortfolio = path === '/portfolio';
            $rootScope.isViewMaterial = path === '/material';
        });
    });

    app.run(function($rootScope, authenticatedUserService) {
    	class TaxonUtils {
    		constructor() {
    			this.EDUCATIONAL_CONTEXT = '.EducationalContext';
    			this.DOMAIN = '.Domain';
    			this.SUBJECT = '.Subject';
    			this.TOPIC = '.Topic';
    			this.SUBTOPIC = '.Subtopic';
    			this.SPECIALIZATION = '.Specialization';
    			this.MODULE = '.Module';
    	    }

    		getEducationalContext(taxon) {
    			return this.getTaxon(taxon, this.EDUCATIONAL_CONTEXT);
    		}

    		getDomain(taxon) {
    			return this.getTaxon(taxon, this.DOMAIN);
    		}

    		getSubject(taxon) {
    			return this.getTaxon(taxon, this.SUBJECT);
    		}

    		getTopic(taxon) {
    			return this.getTaxon(taxon, this.TOPIC);
    		}
    		
    		getSubtopic(taxon) {
    			return this.getTaxon(taxon, this.SUBTOPIC);
    		}
    		
    		getSpecialization(taxon) {
    			return this.getTaxon(taxon, this.SPECIALIZATION);
    		}
    		
    		getModule(taxon) {
    			return this.getTaxon(taxon, this.MODULE);
    		}

    		getTaxon(taxon, level) {
    			if (!taxon) {
    				return;
    			}

    			if (taxon.level === this.EDUCATIONAL_CONTEXT) {
    				return taxon.level === level ? taxon : null;
    			}

    			if (taxon.level === this.DOMAIN) {
    				return taxon.level === level ? taxon : this.getTaxon(taxon.educationalContext, level);
    			}

    			if (taxon.level === this.SUBJECT) {
    				return taxon.level === level ? taxon : this.getTaxon(taxon.domain, level);
    			}

    			if (taxon.level === this.TOPIC) {
    				if (taxon.level === level) {
    					return taxon;
    				}
    				
    				var parent = taxon.subject; 
    				if (!parent) {
    					parent = taxon.domain;
    					
    					if (!parent) {
        					parent = taxon.module;
        				}
    				}
    				
    				return  this.getTaxon(parent, level);
    			}
    			
    			if (taxon.level === this.SUBTOPIC) {
    				return taxon.level === level ? taxon : this.getTaxon(taxon.topic, level);
    			}
    			
    			if (taxon.level === this.SPECIALIZATION) {
    				return taxon.level === level ? taxon : this.getTaxon(taxon.domain, level);
    			}
    			
    			if (taxon.level === this.MODULE) {
    				return taxon.level === level ? taxon : this.getTaxon(taxon.specialization, level);
    			}
    		}
    	}

    	$rootScope.taxonUtils = new TaxonUtils();
    	
    	$rootScope.$watch(function () {
            return authenticatedUserService.isAuthenticated();
        }, function (isAuthenticated) {
        	$rootScope.showMainFabButton = isAuthenticated;
        },true
    );
	});

   return app;
});
