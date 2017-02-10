'use strict';

angular.module('koolikottApp')
.component('dopTour', {
    bindings: {},
    templateUrl: 'directives/tour/tour.html',
    controller: dopTourController
});

dopTourController.$inject = ['$rootScope', '$scope', 'authenticatedUserService', 'tourConfig', '$window', '$timeout', 'tourService', '$mdDialog'];

function dopTourController ($rootScope, $scope, authenticatedUserService, tourConfig, $window, $timeout, tourService, $mdDialog) {
    let vm = this;

    vm.currentStep = -1; // disable tour on load
    vm.isOpenedByUser = false;

    vm.$onInit = () => {
        if ($window.innerWidth >= BREAK_SM && vm.isAuthenticated() && !$rootScope.isEditPortfolioPage) {
            startGeneralTour();
        }
    };

    function startGeneralTour() {
        tourService.getUserTourData()
            .then((data) => {
                vm.userTourData = data;

                if (!vm.userTourData.generalTour) {
                    openFirstTourItem();
                }
            });

        // TODO: Remove for live
        openFirstTourItem();
    }

    function openFirstTourItem () {
        $mdDialog.show({
            templateUrl: 'directives/tour/modal/tour.modal.html',
            controller: 'tourModalController',
            controllerAs: '$ctrl'
        });
    }

    vm.isAuthenticated = () => authenticatedUserService.isAuthenticated();

    $scope.$on('tour:start', () => vm.tourStart());
    $scope.$on('tour:start:editPage', () => vm.tourStart(0, true, true));
    $scope.$on('tour:start:editPage:firstTime', () => vm.tourStart(0, true, true, true));
    $scope.$on('tour:start:cancelled', () => vm.tourStartCancelled());
    $scope.$on('tour:close', () => vm.tourComplete());
    $scope.$on('tour:close:editPage', () => vm.tourComplete(true));
    $scope.$on('tour:close:pageSwitch', () => vm.tourComplete(false, true));

    vm.tourStart = (startStep = 0, isOpenedByUser = true, isEditPage = false, isFirstTime = false) => {
        if (isFirstTime && vm.userTourData.editTour) return;

        vm.isEditPageTour = isEditPage;
        vm.currentStep = startStep;
        vm.isOpenedByUser = isOpenedByUser;
    };

    vm.tourStartCancelled = () => {
        vm.isCancelledTour = true;
        vm.tourStart(0, false);

        $timeout(() => {
            vm.tourComplete();
        }, 5000);
    };

    vm.tourComplete = (isEditPage = false, isPageSwitch = false) => {
        vm.currentStep = -1;
        vm.isOpenedByUser = false;
        vm.isCancelledTour = false;

        if (vm.isAuthenticated() && !isPageSwitch) {
            if (isEditPage) {
                tourService.setEditTourSeen(vm.userTourData).then((data) => {
                    vm.userTourData = data;
                });
            } else {
                tourService.setGeneralTourSeen(vm.userTourData).then((data) => {
                    vm.userTourData = data;
                });
            }
        }
    }
}
