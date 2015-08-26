define(['app'], function(app) {

	app.factory('serverCallService', ["$http", "$location", "loginService", function($http, $location, loginService) {
		return {
			makePost : function(url, data, successCallback, errorCallback) {
				var headers = {};
				if(loginService.isAuthenticated) {
					headers.token = loginService.getToken();
					console.log(headers.token);
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
	        	$http({
					method: 'GET',
					url: url,
					params: params
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