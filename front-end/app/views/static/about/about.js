define([
    'app',
    'views/static/abstractStaticPage'
], function(app) {
    return ['$scope', '$controller', function($scope, $controller) {
        $scope.pageName = 'about';
        $controller('abstractStaticPageController', {
            $scope: $scope
        });
    }];
});