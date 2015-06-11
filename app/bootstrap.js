require.config({
    baseUrl: 'app',
    paths: {
        'dop': '../assets/js/dop.min',
        'modernizr': '../assets/js/modernizr.min',
        'dopHeader': 'directives/header/header',
        'translateUrlLoader': '../assets/js/angular-translate-loader-url.min'
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

require(['app', 'dopHeader'], function(app, dopHeader) {
    angular.bootstrap(document, ['app']);
});
