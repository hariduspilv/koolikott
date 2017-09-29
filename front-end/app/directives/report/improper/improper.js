'use strict'

angular.module('koolikottApp')
.directive('dopReportImproper',
[
    'translationService', '$mdDialog', '$translate', '$rootScope', 'toastService', 'serverCallService',
    function (translationService, $mdDialog, $translate, $rootScope, toastService, serverCallService) {
        return {
            scope: {
                learningObject: '='
            },
            templateUrl: 'directives/report/improper/improper.html',
            controller: ['$scope', function ($scope) {
                $scope.showConfirmationDialog = function () {
                    var confirm = $mdDialog.confirm()
                    .title($translate.instant('REPORT_IMPROPER_TITLE'))
                    .content($translate.instant('REPORT_IMPROPER_CONTENT'))
                    .ok($translate.instant('BUTTON_NOTIFY'))
                    .cancel($translate.instant('BUTTON_CANCEL'));

                    $mdDialog.show(confirm).then(function () {
                        var entity = {
                            learningObject: $scope.learningObject,
                            reason: $scope.learningObject.type.slice(1)
                        };

                        serverCallService.makePut("rest/impropers", entity).then(response => {
                            if (response.status == 200)
                                toastService.show('TOAST_NOTIFICATION_SENT_TO_ADMIN')
                        });
                    });
                };
            }]
        };
    }
]);
