define(['app'], function(app)
{
    app.controller('addPortfolioDialog', ['$scope', '$mdDialog', 'serverCallService', '$location',
        function($scope, $mdDialog, serverCallService, $location) {
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
                $mdDialog.hide();
            };

            $scope.forward = function() {
                $mdDialog.hide();

                $location.path('/addPortfolio');
            }

            $scope.portfolio = {
            	educationalContext: null,
                fieldId: 0,
				fields: [],
				topicId: 0,
				topics: [],
				subTopicId: 0,
				subTopics: [],
                tags: ['Tag1', 'Tag2']
            };
        }
    ]);
});
