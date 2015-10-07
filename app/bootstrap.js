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
        'screenfull': '../assets/js/screenfull',
        'angularScreenfull': '../assets/js/angular-screenfull.min',
        'translationService': 'services/translationService',
        'dopLoginBar': 'directives/login-bar/login-bar',
        'dopAlert': 'directives/alert/alert',
        'alertService': 'services/alertService'
    },
    shim: {
        'app': {
            deps: ['dop', 'modernizr', 'translateUrlLoader','screenfull', 'angularScreenfull']
        },
        'screenfull': {
            deps: ['dop']
        },
        'angularScreenfull': {
            deps: ['screenfull']
        },
        'translateUrlLoader': {
            deps: ['dop']
        }
    }
});

require(['app', 'translationService', 'authenticatedUserService', 'serverCallService', 'authenticationService', 'searchService', 'dopHeader', 'dopFooter', 'dopLoginBar', 'dopAlert', 'alertService', 'angularScreenfull'],
 function(app, translationService, authenticatedUserService, serverCallService, authenticationService, searchService, dopHeader, dopAlert, alertService, angularScreenfull) {
    angular.bootstrap(document, ['app']);
});
