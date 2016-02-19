define([
    'app',
    'services/translationService',
    'services/authenticatedUserService'
], function (app) {
    app.directive('dopReportImproper', ['translationService', '$mdDialog', '$translate', 'authenticatedUserService', function (translationService, $mdDialog, $translate, authenticatedUserService) {
        return {
            scope: {
                learningObject: '='
            },
            templateUrl: 'directives/report/improper/improper.html',
            controller: function ($scope, serverCallService) {
                $scope.isReported = false;
                $scope.isReportedByUser = false;

                $scope.$watch('learningObject', function (newLearningObject, oldLearningObject) {
                    if (newLearningObject) {
                    	getHasReported();
                    }
                }, false);

                function getHasReported() {
                    var url;

                    if ($scope.learningObject && $scope.learningObject.id) {
                        url = "rest/impropers?learningObject=" + $scope.learningObject.id;
                        serverCallService.makeGet(url, {}, requestSuccessful, requestFailed);
                    }
                }

                function requestSuccessful(improper) {
                	var isImproper = improper.length > 0 ? true : false;
                    if ($scope.isAdmin) {
                        $scope.isReported = isImproper;
                    } else {
                        $scope.isReportedByUser = isImproper;
                    }
                }

                function requestFailed() {
                    console.log("Failed checking if already reported the resource")
                }

                $scope.setNotImproper = function () {
                    if($scope.isAdmin && $scope.learningObject) {
                        url = "rest/impropers?learningObject=" + $scope.learningObject.id;
                        serverCallService.makeDelete(url, {}, setNotImproperSuccessful, setNotImproperFailed);
                    }
                };

                function setNotImproperSuccessful() {
                    $scope.isReported = false;
                }

                function setNotImproperFailed() {
                    console.log("Setting not improper failed.")
                }

                $scope.showConfirmationDialog = function () {
                    var confirm = $mdDialog.confirm()
                        .title($translate.instant('REPORT_IMPROPER_TITLE'))
                        .content($translate.instant('REPORT_IMPROPER_CONTENT'))
                        .ok($translate.instant('BUTTON_NOTIFY'))
                        .cancel($translate.instant('BUTTON_CANCEL'));

                    $mdDialog.show(confirm).then(function () {
                        var entity = {
                            learningObject: $scope.learningObject
                        };

                        serverCallService.makePut("rest/impropers", entity, setImproperSuccessful, setImproperFailed);
                    });
                };

                $scope.isAdmin = authenticatedUserService.isAdmin();

                function setImproperSuccessful(improper) {
                	if (!improper) {
                		setImproperFailed();
                	} else {
                		$scope.isReportedByUser = true;
                	}
                }

                function setImproperFailed() {
                    $scope.isReportedByUser = false;
                }
            }
        };
    }]);
});