'use strict'

angular.module('koolikottApp')
.directive('dopAlert',
[
    'translationService', 'alertService', 'toastService',
    function(translationService, alertService, toastService) {
        return {
            scope: true,
            controller: ['$scope', '$timeout', function($scope, $timeout) {
                $scope.$watch(function() {
                    return alertService.getAlert();
                },
                function(newValue) {
                    if (newValue.message) {
                        toastService.show(newValue.message);
                        alertService.clearMessage();

                        $timeout(function() {
                            $scope.alert = null;
                        }, 5000);
                    }
                }, true
            );
        }]
    };
}]);
