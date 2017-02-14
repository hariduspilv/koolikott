'use strict';

angular.module('koolikottApp')
.controller('tourModalController', [
    '$mdDialog', 'authenticatedUserService', '$rootScope',
    function ($mdDialog, authenticatedUserService, $rootScope) {
            let vm = this;

            angular.element('body').addClass('tour-modal-is-showing');

            vm.user = authenticatedUserService.getUser();

            vm.cancel = () => {
                $rootScope.$broadcast('tour:start:cancelled');
                angular.element('body').removeClass('tour-modal-is-showing');
                $mdDialog.hide();
            }

            vm.showTour = () => {
                $rootScope.$broadcast('tour:start');
                angular.element('body').removeClass('tour-modal-is-showing');
                $mdDialog.hide();
            }
        }
    ]);
