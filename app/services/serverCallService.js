define(['app'], function(app) {

	app.factory('serverCallService', ["$http", "$location", "authenticatedUserService",
	 function($http, $location, authenticatedUserService) {
		return {
			makePost : function(url, data, successCallback, errorCallback) {
				var headers = {};
				var user = authenticatedUserService.getUser();
				
				if(authenticatedUserService.isAuthenticated()) {
					headers.Authentication = authenticatedUserService.getToken();
					headers.Username = user.username;
				}

				$http({
					method: 'POST',
					url: url,
					data: data,
					headers: headers
				}).
				success(function(data) {
					successCallback(data);
				}).
				error(function(data, status, headers, config) {
					errorCallback(data, status);  
				});
			},
			
	        makeGet : function(url, params, successCallback, errorCallback) {
	        	var headers = {};
				var user = authenticatedUserService.getUser();

				if(authenticatedUserService.isAuthenticated()) {
					headers.Authentication = authenticatedUserService.getToken();
					headers.Username = user.username;
				}

	        	$http({
					method: 'GET',
					url: url,
					params: params,
					headers: headers
				}).
				success(function(data) {
					successCallback(data);
				}).
				error(function(data, status, headers, config) {
					errorCallback(data, status);  
				});
	        }
	    };
	}]);
});