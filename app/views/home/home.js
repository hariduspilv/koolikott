define([
    'services/serverCallService',
    'directives/materialBox/materialBox',
    'directives/portfolioBox/portfolioBox'
], function(serverCallService) {
    return ['$scope', 'serverCallService', function ($scope, serverCallService) {
        $scope.showHints = true;

        serverCallService.makeGet("rest/material/getNewestMaterials?numberOfMaterials=8", {}, getNewestMaterialsSuccess, requestFailed);
        serverCallService.makeGet("rest/learningObjects/getPopular?count=8", {}, getPopularLearningObjectsSuccess, requestFailed);

        function getNewestMaterialsSuccess(data) {
            if (isEmpty(data)) {
                console.log('No data returned by session search.');
            } else {
                $scope.materials = data;
            }
        }

        function getPopularLearningObjectsSuccess(data) {
            if (isEmpty(data)) {
                console.log('No data returned by session search.');
            } else {
                $scope.popularItems = data;
            }
        }

        function requestFailed() {
            console.log('Session search failed.')
        }
    }];
});
