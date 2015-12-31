define(['app'], function (app) {
    app.directive('dopReportImproper', ['translationService', '$mdDialog', '$translate', 'authenticatedUserService',
        function (translationService, $mdDialog, $translate, authenticatedUserService) {
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
                        getHasReportedByUser();
                        getHasReported();
                    }, false);

                    $scope.$watch('portfolio', function (newValue, oldValue) {
                        if (newValue === undefined) return;
                        getHasReportedByUser();
                        getHasReported();
                    }, false);

                    function getHasReportedByUser() {
                        var url;

                        if ($scope.portfolio && $scope.portfolio.id) {
                            url = "rest/portfolio/hasSetImproper?portfolioId=" + $scope.portfolio.id;

                            serverCallService.makeGet(url, {}, requestSuccessful, requestFailed);
                        } else if ($scope.material && $scope.material.id) {
                            url = "rest/material/hasSetImproper?materialId=" + $scope.material.id;

                            serverCallService.makeGet(url, {}, requestSuccessful, requestFailed);
                        }
                    }

                    function requestSuccessful(response) {
                        if (response === true) {
                            $scope.isReportedByUser = true;
                        }
                    }

                    function requestFailed() {
                        console.log("Failed checking if already reported the resource")
                    }

                    function getHasReported() {
                        if (authenticatedUserService.isAdmin()) {
                            var url;

                            if ($scope.portfolio && $scope.portfolio.id) {
                                url = "rest/portfolio/isSetImproper?portfolioId=" + $scope.portfolio.id;

                                serverCallService.makeGet(url, {}, isReportedSuccessful, isReportedFailed);
                            } else if ($scope.material && $scope.material.id) {
                                url = "rest/material/isSetImproper?materialId=" + $scope.material.id;

                                serverCallService.makeGet(url, {}, isReportedSuccessful, isReportedFailed);
                            }
                        }
                    }

                    function isReportedSuccessful(response) {
                        if (response === true) {
                            $scope.isReported = true;
                        } else {
                            $scope.isReported = false;
                        }
                    }

                    function isReportedFailed() {
                        console.log("Failed checking if resource is reported as improper")
                    }


                    $scope.setNotImproper = function () {
                        if ($scope.portfolio) {
                            url = "rest/portfolio/setNotImproper/" + $scope.portfolio.id;
                        } else if ($scope.material) {
                            url = "rest/material/setNotImproper/" + $scope.material.id;
                        }

                        serverCallService.makePost(url, {}, setNotImproperSuccessful, setNotImproperFailed);
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
                            var url;
                            var entity;

                            if ($scope.portfolio) {
                                url = "rest/portfolio/setImproper";
                                entity = $scope.portfolio;
                            } else if ($scope.material) {
                                url = "rest/material/setImproper";
                                entity = $scope.material;
                            }

                            serverCallService.makePost(url, entity, setImproperSuccessful, setImproperFailed);
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

    return app;
});
