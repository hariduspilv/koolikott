define([
    'app',
    'services/translationService',
    'services/authenticatedUserService',
    'services/serverCallService'
], function(app) {
    app.directive('dopReportBrokenLink', ['translationService', '$mdDialog', '$translate', 'authenticatedUserService', 'serverCallService', 'toastService',
        function(translationService, $mdDialog, $translate, authenticatedUserService, serverCallService, toastService) {
        return {
            scope: false,
            templateUrl: 'directives/report/brokenLink/brokenLink.html',
            controller: function($scope, $rootScope) {

                $scope.isAdmin = authenticatedUserService.isAdmin();

                $scope.$watch('material', function(newValue, oldValue) {
                    if (newValue && newValue.type == ".Material") {
                        if ($scope.isAdmin) {
                            serverCallService.makeGet("rest/material/isBroken?materialId=" + newValue.id, {}, isBrokenSuccess, queryFailed);
                        } else {
                            serverCallService.makeGet("rest/material/hasSetBroken?materialId=" + newValue.id, {}, hasSetBrokenSuccess, queryFailed);
                        }
                    }
                }, false);

                $scope.showConfirmationDialog = function() {
                    var confirm = $mdDialog.confirm()
                        .title($translate.instant('REPORT_BROKEN_LINK_TITLE'))
                        .content($translate.instant('REPORT_BROKEN_LINK_CONTENT'))
                        .ok($translate.instant('BUTTON_REPORT'))
                        .cancel($translate.instant('BUTTON_CANCEL'));

                    $mdDialog.show(confirm).then(function() {
                        url = "rest/material/setBroken";
                        serverCallService.makePost(url, $scope.material, setBrokenSuccessful, queryFailed);

                    });
                };


                function isBrokenSuccess(data) {
                    $scope.isBroken = data;
                    $scope.isBrokenReportedByUser = data;
                }

                function hasSetBrokenSuccess(data) {
                    $scope.isBrokenReportedByUser = data;
                }

                function setBrokenSuccessful() {
                    $scope.isBrokenReportedByUser = true;
                    toastService.show('TOAST_NOTIFICATION_SENT_TO_ADMIN');
                }

                function queryFailed() {
                    log("Request failed");
                }
            }
        };
    }]);
});
