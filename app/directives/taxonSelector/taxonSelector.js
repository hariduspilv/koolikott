define(['app'], function(app)
{
	var EDUCATIONAL_CONTEXTS;
	
    app.directive('dopTaxonSelector', ['serverCallService',
     function() {
        return {
            scope: {
                taxon: '='
            },
            templateUrl: 'directives/taxonSelector/taxonSelector.html',
            controller: function ($scope, serverCallService) {
            	
            	// get educational contexts
            	if (!EDUCATIONAL_CONTEXTS) {
            		serverCallService.makeGet("rest/learningMaterialMetadata/educationalContext", {}, getEducationalContextSuccess, getEducationalContextFail);
            	} else {
                	setEducationalContexts();
            	}

        		function getEducationalContextSuccess(data) {
                    if (isEmpty(data)) {
                    	getEducationalContextFail();
                    } else {
                    	EDUCATIONAL_CONTEXTS = data;
                    	setEducationalContexts();
                    }
            	}

            	function getEducationalContextFail() {
                    console.log('Failed to get educational contexts.')
            	}
            	
            	function setEducationalContexts() {
            		$scope.educationalContexts = EDUCATIONAL_CONTEXTS;
                	$scope.educationalContexts.sort(function(context1, context2) {
                		return context1.id - context2.id;
                	});
            	}
            }
        };
    }]);

    return app;
});
