'use strict';

angular.module('koolikottApp')
    .factory('userLocatorService',
        [
            '$location', '$rootScope', '$timeout', 'serverCallService', 'authenticatedUserService', '$mdDialog', 'toastService', '$interval',
            function($location, $rootScope, $timeout, serverCallService, authenticatedUserService, $mdDialog, toastService, $interval) {

                let updateTimer;

                function saveUserLocation() {
                    if (authenticatedUserService.isAuthenticated())
                        serverCallService.makePost('rest/user/saveLocation', {location : $location.url()})
                }

                return {

                    getUserLocation: function() {
                        return serverCallService.makeGet('rest/user/getLocation')
                    },

                    saveUserLocation: function() {
                        saveUserLocation()
                    },

                    startTimer: function() {
                        updateTimer = $interval(saveUserLocation, 60000) //1 min
                    },

                    stopTimer: function() {
                        $interval.cancel(updateTimer)
                    }
                }
            }
        ]);
