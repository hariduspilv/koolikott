require.config({
    baseUrl: 'app',
    paths: {
        'dop': '../assets/js/dop.min',
        'Modernizr': '../assets/js/modernizr.min',
    },
    shim: {
        'app': {
            deps: ['dop', 'Modernizr']
        }
    }
});

require
(
    [
        'app'
    ],
    function(app)
    {
        angular.bootstrap(document, ['app']);
    }
);