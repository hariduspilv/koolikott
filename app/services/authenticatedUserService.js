define(['app'], function(app) {
	var instance;
	
	app.factory('authenticatedUserService',['$location', '$rootScope', 
	       function($location, $rootScope) {

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

            getToken : function() {
                var authenticatedUser = getAuthenticatedUser();
                if (authenticatedUser) {
                    return authenticatedUser.token;
                }

                return null;
            },
	    };
		
		return instance;
	}]);
});