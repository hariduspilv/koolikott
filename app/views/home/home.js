define([
    'services/serverCallService',
    'directives/materialBox/materialBox',
    'directives/portfolioBox/portfolioBox'
], function(serverCallService) {
    return ['$scope', 'serverCallService', function ($scope, serverCallService) {
        $scope.showHints = true;

        serverCallService.makeGet("rest/learningObjects/getNewest?maxResults=8", {}, getNewestLearningObjectsSuccess, requestFailed);
        serverCallService.makeGet("rest/learningObjects/getPopular?maxResults=8", {}, getPopularLearningObjectsSuccess, requestFailed);

        function getNewestLearningObjectsSuccess(data) {
            if (isEmpty(data)) {
                console.log('Failed to get newest learning objects.');
            } else {
                $scope.newestItems = data;
            }
        }

        function getPopularLearningObjectsSuccess(data) {
            if (isEmpty(data)) {
                console.log('Failed to get most popular learning objects');
            } else {
                $scope.popularItems = data;
            }
        }

        function requestFailed() {
            console.log('Failed to get learning objects.')
        }
    }];
});
