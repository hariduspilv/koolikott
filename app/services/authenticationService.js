define(['app'], function(app) {
	var instance;
	
	app.factory('authenticationService',['$location', '$rootScope', 'serverCallService', 'authenticatedUserService',
	       function($location, $rootScope, serverCallService, authenticatedUserService) {

        function loginSuccess(authenticatedUser) {
            if (isEmpty(authenticatedUser)) {
                log('No data returned by logging in');
            } else {
                authenticatedUserService.setAuthenticatedUser(authenticatedUser);
                
                if (authenticatedUser.firstLogin) {
                	$location.url('/' + authenticatedUser.user.username);
                }
            }
        };
        
        function loginFail(data, status) {
            log('Logging in failed.');
        };

        function logoutSuccess(data) {
            //ignore
        };

        function logoutFail(data, status) {
            //ignore
        };
        
        return {

            logout : function() {              
                serverCallService.makePost("rest/logout", {}, logoutSuccess, logoutFail);
                
                $rootScope.authenticatedUser = null;

                localStorage.removeItem("authenticatedUser");
            },

            loginWithIdCard : function() {
            	serverCallService.makeGet("rest/login/idCard", {}, loginSuccess, loginFail);
            }
	    };
	}]);
});