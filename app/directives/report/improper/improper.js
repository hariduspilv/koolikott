define([
    'app',
    'services/translationService',
    'services/authenticatedUserService'
], function (app) {
    app.directive('dopReportImproper', ['translationService', '$mdDialog', '$translate', 'authenticatedUserService', function (translationService, $mdDialog, $translate, authenticatedUserService) {
        return {
            scope: {
                material: '=',
                portfolio: '='
            },
            templateUrl: 'directives/report/improper/improper.html',
            controller: function ($scope, serverCallService) {
                $scope.isReported = false;
                $scope.isReportedByUser = false;

                $scope.$watch('material', function (newValue, oldValue) {
                    if (newValue === undefined) return;
                    getHasReported();
                }, false);

                $scope.$watch('portfolio', function (newValue, oldValue) {
                    if (newValue === undefined) return;
                    getHasReported();
                }, false);

                function getHasReported() {
                    var url;

                    if ($scope.portfolio && $scope.portfolio.id) {
                        url = "rest/impropers/portfolios/" + $scope.portfolio.id;

                        serverCallService.makeGet(url, {}, requestSuccessful, requestFailed);
                    } else if ($scope.material && $scope.material.id) {
                        url = "rest/impropers/materials/" + $scope.material.id;

                        serverCallService.makeGet(url, {}, requestSuccessful, requestFailed);
                    }
                }

                function requestSuccessful(response) {
                    if ($scope.isAdmin) {
                        $scope.isReported = response === true;
                    } else {
                        $scope.isReportedByUser = response === true;
                    }
                }

                function requestFailed() {
                    console.log("Failed checking if already reported the resource")
                }

                $scope.setNotImproper = function () {
                    if($scope.isAdmin) {
                        if ($scope.portfolio) {
                            url = "rest/impropers?portfolio=" + $scope.portfolio.id;
                        } else if ($scope.material) {
                            url = "rest/impropers?material=" + $scope.material.id;
                        }

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
                            material: $scope.material,
                            portfolio: $scope.portfolio
                        };

                        serverCallService.makePut("rest/impropers", entity, setImproperSuccessful, setImproperFailed);
                    });
                };

                $scope.isAdmin = authenticatedUserService.isAdmin();

                function setImproperSuccessful() {
                    $scope.isReportedByUser = true;
                }

                function setImproperFailed() {
                    $scope.isReportedByUser = false;
                }
            }
        };
    }]);
});