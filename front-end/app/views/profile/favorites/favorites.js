define([
    'app',
    'directives/materialBox/materialBox',
    'directives/portfolioBox/portfolioBox',
    'services/serverCallService'
], function (app) {
    return ['$scope', '$route', 'serverCallService',
        function ($scope, $route, serverCallService ) {
        function init() {
            getUserFavorites();
        }

        function getUserFavorites() {
            serverCallService.makeGet("rest/learningObject/usersFavorite", {}, getFavoritesSuccess, getDataFailed)
        }

        function getFavoritesSuccess(data) {
            if(data) {
                $scope.favorites = data
            }
        }

        function getDataFailed() {
            console.log("Failed to get data")
        }

        init();
    }];
});
