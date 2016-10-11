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

                $scope.$watch(function(){
                    return $location.path();
                }, function(value){
                    if($scope.data) {
                        console.log($scope.data);
                    }
                });

            }
        }
    }]);
});
