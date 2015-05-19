define(['app'], function(app)
{
    app.controller('homeController',
    [
        '$scope',

        function($scope)
        {
            $scope.message = "Hello World";
        }
    ]);
});