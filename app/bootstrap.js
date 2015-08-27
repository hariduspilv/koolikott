require.config({
    baseUrl: 'app',
    paths: {
        'dop': '../assets/js/dop.min',
        'modernizr': '../assets/js/modernizr.min',
        'authenticatedUserService': 'services/authenticatedUserService',
        'serverCallService': 'services/serverCallService',
        'authenticationService': 'services/authenticationService',
        'searchService': 'services/searchService',
        'dopHeader': 'directives/header/header',
        'dopFooter': 'directives/footer/footer',
        'translateUrlLoader': '../assets/js/angular-translate-loader-url.min',
        'translationService': 'services/translationService',
        'dopLoginBar': 'directives/login-bar/login-bar'
    },
    shim: {
        'app': {
            deps: ['dop', 'modernizr', 'translateUrlLoader']
        },
        'translateUrlLoader': {
            deps: ['dop']
        }
    }
});

require(['app', 'translationService', 'authenticatedUserService', 'serverCallService', 'authenticationService', 'searchService', 'dopHeader', 'dopFooter', 'dopLoginBar'],
 function(app, translationService, authenticatedUserService, serverCallService, authenticationService, searchService, dopHeader) {
    angular.bootstrap(document, ['app']);
});
