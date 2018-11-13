'use strict'

angular.module('koolikottApp')
.controller('devLoginController',
[
    'serverCallService', '$rootScope', '$route', '$location', 'authenticationService',
    function(serverCallService, $rootScope, $route, $location, authenticationService) {
        const idCode = $route.current.params.idCode;
        serverCallService.makeGet(`rest/dev/login/${idCode}`)
            .then(({data}) => {
                    this.$rootScope.afterAuthRedirectURL = "/portfolios";
                    authenticationService.loginSuccess(data);
                }, () => {
                    authenticationService.loginFail()
                    $location.url("/");
                }
            )
        ;
    }
]);
