'use strict';

angular.module('koolikottApp')
.component('dopAlert', {
    bindings: {},
    controller: dopAlertController
});

dopAlertController.$inject = ['$scope', '$timeout', 'translationService', 'alertService', 'toastService'];

function dopAlertController ($scope, $timeout, translationService, alertService, toastService) {
    let vm = this;

    $scope.$watch(function() {
        return alertService.getAlert();
    }, function(newValue) {
        if (newValue.message) {
            toastService.show(newValue.message);
            alertService.clearMessage();

            $timeout(function() {
                vm.alert = null;
            }, 5000);
        }
    }, true);
}
