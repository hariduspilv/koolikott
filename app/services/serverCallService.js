define(['app'], function(app) {

	app.factory('serverCallService', ["$http", "$location", function($http, $location) {
		return {
			makePost : function(url, data, successCallback, errorCallback) {
				$http({
					method: 'POST',
					url: url,
					data: data
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