'use strict';

angular.module('koolikottApp')
.component('dopTour', {
    restrict: 'E',
    templateUrl: 'directives/tour/tour.html',
    bindings: {},
    controller: DopTourController
});

DopTourController.$inject = ['$rootScope', '$scope', 'authenticatedUserService', 'tourConfig'];

function DopTourController ($rootScope, $scope, authenticatedUserService, tourConfig) {
    let vm = this;

    vm.currentStep = 0;
    vm.isOpenedByUser = false;

    vm.$onInit = () => {
        vm.isEditPageTour = $rootScope.isEditPortfolioPage ? true : false;

        vm.hasPermission = () => authenticatedUserService.getUser() && !authenticatedUserService.isRestricted();

        tourConfig.scrollSpeed = false;
    }

    $scope.$on('tour:open', () => vm.tourStart());
    $scope.$on('tour:close', () => vm.tourComplete());

    vm.tourStart = (startStep = 0) => {
        vm.currentStep = startStep;
        vm.isOpenedByUser = true;
    }

    vm.tourComplete = () => {
        vm.currentStep = -1;
        vm.isOpenedByUser = false;
    }
}
