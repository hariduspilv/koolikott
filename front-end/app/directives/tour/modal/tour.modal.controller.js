'use strict';

angular.module('koolikottApp')
.controller('tourModalController', [
    '$mdDialog', 'authenticatedUserService', '$rootScope',
    function ($mdDialog, authenticatedUserService, $rootScope) {
            let vm = this;

            $('body').addClass('tour-modal-is-showing');

            vm.user = authenticatedUserService.getUser();

            vm.cancel = () => {
                $rootScope.$broadcast('tour:start:cancelled');
                $('body').removeClass('tour-modal-is-showing');
                $mdDialog.hide();
            }

            vm.showTour = () => {
                $rootScope.$broadcast('tour:start');
                $('body').removeClass('tour-modal-is-showing');
                $mdDialog.hide();
            }
        }
    ]);
