define([
    'angularAMD',
    'services/serverCallService',
], function (angularAMD) {
    angularAMD.directive('dopErrorMessage', ['$location', 'serverCallService', function ($location, serverCallService) {
        return {
            scope: {
                data: '='
            },
            templateUrl: 'directives/errorMessage/errorMessage.html',
            controller: function ($rootScope, $scope) {

                $scope.deleted = true;

                checkItemState($scope.data);

                function checkItemState(data) {
                    if(data) {
                        if (data.deleted) {
                            $scope.showDeleted = true;
                            $rootScope.isDeleted = true;
                        } else {
                            $rootScope.isDeleted = false;
                            $scope.showDeleted = false;
                        }
                        if (data.improper == 1 || data.broken == 1) {
                            $rootScope.isBrokenImproper = true;
                        } else {
                            $rootScope.isBrokenImproper = false;
                        }
                        if (data.improper == 1) {
                            getImproperMessage(data.id);
                            $scope.showImproper = true;
                        }
                        if (data.broken == 1) {
                            $scope.showBroken = true;
                        }
                    }
                    $rootScope.forceUpdate = new Date();
                }

                if($scope.data) {
                    checkItemState($scope.data);
                }

                $scope.$watch('data', function() {
                    checkItemState($scope.data);
                }, true);

                function getImproperMessage(id) {
                    serverCallService.makeGet("rest/impropers", {}, getImproperItemsSuccess, getItemsFail);

                    function getImproperItemsSuccess(impropers) {
                        for (var i = 0; i < impropers.length; i++) {
                            if (impropers[i].learningObject.id == id) {
                                $scope.improperMessage = impropers[i].reason;
                            }
                        }
                    }
                }


                function getItemsFail() {
                    console.log("Failed to get items");
                }
            }
        }
    }]);
});
