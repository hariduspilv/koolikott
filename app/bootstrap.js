require.config({
    baseUrl: 'app',
    paths: {
        'dop': '../assets/js/dop.min',
        'modernizr': '../assets/js/modernizr.min',
        'dopHeader': 'directives/header/header'
    },
    shim: {
        'app': {
            deps: ['dop', 'modernizr']
        }
    }
});

require(['app', 'dopHeader'], function(app, dopHeader) {
    angular.bootstrap(document, ['app']);
});
