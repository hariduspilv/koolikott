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
    'textAngular',

    /* app wide modules */
    'directives/header/header',
    'directives/detailedSearch/detailedSearch',
    'directives/mainFabButton/mainFabButton',
    'directives/sidebar/sidebar',
    'directives/sidenav/sidenav',
    'directives/pageStructure/columnLayout/columnLayout',

    'services/authenticatedUserService',
    'DOPconstants'
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
        'DOPconstants',
        'textAngular'
    ]);

    var provideProvider = null;

    app.config(function ($routeProvider, $locationProvider, $controllerProvider, $compileProvider, $filterProvider, $provide, $translateProvider, $sceProvider, $mdThemingProvider, $httpProvider, $mdDateLocaleProvider, $anchorScrollProvider) {
            //Add rangy manually to the window from the rangy-core, as textAngular needs it and requireJS can't add it
            require(['rangy-core'], function (rangy) {
                window.rangy = rangy;
            });

            $compileProvider.debugInfoEnabled(false);
            app.controller = $controllerProvider.register;
            app.directive = $compileProvider.directive;
            app.filter = $filterProvider.register;
            app.factory = $provide.factory;
            app.service = $provide.service;
            $httpProvider.useApplyAsync(true);
            provideProvider = $provide;

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

            $provide.decorator('taOptions', ['$delegate', function (taOptions) {
                taOptions.forceTextAngularSanitize = true;

                taOptions.toolbar = [
                    ['bold', 'italics', 'ul', 'ol', 'insertLink', 'pre', 'quote']
                ];

                taOptions.classes = {
                    focussed: 'focussed',
                    toolbar: 'btn-toolbar',
                    toolbarGroup: 'btn-group',
                    toolbarButton: 'btn btn-textangular',
                    toolbarButtonActive: 'active',
                    disabled: 'disabled',
                    textEditor: 'form-control',
                    htmlEditor: 'form-control'
                };
                return taOptions; // whatever you return will be the taOptions
            }]);

            $httpProvider.defaults.transformResponse.splice(0, 0, parseJSONResponse);
            $httpProvider.defaults.transformRequest = serializeRequest;

            var isIE = (navigator.userAgent.match(/Trident/) || navigator.userAgent.match(/MSIE/));

            if (isIE) {
                // disable IE ajax request caching
                $httpProvider.defaults.headers.get['If-Modified-Since'] = 'Mon, 26 Jul 1997 05:00:00 GMT';
                // extra
                $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
                $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
            }

            $locationProvider.html5Mode(true);
            $anchorScrollProvider.disableAutoScrolling();
        }
    );

    /**
     * orderByTranslated Filter
     * Sort ng-options or ng-repeat by translated values
     * @example
     *   ng-repeat="scheme in data.schemes | orderByTranslated:'storage__':'collectionName'"
     * @param  {Array|Object} array or hash
     * @param  {String} i18nKeyPrefix
     * @param  {String} objKey (needed if hash)
     * @return {Array}
     */
    app.filter('orderByTranslated', ['$translate', '$filter', function ($translate, $filter) {
        return function (array, objKey, i18nKeyPrefix) {
            var result = [];
            var translated = [];
            if (!i18nKeyPrefix) {
                i18nKeyPrefix = '';
            }

            angular.forEach(array, function (value) {
                var i18nKeySuffix = objKey ? value[objKey] : value;
                translated.push({
                    key: value,
                    label: $translate.instant(i18nKeyPrefix + i18nKeySuffix)
                });
            });
            angular.forEach($filter('orderBy')(translated, 'label'), function (sortedObject) {
                result.push(sortedObject.key);
            });
            return result;
        };
    }]);

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

    function configureTextAngular($provide, $translate) {

        $provide.decorator('taTools', ['$delegate', function (taTools) {
            taTools.bold.buttontext = '<md-icon>format_bold</md-icon>';
            taTools.bold.iconclass = '';
            taTools.bold.tooltiptext = $translate.instant('TEXTANGULAR_BOLD');
            taTools.italics.buttontext = '<md-icon>format_italic</md-icon>';
            taTools.italics.iconclass = '';
            taTools.italics.tooltiptext = $translate.instant('TEXTANGULAR_ITALICS');
            taTools.insertLink.buttontext = '<md-icon>insert_link</md-icon>';
            taTools.insertLink.iconclass = '';
            taTools.insertLink.tooltiptext = $translate.instant('TEXTANGULAR_INSERT_LINK');
            taTools.ul.buttontext = '<md-icon>format_list_bulleted</md-icon>';
            taTools.ul.iconclass = '';
            taTools.ul.tooltiptext = $translate.instant('TEXTANGULAR_UL');
            taTools.ol.buttontext = '<md-icon>format_list_numbered</md-icon>';
            taTools.ol.iconclass = '';
            taTools.ol.tooltiptext = $translate.instant('TEXTANGULAR_OL');
            taTools.pre.buttontext = '<md-icon>crop_16_9</md-icon>';
            taTools.pre.iconclass = '';
            taTools.pre.tooltiptext = $translate.instant('TEXTANGULAR_PRE');
            taTools.quote.buttontext = '<md-icon>format_quote</md-icon>';
            taTools.quote.iconclass = '';
            taTools.quote.tooltiptext = $translate.instant('TEXTANGULAR_QUOTE');
            return taTools;
        }]);
    }

    function setDefaultShareParams($rootScope, $location) {
        $rootScope.shareTitle = 'e-Koolikott';
        $rootScope.shareUrl = $location.absUrl();
        $rootScope.shareDescription = 'e-Koolikott is a single web environment comprising digital learning material arranged by keywords on the basis of the curriculum. The portal allows finding educational materials located in different digital tool collections. The primary purpose of e-Koolikott is to allow accessing digital learning materials from a single point - the user no longer needs to search for materials in different portals.'
        $rootScope.shareImage = '';
    }

    app.run(function ($rootScope, metadataService, APP_VERSION) {
        $rootScope.APP_VERSION = APP_VERSION;
        $rootScope.hasAppInitated = false;
        $rootScope.taxonParser = taxonParser;
        metadataService.loadEducationalContexts(setTaxons);

        function setTaxons(taxon) {
            $rootScope.taxon = taxon;
            taxonParser.setTaxons(taxon);
        }
    });

    function isViewMyProfilePage($location, user) {
        return user && $location.path().startsWith('/' + user.username);
        // return user && $location.path().indexOf('/' + user.username) != -1
    }

    function isDashboardPage(path) {
        return path.indexOf("/dashboard") !== -1;
    }

    function isViewMaterialPage(path) {
        return path === '/material';
    }

    function isViewPortfolioPage(path) {
        return path === '/portfolio';
    }

    function isEditPortfolioPage(path) {
        return path === '/portfolio/edit';
    }

    function isSearchPage($location) {
        return $location.url().indexOf("/search") != -1
    }

    function isHomePage(path) {
        return path === '/';
    }

    app.run(function ($rootScope, $location, authenticatedUserService) {
        $rootScope.$on('$routeChangeSuccess', function () {
            const editModeAllowed = ["/portfolio/edit", "/search/result", "/material"];

            var path = $location.path();
            var user = authenticatedUserService.getUser();
            var isViewHomePage = isHomePage(path);
            var isViewMyProfile = isViewMyProfilePage($location, user);

            $rootScope.isViewPortforlioPage = isViewPortfolioPage(path);
            $rootScope.isEditPortfolioPage = isEditPortfolioPage(path);
            $rootScope.isViewMaterialPage = isViewMaterialPage(path);
            $rootScope.isViewAdminPanelPage = isDashboardPage(path);
            $rootScope.isViewMaterialOrPortfolioPage = !!($rootScope.isViewMaterialPage || $rootScope.isViewPortforlioPage);

            if (isViewMyProfile && $location.path() === '/' + authenticatedUserService.getUser().username) {
                $location.path('/' + user.username + '/portfolios');
            }
            if (!$rootScope.isViewPortforlioPage || !$rootScope.isViewMaterialPage) setDefaultShareParams($rootScope, $location);

            $rootScope.isUserTabOpen = !!($rootScope.isViewAdminPanelPage || isViewMyProfile || $rootScope.isViewMaterialPage);
            $rootScope.isTaxonomyOpen = !!($rootScope.isViewMaterialPage || isViewHomePage || isSearchPage($location));

            if ($rootScope.isEditPortfolioPage) {
                $rootScope.isEditPortfolioMode = true;
                $rootScope.selectedMaterials = [];
                $rootScope.selectedSingleMaterial = null;
            } else if (editModeAllowed.indexOf(path) != -1 && !$rootScope.isViewMaterialPage) {
                $rootScope.selectedSingleMaterial = null;
                $rootScope.selectedMaterials = [];
            } else if (authenticatedUserService.isAuthenticated() && !$rootScope.isViewMaterialPage) {
                $rootScope.isEditPortfolioMode = false;
                $rootScope.selectedSingleMaterial = null;
                $rootScope.selectedMaterials = [];
            } else {
                $rootScope.isEditPortfolioMode = false;
                $rootScope.savedPortfolio = null;
                $rootScope.selectedMaterials = null;
            }

            if (window.innerWidth > 1280 && ($rootScope.isViewPortforlioPage || $rootScope.isEditPortfolioPage)) {
                $rootScope.sideNavOpen = true;
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

    app.run(function ($rootScope, $location, $timeout) {
        if (!window.history || !history.replaceState) {
            return;
        }

        $rootScope.$on('duScrollspy:becameActive', function ($event, $element) {
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
        $rootScope.$on('$locationChangeStart', function (event, next) {
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

    app.run(function ($translate) {
        configureTextAngular(provideProvider, $translate);
    });

    return angularAMD.bootstrap(app);
})
;
