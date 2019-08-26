'use strict'

angular.module('koolikottApp')
.controller('loginRedirectController',
[
    'authenticationService', '$route',
    function(authenticationService, $route) {
        authenticationService.authenticateUsingOAuth($route.current.params);
        console.log($route.current.params)
    }
]);
