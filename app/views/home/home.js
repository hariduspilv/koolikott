define([
    'services/serverCallService',
    'directives/materialBox/materialBox'
], function(serverCallService) {
    return ['$scope', 'serverCallService', function ($scope, serverCallService) {
        $scope.showHints = true;

        serverCallService.makeGet("rest/material/getNewestMaterials?numberOfMaterials=8", {}, getNewestMaterialsSuccess, requestFailed);
        serverCallService.makeGet("rest/material/getPopularMaterials?numberOfMaterials=8", {}, getPopularMaterialsSuccess, requestFailed);
        
        function getNewestMaterialsSuccess(data) {
            if (isEmpty(data)) {
                console.log('No data returned by session search.');
            } else {
                $scope.materials = data;
            }
        }

        function getPopularMaterialsSuccess(data) {
            if (isEmpty(data)) {
                console.log('No data returned by session search.');
            } else {
                $scope.popularMaterials = data;
            }
        }

        function requestFailed(data, status) {
            console.log('Session search failed.')
        }
    }];
});