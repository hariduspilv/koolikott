define([
    'app',
    'services/translationService',
    'services/alertService'
], function(app) {
    app.directive('dopAlert', ['translationService', '$rootScope', 'alertService', function(translationService, $rootScope, alertService) {
        return {
            scope: true,
            templateUrl: 'directives/alert/alert.html',
            controller: function($scope, $timeout) {
                $scope.$watch(function() {
                        return alertService.getAlert();
                    },
                    function(newValue) {
                        if (newValue.message) {
                            $scope.alert = newValue;
                            alertService.clearMessage();

                            $timeout(function() {
                                $scope.alert = null;
                            }, 5000);
                        }
                    }, true
                );
            }
        };
    }]);
});