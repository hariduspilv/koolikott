'use strict';

angular.module('koolikottApp')
    .factory('userLocatorService',
        [
            '$location', '$rootScope', '$timeout', 'serverCallService', 'authenticatedUserService', '$mdDialog', 'toastService', '$interval',
            function($location, $rootScope, $timeout, serverCallService, authenticatedUserService, $mdDialog, toastService, $interval ) {

                let updateTimer

                return {

                    getUserLocation: function() {
                        return serverCallService.makeGet('rest/user/getLocation').then((response) => {
                            debugger
                            if (response.data) return response.data
                        });
                    },

                    saveUserLocation: function() {
                        serverCallService.makePost('rest/user/saveLocation', {location : $location.url()})
                    },

                    startTimer: function() {
                        updateTimer = $interval(function (){
                        serverCallService.makePost('rest/user/saveLocation', {location : $location.url()})
                        }, 10000)
                    },

                    stopTimer: function() {
                        $interval.cancel(updateTimer)
                    }
                }
            }
        ]);
