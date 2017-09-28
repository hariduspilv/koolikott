'use strict'

angular.module('koolikottApp')
.directive('dopReportImproper',
[
    'translationService', '$mdDialog', '$translate', 'authenticatedUserService', '$rootScope', 'toastService', 'serverCallService',
    function (translationService, $mdDialog, $translate, authenticatedUserService, $rootScope, toastService, serverCallService) {
        return {
            scope: {
                learningObject: '='
            },
            templateUrl: 'directives/report/improper/improper.html',
            controller: ['$scope', function ($scope) {
                $scope.isReported = false;

                $scope.$watch('learningObject', function (newLearningObject) {
                    if (newLearningObject) {
                        getHasReported();
                    }
                }, false);

                function getHasReported() {
                    var url;

                    if ($scope.learningObject && $scope.learningObject.id) {
                        url = "rest/admin/improper?learningObject=" + $scope.learningObject.id;
                        serverCallService.makeGet(url, {}, requestSuccessful, requestFailed);
                    }
                }

                function requestSuccessful(improper) {
                    var isImproper = improper.length > 0;
                    if ($scope.isAdmin) {
                        $scope.isReported = isImproper;
                    } else {
                        $rootScope.isReportedByUser = isImproper;
                    }
                }

                function requestFailed() {
                    console.log("Failed checking if already reported the resource")
                }

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

                        serverCallService.makePut("rest/admin/improper", entity, setImproperSuccessful, setImproperFailed);
                    });
                };

                $scope.isAdmin = authenticatedUserService.isAdmin();

                function setImproperSuccessful(improper) {
                    if (!improper) {
                        setImproperFailed();
                    } else {
                        $rootScope.isReportedByUser = true;
                        toastService.show('TOAST_NOTIFICATION_SENT_TO_ADMIN');
                    }
                }

                function setImproperFailed() {
                    $rootScope.isReportedByUser = false;
                }
            }]
        };
    }
]);
