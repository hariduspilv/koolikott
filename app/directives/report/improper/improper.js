define(['app'], function (app) {
    app.directive('dopReportImproper', ['translationService', '$mdDialog', '$translate',
        function (translationService, $mdDialog, $translate) {
            return {
                scope: {
                    material: '=',
                    portfolio: '='
                },
                templateUrl: 'directives/report/improper/improper.html',
                controller: function ($scope, serverCallService) {
                    $scope.isAlreadyReported = false;


                    $scope.$watch('material', function(newValue, oldValue) {
                        if(newValue === undefined) return;
                        getHasReported();
                    }, false);

                    $scope.$watch('portfolio', function(newValue, oldValue) {
                        if(newValue === undefined) return;
                        getHasReported();
                    }, false);

                    var getHasReported = function () {
                        var url;


                        if ($scope.portfolio && $scope.portfolio.id) {
                            url = "rest/portfolio/hasSetImproper?portfolioId=" + $scope.portfolio.id;

                            serverCallService.makeGet(url, {}, requestSuccessful, requestFailed);
                        } else if ($scope.material && $scope.material.id) {
                            url = "rest/material/hasSetImproper?materialId="+ $scope.material.id;

                            serverCallService.makeGet(url, {}, requestSuccessful, requestFailed);
                        }

                        return true;
                    };

                    function requestSuccessful(response) {
                        if(response === true) {
                            $scope.isAlreadyReported = true;
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
                            $scope.isAlreadyReported = true;
                        });


                    };


                    function setImproperSuccessful() {}

                    function setImproperFailed() {}

                }
            };
        }]);

    return app;
});
