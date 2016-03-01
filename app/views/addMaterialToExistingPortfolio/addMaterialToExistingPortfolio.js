define([
    'app',
    'services/serverCallService',
    'services/translationService',
    'services/metadataService',
    'services/authenticatedUserService'
], function (app) {
    return ['$scope', '$mdDialog', 'serverCallService', 'translationService', 'metadataService', '$rootScope', 'authenticatedUserService',
        function ($scope, $mdDialog, serverCallService, translationService, metadataService, $rootScope, authenticatedUserService) {
            $scope.step = {};
            $scope.step.currentStep = 0;

            $scope.cancel = function () {
                $mdDialog.hide();
            };

            $scope.step.previousStep = function () {
                $scope.step.currentStep -= 1;
            };
        }];
});
