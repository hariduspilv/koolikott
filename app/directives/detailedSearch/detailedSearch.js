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
                    
                    var validEducationalContexts = ['preschooleducation', 'basiceducation', 'secondaryeducation', 'vocationaleducation'];
                    var defaultEducationalContext = 'preschooleducation';

                    if (searchService.getEducationalContext() && validEducationalContexts.indexOf(searchService.getEducationalContext()) > -1) {
                        $scope.detailedSearch.educationalContext = searchService.getEducationalContext();
                    } else {
                        $scope.detailedSearch.educationalContext = defaultEducationalContext;
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
