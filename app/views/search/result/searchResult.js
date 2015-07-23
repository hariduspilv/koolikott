define(['app'], function(app)
{
    app.controller('searchResultController', ['$scope', "serverCallService", 'translationService', '$location', '$rootScope',
             function($scope, serverCallService, translationService, $location, $rootScope) {
    	var searchObject = $location.search();

    	if (searchObject.q) {
            $scope.searching = true;
            $scope.searchQuery = searchObject.q;
            var start = 0;
            if (searchObject.start) {
            	start = searchObject.start;
            }
	    	var params = {
	    	    'q': searchObject.q,
	    	    'start': start
	    	};
	    	serverCallService.makeGet("rest/search", params, getAllMaterialSuccess, getAllMaterialFail);
	    	serverCallService.makeGet("rest/search/countResults", params, getResultCountSuccess, getResultCountFail);
	    	$rootScope.searchFields.searchQuery = searchObject.q;
    	} else {
            $location.url('/');
        }
    	
    	function getAllMaterialSuccess(data) {
            if (isEmpty(data)) {
                log('No data returned by session search.');
            } else {
                $scope.materials = data;
            }
            $scope.searching = false;
    	}
    	
    	function getAllMaterialFail(data, status) {
            console.log('Session search failed.')
            $scope.searching = false;
    	}
    	
    	function getResultCountSuccess(data) {
            if (isEmpty(data)) {
                log('No result count returned.');
            } else {
                $scope.resultCount = data;
            }
    	}
    	
    	function getResultCountFail(data, status) {
            console.log('Failed to get result count');
    	}

        $scope.getNumberOfResults = function() {
            if (!$scope.materials) {
                return 0;
            }
            
            if ($scope.resultCount) {
            	return $scope.resultCount;
            }

            return $scope.materials.length;
        }
    	
    	$scope.$on("$destroy", function() {
    		$rootScope.searchFields.searchQuery = "";
        });
    	
    }]);
});