define([
    'services/serverCallService',
    'directives/materialBox/materialBox',
    'directives/portfolioBox/portfolioBox'
], function(serverCallService) {
    return ['$scope', 'serverCallService', '$rootScope', function ($scope, serverCallService, $rootScope) {
        $scope.showHints = true;
        $rootScope.savedPortfolio = null;

        serverCallService.makeGet("rest/learningObjects/getNewest?maxResults=8", {}, getNewestLearningObjectsSuccess, requestFailed);
        serverCallService.makeGet("rest/search?q=&type=all&sort=views&sortDirection=desc&start=0&limit=8", {}, getPopularLearningObjectsSuccess, requestFailed);

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
                $scope.popularItems = data.items;
            }
        }

        function requestFailed() {
            console.log('Failed to get learning objects.')
        }
    }];
});
