define([
    'angularAMD',

    'app.routes',
    'utils/taxonUtils',
    'utils/taxonParser',

    'angular-translate',
    'angular-translate-loader-url',
    'angular-material',
    'angular-route',
    'angular-click-outside',
    'angular-scroll',
    'jsog',
    'utils/commons',
    'ng-file-upload',

    /* app wide modules */
    'directives/header/header',
    'directives/editPortfolioModeHeader/editPortfolioModeHeader',
    'directives/detailedSearch/detailedSearch',
    'directives/mainFabButton/mainFabButton',
    'directives/sidebar/sidebar',

    /* TODO: we could save more request if layout system is built in another way */
    'directives/pageStructure/columnLayout/columnLayout',
    'directives/pageStructure/linearLayout/linearLayout',

    'services/authenticatedUserService',
], function(angularAMD, config, taxonUtils, taxonParser) {
    'use strict';

    var app = angular.module('app', [
        'ngRoute',
        'ngMaterial',
        'pascalprecht.translate',
        'angular-click-outside',
        'duScroll',
        'ngFileUpload'
    ]);

    app.config(function($routeProvider, $locationProvider, $controllerProvider, $compileProvider, $filterProvider, $provide, $translateProvider, $sceProvider, $mdThemingProvider, $httpProvider, $mdDateLocaleProvider, $anchorScrollProvider) {
            app.controller = $controllerProvider.register;
            app.directive = $compileProvider.directive;
            app.filter = $filterProvider.register;
            app.factory = $provide.factory;
            app.service = $provide.service;

            if (config.routes !== undefined) {
                angular.forEach(config.routes, function(route, path) {
                    $routeProvider.when(
                        path,
                        angularAMD.route({
                            templateUrl: route.templateUrl,
                            controllerUrl: route.controllerUrl,
                            reloadOnSearch: angular.isDefined(route.reloadOnSearch) ? route.reloadOnSearch : undefined
                        })
                    );
                });
            }

            if (config.defaultRoutePath !== undefined) {
                $routeProvider.otherwise({
                    redirectTo: config.defaultRoutePath
                });
            }

            configureTranslationService($translateProvider);
            configureTheme($mdThemingProvider);
            configureDateLocale($mdDateLocaleProvider);
            $sceProvider.enabled(false);

            $httpProvider.defaults.transformResponse.splice(0, 0, parseJSONResponse);
            $httpProvider.defaults.transformRequest = serializeRequest;

            $locationProvider.html5Mode(true);
            $anchorScrollProvider.disableAutoScrolling();
        }
    );

    function serializeRequest(data, headersGetter) {
        if (data && headersGetter()['content-type'].contains('application/json')) {
            data = clone(data);
            taxonParser.serialize(data);
            return JSOG.stringify(data);
        }

        return data;
    }

    function parseJSONResponse(data, headersGetter) {
        if (data && (headersGetter()['content-type'] === 'application/json')) {
            data = JSOG.parse(data);
            taxonParser.parse(data);
            return data;
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
            .accentPalette('purple', {
                'default': '500'
            });

        $mdThemingProvider.theme('input', 'default').primaryPalette('grey');
    }

    function configureDateLocale($mdDateLocaleProvider) {
        $mdDateLocaleProvider.firstDayOfWeek = 1;
    }

    app.run(function($rootScope, metadataService) {
        metadataService.loadEducationalContexts(taxonParser.setTaxons);
    });

    app.run(function($rootScope, $location, authenticatedUserService) {
        $rootScope.$on('$routeChangeSuccess', function() {
            var path = $location.path();
            var editModeAllowed = ["/portfolio/edit", "/search/result", "/material"];

            $rootScope.isViewPortforlioPage = path === '/portfolio';
            $rootScope.isEditPortfolioPage = path === '/portfolio/edit';

            if (path == "/portfolio/edit") {
            	$rootScope.isEditPortfolioMode = true;
            	$rootScope.selectedMaterials = [];
            	$rootScope.selectedSingleMaterial = null;
            } else if(editModeAllowed.indexOf(path) != -1) {
            	if(path != "/material") {
            		$rootScope.selectedSingleMaterial = null;
                    $rootScope.selectedMaterials = [];
            	}
            } else if(authenticatedUserService.isAuthenticated() && path != "/material") {
                $rootScope.isEditPortfolioMode = false;
                $rootScope.selectedSingleMaterial = null;
                $rootScope.selectedMaterials = [];
            } else {
                $rootScope.isEditPortfolioMode = false;
                $rootScope.savedPortfolio = null;
                $rootScope.selectedMaterials = null;
            }
        });
    });

    app.run(function($rootScope, $location) {
        var history = [];

        $rootScope.$on('$routeChangeSuccess', function() {
            history.push($location.url());
        });

        $rootScope.back = function() {
            var prevUrl = history.length > 1 ? history.splice(-2)[0] : '/';
            $location.url(prevUrl);
        };
    });

    app.run(function($rootScope, $location, $timeout, $document) {
        if(!window.history || !history.replaceState) {
            return;
        }

        $rootScope.$on('duScrollspy:becameActive', function($event, $element, $target) {
            //Automatically update location
            var hash = $element.prop('hash');
            if (hash) {
                $timeout(function() {
                    // replace hash
                    var newUrl = $location.url().split('#', 1);
                    history.replaceState('', '', newUrl[0] + hash);
                });
            }
        });
    });

    app.run(['$rootScope', 'authenticatedUserService', function($rootScope, authenticatedUserService) {
        $rootScope.$on('$locationChangeStart', function(event, next, current) {
            for (var i in config.routes) {
                if (next.indexOf(i) != -1) {
                    var permissions = config.routes[i].permissions;

                    if (permissions === undefined) continue;

                    if (!authenticatedUserService.getUser())
                        return event.preventDefault();

                    if (permissions && permissions.indexOf(authenticatedUserService.getUser().role) == -1) {
                        return event.preventDefault();
                    }
                }
            }
        });
    }]);

    app.run(['$rootScope', 'authenticatedUserService', function($rootScope, authenticatedUserService) {
        $rootScope.taxonUtils = taxonUtils;

        $rootScope.$watch(function() {
            return authenticatedUserService.isAuthenticated();
        }, function(isAuthenticated) {
            $rootScope.showMainFabButton = isAuthenticated;
        }, true);
    }]);

    return angularAMD.bootstrap(app);
});
