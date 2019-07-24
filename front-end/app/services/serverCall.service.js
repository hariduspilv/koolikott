'use strict';
{
class controller extends Controller {

    makeCall(url, method, params, includeAuthentication, successCallback, errorCallback, finallyCallback, transformRequest, cache) {
        let headers = {};

        if (includeAuthentication) this.setAuthorization(headers);

        let config = {
            method: method,
            url: url,
            headers: headers,
            cache: !!cache
        };

        if (method === 'POST' || method === 'PUT') config.data = params;
        else config.params = params;

        if (transformRequest) config.transformRequest = transformRequest;
        if (!successCallback)
            return this.$http(config).catch(response => {
                switch (response.status) {
                    case 419:
                        if (window.location.hostname === 'spring.oxygen.netgroupdigital.com'){
                            this.authenticatedUserService.removeAuthenticatedUser()
                        }
                        break
                    case 400:
                    case 404:
                        window.location.replace('/404')
                    case 401:
                        this.authenticatedUserService.removeAuthenticatedUser()
                    case 403:
                        this.$location.url('/')
                }
                throw new Error(`HTTP request failed — ${method.toUpperCase()} ${url} ${status}`)
            })

        this.$http(config)
            .then((response)=> {
                if (method === "HEAD") successCallback(response.headers);
                else successCallback(response.data);
            }, (response)=> {
                if (response.status === 419) {
                    this.authenticatedUserService.removeAuthenticatedUser();
                    this.makeCall(url, method, params, false, successCallback, errorCallback, finallyCallback, transformRequest);
                } else if (response.status === 401 || response.status === 403) {
                    this.$location.url('/');
                } else if (errorCallback) {
                    errorCallback(response.data, response.status);
                }
            })
            .then(finallyCallback);
    }

    setAuthorization(headers) {
        if (this.authenticatedUserService.isAuthenticated()) {
            const user = this.authenticatedUserService.getUser();
            headers.Authentication = this.authenticatedUserService.getToken();
            headers.Username = user.username;
        }
    }

    makePost(url, data, successCallback, errorCallback, finallyCallback) {
        return this.makeCall(url, 'POST', data, true, successCallback, errorCallback, finallyCallback);
    }

    makeGet(url, params, successCallback, errorCallback, finallyCallback, transformRequest, cache) {
        return this.makeCall(url, 'GET', params, true, successCallback, errorCallback, finallyCallback, transformRequest, cache);
    }

    makePut(url, data, successCallback, errorCallback, finallyCallback) {
        return this.makeCall(url, 'PUT', data, true, successCallback, errorCallback, finallyCallback);
    }

    makeDelete(url, data, successCallback, errorCallback, finallyCallback) {
        return this.makeCall(url, 'DELETE', data, true, successCallback, errorCallback, finallyCallback);
    }

    makeHead(url, data, successCallback, errorCallback, finallyCallback) {
        return this.makeCall(url, 'HEAD', data, true, successCallback, errorCallback, finallyCallback);
    }

    makeJsonp(url, params, successCallback, errorCallback, finallyCallback) {
        return this.makeCall(url, 'JSONP', params, false, successCallback, errorCallback, finallyCallback);
    }

    upload(url, data, successCallback, errorCallback, finallyCallback) {
        var headers = {};
        this.setAuthorization(headers);

        return this.Upload.upload({
            url: url,
            data: data,
            headers: headers
        }).then(successCallback, errorCallback, finallyCallback);
    }
}


controller.$inject = [
    '$http',
    '$location',
    'authenticatedUserService',
    'Upload',
]
factory('serverCallService', controller)
}
