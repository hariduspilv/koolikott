define(['app.routes', 'services/dependencyResolver', 'utils/taxonUtils'], function(config, dependencyResolver, taxonUtils)
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
      'angular-click-outside'
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
            $httpProvider.defaults.transformRequest = serializeRequest;
        }
    ]);

    function serializeRequest(data, headersGetter) {
        if (data && headersGetter()['content-type'].contains('application/json')) {
        	return JSOG.stringify(data);
        }

        return data;
    }

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

// http://stackoverflow.com/questions/30123735/how-to-create-multiple-theme-in-material-angular
    function configureTheme($mdThemingProvider) {


        var customBlueMap = $mdThemingProvider.extendPalette('blue', {
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

            $rootScope.isViewPortforlioPage = path === '/portfolio';

            if (!$rootScope.isEditPortfolioMode) {
            	$rootScope.isEditPortfolioMode = path === '/portfolio/edit';
            } else {
            	$rootScope.isEditPortfolioMode = false;
            	$rootScope.savedPortfolio = null;
            }
        });
    });

    app.run(function($rootScope, authenticatedUserService) {
    	$rootScope.taxonUtils = taxonUtils;

    	$rootScope.$watch(function () {
            return authenticatedUserService.isAuthenticated();
        }, function (isAuthenticated) {
        	$rootScope.showMainFabButton = isAuthenticated;
        }, true);
	});

   return app;
});
