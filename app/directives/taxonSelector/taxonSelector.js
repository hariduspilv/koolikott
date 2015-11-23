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
            controller: function ($scope, serverCallService, $rootScope) {
            	
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
            		$scope.$watch('taxon', function(newTaxon, oldTaxon) {
                        if (newTaxon !== oldTaxon) {
                       		buildTaxonPath();
                        }
                    }, true);
            		
            		$scope.$watch('taxonPath.educationalContext', function(newEducationalContext, oldEducationalContext) {
                        if (newEducationalContext && newEducationalContext !== oldEducationalContext) {
                       		$scope.taxon = newEducationalContext;
                        }
                    }, true);
                	
                	$scope.$watch('taxonPath.domain', function(newDomain, oldDomain) {
                        if (newDomain && newDomain !== oldDomain) {
                       		$scope.taxon = newDomain;
                        }
                    }, true);
                	
                	$scope.$watch('taxonPath.subject', function(newSubject, oldSubject) {
                        if (newSubject && newSubject !== oldSubject) {
                        	$scope.taxon = newSubject;
                        }
                    }, true);
                	
                	$scope.$watch('taxonPath.topic', function(newTopic, oldTopic) {
                        if (newTopic && newTopic !== oldTopic) {
                        		$scope.taxon = newTopic;
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
            		if (!$scope.taxonPath) {
            			$scope.taxonPath = {}
            		}
            		
        			$scope.taxonPath.educationalContext = $rootScope.taxonUtils.getEducationalContext($scope.taxon);
        			$scope.taxonPath.domain = $rootScope.taxonUtils.getDomain($scope.taxon);
        			$scope.taxonPath.subject = $rootScope.taxonUtils.getSubject($scope.taxon);
        			$scope.taxonPath.topic = $rootScope.taxonUtils.getTopic($scope.taxon);
            	}
            }
        };
    }]);

    return app;
});
