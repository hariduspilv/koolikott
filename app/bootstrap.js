require.config({
    baseUrl: 'app',
    paths: {
		'dop': '../assets/js/dop.min',
    },
	shim: {
		'app': {
			deps: ['dop']
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