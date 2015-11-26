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
            		/*
            		 * The other of the watchers is important and should be the same as the three.
            		 */
            		
            		$scope.$watch('taxon', function(newTaxon, oldTaxon) {
                        if (newTaxon !== oldTaxon) {
                       		buildTaxonPath();
                        }
                    }, false);
            		
            		$scope.$watch('taxonPath.educationalContext', function(newEducationalContext, oldEducationalContext) {
                        if (newEducationalContext && newEducationalContext !== oldEducationalContext) {
                       		$scope.taxon = newEducationalContext;
                        }
                    }, false);
                	
                	$scope.$watch('taxonPath.domain', function(newDomain, oldDomain) {
                        if (newDomain && newDomain !== oldDomain) {
                       		$scope.taxon = newDomain;
                        }
                    }, false);
                	
                	$scope.$watch('taxonPath.subject', function(newSubject, oldSubject) {
                        if (newSubject && newSubject !== oldSubject) {
                        	$scope.taxon = newSubject;
                        }
                    }, false);
                	
                	$scope.$watch('taxonPath.specialization', function(newSpecialization, oldSpecialization) {
                        if (newSpecialization && newSpecialization !== oldSpecialization) {
                        		$scope.taxon = newSpecialization;
                        }
                    }, false);
                	
                	$scope.$watch('taxonPath.module', function(newModule, oldModule) {
                        if (newModule && newModule !== oldModule) {
                        		$scope.taxon = newModule;
                        }
                    }, false);
                	
                	$scope.$watch('taxonPath.topic', function(newTopic, oldTopic) {
                		if (newTopic && newTopic !== oldTopic) {
                			$scope.taxon = newTopic;
                		}
                	}, false);
                	
                	$scope.$watch('taxonPath.subtopic', function(newSubtopic, oldSubtopic) {
                		if (newSubtopic && newSubtopic !== oldSubtopic) {
                			$scope.taxon = newSubtopic;
                		}
                	}, false);
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
        			$scope.taxonPath.specialization = $rootScope.taxonUtils.getSpecialization($scope.taxon);
        			$scope.taxonPath.module = $rootScope.taxonUtils.getModule($scope.taxon);
        			$scope.taxonPath.topic = $rootScope.taxonUtils.getTopic($scope.taxon);
        			$scope.taxonPath.subtopic = $rootScope.taxonUtils.getSubtopic($scope.taxon);
            	}
            }
        };
    }]);

    return app;
});
