define([
    'services/serverCallService',
    'directives/materialBox/materialBox',
    'directives/portfolioBox/portfolioBox',
    'services/storageService'
], function (serverCallService) {
    return ['$scope', 'serverCallService', '$rootScope', 'storageService', '$timeout', function ($scope, serverCallService, $rootScope, storageService, $timeout) {
        $scope.showHints = true;
        $rootScope.savedPortfolio = null;
        $scope.newestItems = storageService.getNewestItems();
        $scope.popularItems = storageService.getPopularItems();

        serverCallService.makeGet("rest/learningObjects/getNewest?maxResults=8",
            {}, getNewestLearningObjectsSuccess, requestFailed);
        $timeout(serverCallService.makeGet("rest/search?q=&type=all&sort=views&sortDirection=desc&start=0&limit=8",
            {}, getPopularLearningObjectsSuccess, requestFailed));

        function getNewestLearningObjectsSuccess(data) {
            if (isEmpty(data)) {
                console.log('Failed to get newest learning objects.');
            } else {
                $scope.newestItems = data;
                storageService.setNewestItems(data);
            }
        }

        function getPopularLearningObjectsSuccess(data) {
            if (isEmpty(data)) {
                console.log('Failed to get most popular learning objects');
            } else {
                $scope.popularItems = data.items;
                storageService.setPopularItems(data.items);
            }
        }

        function requestFailed() {
            console.log('Failed to get learning objects.')
        }
    }];
});
