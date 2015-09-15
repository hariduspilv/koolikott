define(['app'], function(app) {
    var instance;
    var isAuthenticationInProgress;
    var isOAuthAuthentication = false;
    
    app.factory('authenticationService',['$location', '$rootScope', 'serverCallService', 'authenticatedUserService', 'alertService',
    function($location, $rootScope, serverCallService, authenticatedUserService, alertService) {

        function loginSuccess(authenticatedUser) {
            if (isEmpty(authenticatedUser)) {
            	loginFail();
            } else {
                authenticatedUserService.setAuthenticatedUser(authenticatedUser);

                if (authenticatedUser.firstLogin) {
                    $location.url('/' + authenticatedUser.user.username);
                } else if (isOAuthAuthentication) {
                	var url = localStorage.getItem(LOGIN_ORIGIN);
                	$location.url(url);
                }
            }
            
            enableLogin();
            localStorage.removeItem(LOGIN_ORIGIN);
            isOAuthAuthentication = false;
        }

        function loginFail() {
            log('Logging in failed.');
            alertService.setErrorAlert('ERROR_LOGIN_FAILED');
            enableLogin();
            
            if (isOAuthAuthentication) {
            	localStorage.removeItem(LOGIN_ORIGIN);
            	$location.url('/');
            }
            
            isOAuthAuthentication = false
        }

        function logoutSuccess(data) {
        	authenticatedUserService.removeAuthenticatedUser();
            enableLogin();
        }

        function logoutFail(data, status) {
            //ignore
        }

        function disableLogin() {
            isAuthenticationInProgress = true;
        }

        function enableLogin() {
            isAuthenticationInProgress = false;
        }

        return {

            logout : function() {              
                serverCallService.makePost("rest/logout", {}, logoutSuccess, logoutFail);
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
            },
            
            authenticateUsingOAuth : function(token) {
            	var params = {
                        'token': token
                };
            	
            	serverCallService.makeGet("rest/login/getAuthenticatedUser", params, loginSuccess, loginFail);
            	isOAuthAuthentication = true;
            }  
        };
    }]);
});
