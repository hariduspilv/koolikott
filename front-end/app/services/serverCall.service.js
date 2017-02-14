'use strict';

angular.module('koolikottApp')
.factory('serverCallService',
[
    '$http', '$location', 'authenticatedUserService', 'Upload',
    function($http, $location, authenticatedUserService, Upload) {
        var instance;

        function makeCall(url, method, params, includeAuthentication, successCallback, errorCallback, finallyCallback, transformRequest, cache) {
            var headers = {};

            if (includeAuthentication) {
                setAuthorization(headers);
            }

            var config = {
                method: method,
                url: url,
                headers: headers,
                cache: !!cache
            };

            if (method === 'POST' || method === 'PUT') {
                config.data = params;
            } else {
                config.params = params;
            }

            if (transformRequest) {
                config.transformRequest = transformRequest;
            }

            if (!successCallback) {
                return $http(config);
            }

            $http(config)
            .then(function (response) {
                if(method == "HEAD"){
                    successCallback(response.headers);
                }else{
                    successCallback(response.data);
                }
            }, function (response) {
                if (response.status == '419') {
                    authenticatedUserService.removeAuthenticatedUser();
                    makeCall(url, method, params, false, successCallback, errorCallback, finallyCallback, transformRequest);
                } else if (response.status == '401') {
                    $location.url('/');
                } else if (errorCallback) {
                    errorCallback(response.data, response.status);
                }
            })
            .then(finallyCallback);
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
                return makeCall(url, 'POST', data, true, successCallback, errorCallback, finallyCallback);
            },

            makeGet: function(url, params, successCallback, errorCallback, finallyCallback, transformRequest, cache) {
                return makeCall(url, 'GET', params, true, successCallback, errorCallback, finallyCallback, transformRequest, cache);
            },

            makePut: function(url, data, successCallback, errorCallback, finallyCallback) {
                return makeCall(url, 'PUT', data, true, successCallback, errorCallback, finallyCallback);
            },
            makeDelete: function(url, data, successCallback, errorCallback, finallyCallback) {
                return makeCall(url, 'DELETE', data, true, successCallback, errorCallback, finallyCallback);
            },
            makeHead: function(url, data, successCallback, errorCallback, finallyCallback) {
                return makeCall(url, 'HEAD', data, true, successCallback, errorCallback, finallyCallback);
            },

            makeJsonp: function(url, params, successCallback, errorCallback, finallyCallback) {
                return makeCall(url, 'JSONP', params, false, successCallback, errorCallback, finallyCallback);
            },

            upload: function(url, data, successCallback, errorCallback, finallyCallback) {
                var headers = {};
                setAuthorization(headers);

                Upload.upload({
                    url: url,
                    data: data,
                    headers: headers
                }).then(successCallback, errorCallback, finallyCallback);
            }
        };

        return instance;
    }
]);
