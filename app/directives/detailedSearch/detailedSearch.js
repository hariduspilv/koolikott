define(['app'], function(app)
{    
    app.directive('dopDetailedSearch', [ '$location', 'searchService', 
     function($location, searchService) {
        return {
            scope: true,
            templateUrl: 'app/directives/detailedSearch/detailedSearch.html',
            controller: function ($scope) {

                init();
                
                function init() {
                    $scope.detailedSearch = {};

                    if (searchService.getEducationalContext()) {
                        $scope.detailedSearch.educationalContext = searchService.getEducationalContext();
                    } else {
                        $scope.detailedSearch.educationalContext = 'preschooleducation';
                    }
                }

                $scope.search = function() {
                    // Clear the search query if it has been set from simple search
                    searchService.setSearch('');

                    searchService.setEducationalContext($scope.detailedSearch.educationalContext);
                    $location.url(searchService.getURL());
                };

            }
        };
    }]);
    
    return app;
});
