define(['app'], function(app) {

	app.factory('serverCallService', ["$http", "$location", function($http, $location) {
		function makeCall(method, url, params, successCallback, errorCallback) {
			
			$http({
				method: method,
				url: url,
				data: params
			}).
			success(function(data) {
				successCallback(data);
			}).
			error(function(data, status, headers, config) {
				errorCallback(data, status);  
			});
		}

		return {
			makePost : function(url, params, successCallback, errorCallback) {
				makeCall('POST', url, params, successCallback, errorCallback);
			},
			
	        makeGet : function(url, params, successCallback, errorCallback) {
	        	makeCall('GET', url, params, successCallback, errorCallback);
	        }
	    };
	}]);
});