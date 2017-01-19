'use strict'

angular.module('koolikottApp')
.controller('helpController',
[
    '$scope', '$controller', function($scope, $controller) {
        $scope.pageName = 'help';
        $controller('abstractStaticPageController', {
            $scope: $scope
        });
    }
]);
