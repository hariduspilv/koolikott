define(['app'], function(app)
{
    app.controller('addPortfolioDialog', ['$scope', '$mdDialog', 'serverCallService',
        function($scope, $mdDialog, serverCallService) {
    	
    		// get educational contexts
    		serverCallService.makeGet("rest/learningMaterialMetadata/educationalContext", {}, getEducationalContextSuccess, getEducationalContextFail);
            
    		function getEducationalContextSuccess(data) {
                if (isEmpty(data)) {
                	getEducationalContextFail();
                } else {
                	$scope.educationalContexts = data;
                	$scope.educationalContexts.sort(function(context1, context2) {
                		return context1.id - context2.id;
                	})
                }
        	}
        	
        	function getEducationalContextFail() {
                console.log('Failed to get educational contexts.')
        	}
    		
    		
            $scope.cancel = function() {
                $mdDialog.cancel();
            };
            
            $scope.portfolio = {
            	title: null,
            	summary: null,
            	educationalContext: null
            };
            
        }
    ]);

    app.directive('dopAddPortfolio', [ 
        function() {
            return {
                scope: true,
                templateUrl: 'app/directives/addPortfolio/addPortfolio.html',
                controller: function ($scope, $mdDialog) {
                    
                    $scope.ShowAddPortfolioDialog = function(ev) {
                        $mdDialog.show({
                            controller: 'addPortfolioDialog',
                            templateUrl: 'app/directives/addPortfolio/addPortfolioDialog.html',
                            targetEvent: ev
                        });
                    };                    
                }
            };
        }
    ]);

    return app;
});