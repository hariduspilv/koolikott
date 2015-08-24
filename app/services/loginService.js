define(['app'], function(app) {

	app.factory('loginService',['$location', '$rootScope', function($location, $rootScope) {

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

            logout : function() {
                $rootScope.authenticatedUser = null;
                localStorage.removeItem("authenticatedUser");
            }
	    };
	}]);
});