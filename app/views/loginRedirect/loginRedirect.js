define(['app'], function(app)
{
    app.controller('loginRedirectController', ['authenticationService', '$route',
       function(authenticationService, $route) {
    	authenticationService.authenticateUsingOAuth($route.current.params.token);
    }]);
});