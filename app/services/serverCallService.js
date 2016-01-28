define(['angularAMD'], function(angularAMD) {
    var instance;

    angularAMD.factory('serverCallService', ['$http', '$location', 'authenticatedUserService', "Upload",
        function($http, $location, authenticatedUserService, Upload) {

	    	function makeCall(url, method, params, includeAuthentication, successCallback, errorCallback, finallyCallback, transformRequest) {
	    		headers = {};
	    		
	    		if (includeAuthentication) {
	    			setAuthorization(headers);
	    		}
	
	            var config = {
	                method: method,
	                url: url,
	                headers: headers
	            }
	            
	            if (method === 'POST') {
	            	config.data = params;
	            } else {
	            	config.params = params;
	            }
	            
	            if (transformRequest) {
	                config.transformRequest = transformRequest
	            }
	            
	            $http(config).
	            success(function(data) {
	                successCallback(data);
	            }).
	            error(function(data, status, headers, config) {
	                if (status == '419') {
	                    authenticatedUserService.removeAuthenticatedUser();
	                    instance.makePost(url, data, successCallback, errorCallback);
	                } else {
	                    errorCallback(data, status);
	                }
	            }).finally(finallyCallback);
	        }
	    	
	    	function setAuthorization(headers) {
	            if (authenticatedUserService.isAuthenticated()) {
	            	var user = authenticatedUserService.getUser();
	                headers.Authentication = authenticatedUserService.getToken();
	                headers.Username = user.username;
	            }
	    	}
	    	
            instance = {
                makePost: function(url, data, successCallback, errorCallback, finallyCallback) {
                	makeCall(url, 'POST', data, true, successCallback, errorCallback, finallyCallback);
                },

                makeGet: function(url, params, successCallback, errorCallback, finallyCallback) {
                	makeCall(url, 'GET', params, true, successCallback, errorCallback, finallyCallback);
                },

                makeJsonp: function(url, params, successCallback, errorCallback, finallyCallback) {
                	makeCall(url, 'JSONP', params, false, successCallback, errorCallback, finallyCallback);
                },
                
                upload: function(url, data, successCallback, errorCallback, finallyCallback) {
                	var headers = {};
                	setAuthorization(headers);
                	
                	Upload.upload({
                        url: url,
                        data: data,
                        headers: headers
                    }).then(successCallback, errorCallback).finally(finallyCallback);
                }
            };

            
            var transformaRequest = function (postData, headersGetter) {
                var formData = new FormData();
                angular.forEach(postData, function (value, key) {
                    formData.append(key, value);
                });

                return formData;
            }
            
            return instance;
        }
    ]);
});
