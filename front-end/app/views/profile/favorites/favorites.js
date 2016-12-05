define([
    'directives/infiniteSearchResult/infiniteSearchResult'
], function () {
    return ['$scope',
        function ($scope) {
            $scope.url = "rest/learningObject/usersFavorite";
            $scope.params = {
                'maxResults': 20
            };
        }];
});
