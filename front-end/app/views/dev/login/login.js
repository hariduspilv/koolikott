'use strict'

angular.module('koolikottApp')
.controller('devLoginController',
[
    'serverCallService', '$rootScope', '$route', '$location', 'authenticationService',
    function(serverCallService, $rootScope, $route, $location, authenticationService) {
        const idCode = $route.current.params.idCode;
        serverCallService.makeGet('rest/dev/login/' + idCode)
            .then( function(data) {
                    $rootScope.showLocationDialog = false;
                    $rootScope.afterAuthRedirectURL = '/';
                    authenticationService.loginSuccess(data.data);
                }, function() {
                    authenticationService.loginFail()
                    $location.url('/');
                }
            )
        ;
    }
]);
