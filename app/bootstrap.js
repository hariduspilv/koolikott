require.config({
    baseUrl: 'app',
    paths: {
        'dop': '../assets/js/dop.min',
        'modernizr': '../assets/js/modernizr.min',
        'loginService': 'services/loginService',
        'serverCallService': 'services/serverCallService',
        'searchService': 'services/searchService',
        'dopHeader': 'directives/header/header',
        'dopFooter': 'directives/footer/footer',
        'translateUrlLoader': '../assets/js/angular-translate-loader-url.min',
        'translationService': 'services/translationService'
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

require(['app', 'translationService', 'loginService', 'serverCallService', 'searchService', 'dopHeader', 'dopFooter'],
 function(app, translationService, loginService, serverCallService, searchService, dopHeader) {
    angular.bootstrap(document, ['app']);
});
