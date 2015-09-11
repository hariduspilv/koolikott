define(['app'], function(app)
{
    
    app.directive('dopFooter', function(translationService) {
        return {
            scope: true,
            templateUrl: 'app/directives/footer/footer.html',
            controller: function ($scope, $location) {

                function setSiteLang(language) {
                    $scope.selectedLanguage = language;
                }

                $scope.$watch(function () {
                        return translationService.getLanguage();
                    }, function(language) {
                        setSiteLang(language);
                }, true);
                
                setSiteLang(translationService.getLanguage());
            }
        };
    });
    
    return app;
});