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

                function checkItemState(data) {
                    if (data.deleted == true) {
                        $rootScope.isDeleted = true;
                        $scope.showError = true;
                        $scope.errorMessage = 'See kogumik on kustutatud!';
                        $scope.errorIcon = 'delete';
                    } else if (data.improper > 0) {
                        $scope.showError = true;
                        $scope.errorMessage = 'See kogumik on lubamatu!';
                        $scope.errorIcon = 'report';
                    } else if (data.borken > 0) {
                        $scope.showError = true;
                        $scope.errorMessage = 'See kogumik on vigane!';
                        $scope.errorIcon = 'report';
                    } else {
                        $rootScope.isDeleted = false;
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
