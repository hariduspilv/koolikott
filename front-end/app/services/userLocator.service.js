'use strict';

angular.module('koolikottApp')
    .factory('userLocatorService',
        [
            '$location', '$rootScope', '$timeout', 'serverCallService', 'authenticatedUserService', '$mdDialog', 'toastService',
            function($location, $rootScope, $timeout, serverCallService, authenticatedUserService, $mdDialog, toastService ) {

                function getUser(){
                    return authenticatedUserService.getUser()
                }

                function saveUserLocation() {
                    debugger
                    serverCallService.makePost('rest/user/saveLocation', {location : $location.url()})
                }

                function getUserLocation() {
                    return serverCallService.makeGet('rest/user/getLocation').then((response) => {
                        if (response.data) return response.data
                    });
                }

                return {
                    getUser: function() {
                        getUser()
                    },

                    getUserLocation: function() {
                        getUserLocation()
                    },

                    saveUserLocation: function() {
                        saveUserLocation()
                    }
                }
            }
        ]);
