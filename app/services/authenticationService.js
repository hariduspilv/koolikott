define(['app'], function(app) {
    var instance;
    var isAuthenticationInProgress;
    
    app.factory('authenticationService',['$location', '$rootScope', 'serverCallService', 'authenticatedUserService',
    function($location, $rootScope, serverCallService, authenticatedUserService) {

        function loginSuccess(authenticatedUser) {
            if (isEmpty(authenticatedUser)) {
                log('No data returned by logging in');
                $rootScope.errorMessageShow = true;
                $rootScope.errorMessageNoLoginData = true;
                enableLogin();
            } else {
                authenticatedUserService.setAuthenticatedUser(authenticatedUser);

                if (authenticatedUser.firstLogin) {
                    $location.url('/' + authenticatedUser.user.username);
                }
            }
        };

        function loginFail(data, status) {
            log('Logging in failed.');
            $rootScope.errorMessageShow = true;
            $rootScope.errorMessageLoginFail = true;
            enableLogin();
        };

        function logoutSuccess(data) {
            enableLogin();
        };

        function logoutFail(data, status) {
            //ignore
        };

        function disableLogin() {
            isAuthenticationInProgress = true;
        }

        function enableLogin() {
            isAuthenticationInProgress = false;
        }

        return {

            logout : function() {              
                serverCallService.makePost("rest/logout", {}, logoutSuccess, logoutFail);

                $rootScope.authenticatedUser = null;

                localStorage.removeItem("authenticatedUser");
            },

            loginWithIdCard : function() {
                if (isAuthenticationInProgress) {
                    return;
                }
            
                disableLogin();
                serverCallService.makeGet("rest/login/idCard", {}, loginSuccess, loginFail);
            }, 

            loginWithTaat : function() {
                localStorage.removeItem(LOGIN_ORIGIN);
                localStorage.setItem(LOGIN_ORIGIN, $location.url());
                window.location = "/rest/login/taat";
            }
        };
    }]);
});
