'use strict';

angular.module('koolikottApp').factory('authenticatedUserService', [
    '$location',
    function ($location) {

        var instance = {
            setAuthenticatedUser: function (authenticatedUser) {
                localStorage.setItem("authenticatedUser", JSON.stringify(authenticatedUser));
            },

            getAuthenticatedUser: function () {
                return JSON.parse(localStorage.getItem("authenticatedUser"));
            },

            removeAuthenticatedUser: function () {
                localStorage.removeItem("authenticatedUser");
            },

            isAuthenticated: function () {
                return !!instance.getAuthenticatedUser();
            },

            isAdmin: function () {
                var user = instance.getUser();
                return user && user.role === 'ADMIN';
            },

            isPublisher: function () {
                var user = instance.getUser();
                return user && isDefined(user.publisher);
            },

            isRestricted: function () {
                var user = instance.getUser();
                return user && user.role === 'RESTRICTED';
            },

            isModerator: function () {
                var user = instance.getUser();
                return user && user.role === 'MODERATOR';
            },

            getUser: function () {
                var authenticatedUser = instance.getAuthenticatedUser();
                if (authenticatedUser) {
                    return authenticatedUser.user;
                }

                return null;
            },

            getToken: function () {
                var authenticatedUser = instance.getAuthenticatedUser();
                if (authenticatedUser) {
                    return authenticatedUser.token;
                }

                return null;
            },
        };

        return instance;
    }
]);
