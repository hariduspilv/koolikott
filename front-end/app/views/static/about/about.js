'use strict'

angular.module('koolikottApp')
.controller('aboutController',
[
    '$scope', '$controller', function($scope, $controller) {
        $scope.pageName = 'about';
        $controller('abstractStaticPageController', {
            $scope: $scope
        });
    }
]);
