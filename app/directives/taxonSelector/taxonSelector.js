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
            		init();
            	}
            	
            	function init() {
            		setEducationalContexts();
            		buildTaxonPath();
            		addTaxonPathListeners();                	
            	}
            	
            	function addTaxonPathListeners() {
            		$scope.$watch('taxonPath.educationalContext', function(newEducationalContext, oldEducationalContext) {
                        if (newEducationalContext !== oldEducationalContext) {
                        	$scope.taxon = newEducationalContext;
                        	$scope.taxonPath.domain = null;
                        	$scope.taxonPath.subject = null;
                        }
                    }, true);
                	
                	$scope.$watch('taxonPath.domain', function(newDomain, oldDomain) {
                        if (newDomain !== oldDomain) {
                        	$scope.taxon = newDomain;
                        	$scope.taxonPath.subject = null;
                        }
                    }, true);
                	
                	$scope.$watch('taxonPath.subject', function(newSubject, oldSuject) {
                        if (newSubject !== oldSuject) {
                        	$scope.taxon = newSubject;
                        }
                    }, true);
            	}

        		function getEducationalContextSuccess(data) {
                    if (isEmpty(data)) {
                    	getEducationalContextFail();
                    } else {
                    	EDUCATIONAL_CONTEXTS = data;
                    	init();
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
            	
            	function buildTaxonPath() {
            		if ($scope.taxon) {
            			$scope.taxonPath = {}
            			$scope.taxonPath.educationalContext = $scope.taxonUtils.getEducationalContext(taxon);
            			$scope.taxonPath.domain = $scope.taxonUtils.getDomain(taxon);
            			$scope.taxonPath.subject = $scope.taxonUtils.getSubject(taxon);
            		}
            	}
            }
        };
    }]);

    return app;
});
