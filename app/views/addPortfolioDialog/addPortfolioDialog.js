define(['app'], function(app)
{
    app.controller('addPortfolioDialog', ['$scope', '$mdDialog', '$location',
        function($scope, $mdDialog, $location) {

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
