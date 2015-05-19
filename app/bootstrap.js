require.config({
    baseUrl: 'app',
    paths: {
    	'dop': '../assets/js/dop.min',
        'modernizr': '../assets/js/modernizr.min',
    },
	shim: {
		'app': {
			deps: ['dop', 'modernizr']
		}
	}
});

require(['app'], function(app) {
        angular.bootstrap(document, ['app']);
});