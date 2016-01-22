define(['app'], function (app) {  
    app.controller('dashboardController', ['$scope', '$location', '$sce', '$templateRequest', '$compile', 'translationService', function ($scope, $location, $sce, $templateRequest, $compile, translationService) {
        var collection = null;
       
        $scope.filter = {
            options: {
              debounce: 500
            }
        };

        $scope.query = {
            filter: '',
            order: 'bySubmittedAt',
        };

        function getItemsSuccess(improper) {
            if (isEmpty(improper)) {
                log('No data returned by session search.');
            } else {
                collection = improper;
                $scope.data = improper;
                
                orderItems($scope.query.order);
            }
        }
        
        function getItemsFail() {
            console.log('Session search failed.')
        }
        
        function orderItems(order) {
            $scope.data = $scope.data.sort(function(a, b) {
                if (order === 'bySubmittedAt' || order === '-bySubmittedAt')
                    return new Date(b.added) - new Date(a.added);
                
                if (order === 'bySubmittedBy' || order == '-bySubmittedBy')
                    return (a.creator.name + ' ' + a.creator.surname).localeCompare(b.creator.name + ' ' + b.creator.surname);
                
                return 0;
            });

            if (order.slice(0, 1) === '-')
                $scope.data.reverse();
        }
        
        function filterItems() {
            $scope.data = collection.filter(function(data) {
                if (data && data.portfolio != null)
                  return data.portfolio.title.slice(0, $scope.query.filter.length).toLowerCase() === $scope.query.filter.toLowerCase();
                if (data && data.material != null)
                  return $scope.getCorrectLanguageTitle(data.material).slice(0, $scope.query.filter.length).toLowerCase() === $scope.query.filter.toLowerCase();
                if (data && data.type == ".Material")
                  return $scope.getCorrectLanguageTitle(data).slice(0, $scope.query.filter.length).toLowerCase() === $scope.query.filter.toLowerCase();
                if (data && data.type == ".Portfolio")
                    return data.title.slice(0, $scope.query.filter.length).toLowerCase() === $scope.query.filter.toLowerCase();
            });
        }
        
        $scope.getCorrectLanguageTitle = function (item) {
            if (item) {
                var result = getUserDefinedLanguageString(item.titles, translationService.getLanguage(), item.language);
                if(!result) {
                	return "";
                }
                return result;
            }
		};
        
        $scope.onReorder = function (order) {
            orderItems(order);
        };
        
        $scope.removeFilter = function () {
            $scope.filter.show = false;
            $scope.query.filter = '';
            $scope.data = collection;
            
            if ($scope.filter.form.$dirty) {
                $scope.filter.form.$setPristine();
            }
        };
        
        $scope.$watch('query.filter', function (newValue, oldValue) {
            if (newValue === oldValue) return;
            filterItems();
        });
        
        $scope.formatDate = function(date) {
            return formatDateToDayMonthYear(date);
        }
        
        $scope.gotoImproperMaterials = function() {
    		$location.url("/dashboard/improper/material");
    	}
    	
    	$scope.gotoImproperPortfolios = function() {
    		$location.url("/dashboard/improper/portfolio");
    	}
    	
    	$scope.gotoDeletedMaterials = function() {
    		$location.url("/dashboard/deleted/material");
    	}
        
    	$scope.gotoDeletedPortfolios = function() {
    		$location.url("/dashboard/deleted/portfolio");
    	}
    	
    	$scope.gotoBrokenMaterials = function() {
    		$location.url("/dashboard/broken/material");
    	}
    	
        return {
            getItemsSuccess: getItemsSuccess,
            getItemsFail: getItemsFail
        }
    }]);
    
});
