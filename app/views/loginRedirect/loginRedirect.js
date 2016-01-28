define([
    'app',
    'services/authenticationService'
], function(app) {
    return ['authenticationService', '$route', function(authenticationService, $route) {
        authenticationService.authenticateUsingOAuth($route.current.params.token);
    }];
});