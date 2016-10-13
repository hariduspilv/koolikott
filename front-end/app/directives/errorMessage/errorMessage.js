define([
    'angularAMD'
], function (angularAMD) {
    angularAMD.directive('dopErrorMessage', ['$location', function ($location) {
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
                        }
                        if (data.improper == 1 || data.broken == 1) {
                            $rootScope.isBrokenImproper = true;
                        } else {
                            $rootScope.isBrokenImproper = false;
                        }
                        if (data.improper == 1) {
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
            }
        }
    }]);
});
