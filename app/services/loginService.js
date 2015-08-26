define(['app'], function(app) {

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
        };

        function logoutSuccess() {
            //ignore
        };
        
        function logoutFail() {
            //ignore
        };
		
		return {
			
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

            getToken : function() {
                var authenticatedUser = getAuthenticatedUser();
                if (authenticatedUser) {
                    return authenticatedUser.token;
                }

                return null;
            },

            logout : function() {
                var user = getAuthenticatedUser();
                if (user) {
                    var params = {
                    'token' : user.token
                    };

                    serverCallService.makePost("rest/logout", params, logoutSuccess, logoutFail);
                }
                $rootScope.authenticatedUser = null;
                localStorage.removeItem("authenticatedUser");   
            }
	    };
	}]);
});