'use strict';

angular.module('koolikottApp')
.controller('tourModalController', [
    '$mdDialog', 'authenticatedUserService', '$rootScope',
    function ($mdDialog, authenticatedUserService, $rootScope) {
            var vm = this;
            vm.user = authenticatedUserService.getUser();

            vm.cancel = () => {
                $rootScope.$broadcast('tour:start:cancelled');
                $mdDialog.hide();
            }

            vm.showTour = () => {
                $rootScope.$broadcast('tour:start');
                $mdDialog.hide();
            }
        }
    ]);
