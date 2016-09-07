define([
    'angularAMD',

    'app.routes',
    'utils/taxonUtils',
    'utils/taxonParser',
    'moment',

    'angular-translate',
    'angular-translate-loader-url',
    'angular-material',
    'angular-route',
    'angular-click-outside',
    'angular-scroll',
    'jsog',
    'utils/commons',
    'ng-file-upload',

    'angular-bootstrap',

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
    'DOPconstants',
], function (angularAMD, config, taxonUtils, taxonParser, moment) {
    'use strict';

    var app = angular.module('app', [
        'ngRoute',
        'ngMaterial',
        'pascalprecht.translate',
        'angular-click-outside',
        'duScroll',
        'ngFileUpload',
        'ui.bootstrap',
        'DOPconstants'
    ]);

    app.config(function ($routeProvider, $locationProvider, $controllerProvider, $compileProvider, $filterProvider, $provide, $translateProvider, $sceProvider, $mdThemingProvider, $httpProvider, $mdDateLocaleProvider, $anchorScrollProvider) {
            $compileProvider.debugInfoEnabled(false);
            app.controller = $controllerProvider.register;
            app.directive = $compileProvider.directive;
            app.filter = $filterProvider.register;
            app.factory = $provide.factory;
            app.service = $provide.service;
            $httpProvider.useApplyAsync(true);

            if (config.routes !== undefined) {
                angular.forEach(config.routes, function (route, path) {
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

            if (!$httpProvider.defaults.headers.get) {
                $httpProvider.defaults.headers.get = {};
            }

            $httpProvider.defaults.transformResponse.splice(0, 0, parseJSONResponse);
            $httpProvider.defaults.transformRequest = serializeRequest;
            //disable IE ajax request caching
            $httpProvider.defaults.headers.get['If-Modified-Since'] = 'Mon, 26 Jul 1997 05:00:00 GMT';
            // extra
            $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
            $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';

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

        $mdDateLocaleProvider.formatDate = function (date) {
            return date ? moment(date).format('DD.MM.YYYY') : '';
        };

        $mdDateLocaleProvider.parseDate = function (dateString) {
            var m = moment(dateString, ['DD.MM.YYYY', 'MM.YYYY', 'YYYY', 'DD-MM-YYYY', 'DD/MM/YYYY'], true);
            return m.isValid() ? m.toDate() : new Date(NaN)
        };
    }

    app.run(function ($rootScope, metadataService, APP_VERSION) {
        $rootScope.APP_VERSION = APP_VERSION;
        $rootScope.hasAppInitated = false;
        $rootScope.taxonParser = taxonParser;
        metadataService.loadEducationalContexts(taxonParser.setTaxons);
    });

    app.run(function ($rootScope, $location, authenticatedUserService) {
        $rootScope.$on('$routeChangeSuccess', function () {
            var path = $location.path();
            var editModeAllowed = ["/portfolio/edit", "/search/result", "/material"];

            $rootScope.isViewPortforlioPage = path === '/portfolio';
            $rootScope.isEditPortfolioPage = path === '/portfolio/edit';

            if (path == "/portfolio/edit") {
                $rootScope.isEditPortfolioMode = true;
                $rootScope.selectedMaterials = [];
                $rootScope.selectedSingleMaterial = null;
            } else if (editModeAllowed.indexOf(path) != -1) {
                if (path != "/material") {
                    $rootScope.selectedSingleMaterial = null;
                    $rootScope.selectedMaterials = [];
                }
            } else if (authenticatedUserService.isAuthenticated() && path != "/material") {
                $rootScope.isEditPortfolioMode = false;
                $rootScope.selectedSingleMaterial = null;
                $rootScope.selectedMaterials = [];
            } else {
                $rootScope.isEditPortfolioMode = false;
                $rootScope.savedPortfolio = null;
                $rootScope.selectedMaterials = null;
            }

            $rootScope.hasAppInitated = true;
        });
    });

    app.run(function ($rootScope, $location) {
        var history = [];

        $rootScope.$on('$routeChangeSuccess', function () {
            history.push($location.url());
        });

        $rootScope.back = function () {
            var prevUrl = history.length > 1 ? history.splice(-2)[0] : '/';
            $location.url(prevUrl);
        };
    });

    app.run(function ($rootScope, $location, $timeout, $document) {
        if (!window.history || !history.replaceState) {
            return;
        }

        $rootScope.$on('duScrollspy:becameActive', function ($event, $element, $target) {
            //Automatically update location
            var hash = $element.prop('hash');
            if (hash) {
                $timeout(function () {
                    // replace hash
                    var newUrl = $location.url().split('#', 1);
                    history.replaceState('', '', newUrl[0] + hash);
                });
            }
        });
    });

    app.run(['$rootScope', 'authenticatedUserService', function ($rootScope, authenticatedUserService) {
        $rootScope.$on('$locationChangeStart', function (event, next, current) {
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

    app.run(['$rootScope', 'authenticatedUserService', function ($rootScope, authenticatedUserService) {
        $rootScope.taxonUtils = taxonUtils;

        $rootScope.$watch(function () {
            return authenticatedUserService.isAuthenticated();
        }, function (isAuthenticated) {
            $rootScope.showMainFabButton = isAuthenticated;
        }, true);
    }]);

    app.run(function ($templateCache, $sce, $templateRequest) {
        var addMaterialDialog = $sce.getTrustedResourceUrl('views/addMaterialDialog/addMaterialDialog.html');
        $templateRequest(addMaterialDialog).then(function (template) {
            $templateCache.put('addMaterialDialog.html', template);
        }, function () {
            console.log("Failed to load addMaterialDialog.html template")
        });

        var detailedSearch = $sce.getTrustedResourceUrl('directives/detailedSearch/detailedSearch.html');
        $templateRequest(detailedSearch).then(function (template) {
            $templateCache.put('detailedSearch.html', template);
        }, function () {
            console.log("Failed to load detailedSearch.html template")
        });
    });

    return angularAMD.bootstrap(app);
});
