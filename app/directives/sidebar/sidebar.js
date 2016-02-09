define([
    'angularAMD',
    'services/serverCallService',
    'directives/learningObjectRow/learningObjectRow'
], function(angularAMD) {
    angularAMD.directive('dopSidebar', ['serverCallService', function() {
        return {
            scope: true,
            templateUrl: 'directives/sidebar/sidebar.html',
            controller: function($scope, serverCallService) {
                
                var SIDE_ITEMS_AMOUNT = 5;

                var params = {
                    q: 'recommended:true',
                    start: 0,
                    sort: 'recommendation_timestamp',
                    sortDirection: 'desc',
                    limit: SIDE_ITEMS_AMOUNT
                };

                serverCallService.makeGet("rest/search", params, getRecommendationsSuccess, getRecommendationsFail);
                
                var params = {
                    maxResults: SIDE_ITEMS_AMOUNT
                };

                serverCallService.makeGet("rest/search/mostLiked", params, getMostLikedSuccess, getMostLikedFail);

                function getRecommendationsSuccess(data) {
                    if (isEmpty(data)) {
                        log('No data returned by recommended item search.');
                    } else {
                        $scope.recommendations = data.items;
                    }
                }

                function getRecommendationsFail(data, status) {
                    console.log('Session search failed.')
                }
                
                function getMostLikedSuccess(data) {
                    if (isEmpty(data)) {
                    	getMostLikedFail();
                    } else {
                        $scope.mostLikedList = data;
                    }
                }

                function getMostLikedFail() {
                    console.log('Most liked search failed.')
                }
            }
        }
    }]);
});