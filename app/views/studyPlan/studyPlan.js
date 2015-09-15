define(['app'], function(app)
{
    app.controller('studyPlanController', ['$scope', 'translationService', 'serverCallService', '$route', 
        function($scope, translationService, serverCallService, $route) {

	    	var studyPlanId = $route.current.params.id;
	        serverCallService.makeGet("rest/studyPlan?id=" + studyPlanId, {}, getStudyPlanSuccess, getStudyPlanFail);
	        
	        function getStudyPlanSuccess(studyPlan) {
	            if (isEmpty(studyPlan)) {
	            	getStudyPlanFail();
	            } else {
	                $scope.studyPlan = studyPlan;
	            }
	    	}
	    	
	    	function getStudyPlanFail() {
	            log('No data returned by getting studyPlan.');
	    	}

	    	$scope.formatStudyPlanCreatedDate = function(createdDate) {
            	var created = new Date(createdDate);
            	return formatDay(created.getDate()) + "." + formatMonth(created.getMonth() + 1) + "." + created.getFullYear();
        	}
    	}
    ]);
});