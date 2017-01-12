'use strict';

angular.module('koolikottApp')
.component('dopTour', {
    restrict: 'E',
    templateUrl: 'directives/tour/tour.html',
    bindings: {},
    controller: dopTourController
});

dopTourController.$inject = ['$rootScope', '$scope', 'authenticatedUserService', 'tourConfig', '$mdDialog', '$window', '$timeout', 'tourService'];

function dopTourController ($rootScope, $scope, authenticatedUserService, tourConfig, $mdDialog, $window, $timeout, tourService) {
    let vm = this;

    vm.currentStep = -1; // disable tour on load
    vm.isOpenedByUser = false;

    vm.$onInit = () => {
        // TODO: show editPage tour if has not seen before

        // TODO: show modal only on first login
        if ($window.innerWidth >= BREAK_SM && vm.isAuthenticated() && !$rootScope.isEditPortfolioPage) {
            startGeneralTour();
        }
    };

    function startGeneralTour() {
        tourService.getUserTourData()
            .then((data) => {

                vm.userTourData = data;

                if (!data.generalTour) {
                    openModal();
                }
            });
    }

    function openModal () {
        $mdDialog.show({
            templateUrl: 'directives/tour/modal/tour.modal.html',
            controller: 'tourModalController',
            controllerAs: 'vm'
        });
    }

    vm.isAuthenticated = () => authenticatedUserService.isAuthenticated();

    $scope.$on('tour:start', () => vm.tourStart());
    $scope.$on('tour:start:editPage', () => vm.tourStart(0, true, true));
    $scope.$on('tour:start:cancelled', () => vm.tourStartCancelled());
    $scope.$on('tour:close', () => vm.tourComplete());

    vm.tourStart = (startStep = 0, isOpenedByUser = true, isEditPage = false) => {
        vm.isEditPageTour = isEditPage;
        vm.currentStep = startStep;
        vm.isOpenedByUser = isOpenedByUser;
    };

    vm.tourStartCancelled = () => {
        vm.isCancelledTour = true;
        vm.isEditPageTour = false;
        vm.isOpenedByUser = false;
        vm.currentStep = 0;

        $timeout(() => {
            vm.tourComplete();
        }, 3000);
    };

    vm.tourComplete = () => {
        vm.currentStep = -1;
        vm.isOpenedByUser = false;
        vm.isCancelledTour = false;

        if (vm.isAuthenticated()) {
            vm.userTourData = tourService.setGeneralTourSeen(vm.userTourData);
        }
    }
}
