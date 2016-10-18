define([
    'angularAMD',
    'services/serverCallService',
    'services/userDataService',
], function (angularAMD) {
    angularAMD.directive('dopErrorMessage', ['$location', 'serverCallService', 'userDataService', function ($location, serverCallService, userDataService) {
        return {
            scope: {
                data: '='
            },
            templateUrl: 'directives/errorMessage/errorMessage.html',
            controller: function ($rootScope, $scope) {



            }
        }
    }]);
});
