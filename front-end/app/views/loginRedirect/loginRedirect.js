'use strict'

angular.module('koolikottApp')
.controller('loginRedirectController',
[
    'authenticationService', '$route',
    function(authenticationService, $route) {
        const params = $route.current.params;
        authenticationService.authenticateUsingOAuth(params.token, params.agreement, params.existingUser);
    }
]);
