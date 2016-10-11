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

                // TODO: data.deleted = true/false, data.
                console.log($scope.data);

                function checkItemState(data) {
                    if(data) {
                        if (data.deleted) {
                            // For header to change color
                            //  $rootScope.isDeleted = true;
                            console.log("Deleted");
                            $scope.showDeleted = true;
                        } else {
                            $rootScope.isDeleted = false;
                        }
                        if (data.improper > 0) {
                            console.log("Improper");
                            $scope.showImproper = true;
                        }
                        if (data.broken > 0) {
                            console.log("Broken");
                            $scope.showBroken = true;
                        }
                    }

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
