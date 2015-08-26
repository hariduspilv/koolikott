define(['app'], function(app) {

	var instance;
	
	app.factory('loginService',['$location', '$rootScope', 'serverCallService', 
	       function($location, $rootScope, serverCallService) {

        function getAuthenticatedUser() {
            var user = JSON.parse(localStorage.getItem("authenticatedUser"));
            if (user) {
                return user;
            }

            user = $rootScope.authenticatedUser;
            if (user) {
                return user;
            }
            
            return null;
        }

        function loginSuccess(authenticatedUser) {
            if (isEmpty(authenticatedUser)) {
                log('No data returned by logging in');
            } else {
                instance.setAuthenticatedUser(authenticatedUser);
                
                if (authenticatedUser.firstLogin) {
                	$location.url('/' + authenticatedUser.user.username);
                }
            }
        };
        
        function loginFail(material, status) {
            log('Logging in failed.');
        };
        
        instance = {
			setAuthenticatedUser : function(authenticatedUser) {
				$rootScope.authenticatedUser = authenticatedUser;
                localStorage.setItem("authenticatedUser", JSON.stringify(authenticatedUser));
            },

            isAuthenticated : function() {
                if(getAuthenticatedUser()) {
                    return true;
                }

                return false;
            },

            getUser : function() {
                var authenticatedUser = getAuthenticatedUser();
                if (authenticatedUser) {
                    return authenticatedUser.user;
                }

                return null;
            },

            logout : function() {
                $rootScope.authenticatedUser = null;
                localStorage.removeItem("authenticatedUser");
            },

            loginWithIdCard : function() {
            	serverCallService.makeGet("rest/login/idCard", {}, loginSuccess, loginFail);
            }
	    };
		
		return instance;
	}]);
});