define([
    'angularAMD'
], function (angularAMD) {
    angularAMD.directive('dopErrorMessage', ['metadataService', function (metadataService) {
        return {
            scope: {
                data: '='
            },
            templateUrl: 'directives/errorMessage/errorMessage.html',
            controller: function ($rootScope, $scope) {

                $scope.deleted = true;

                // TODO: data.deleted = true/false, data.
                if ($scope.data) {
                    console.log($scope.data);
                }

            }
        }
    }]);
});
