define(['app'], function(app)
{
    app.controller('studyPlanController', ['$scope', 'translationService', 'serverCallService', '$route', '$location', 'alertService',
        function($scope, translationService, serverCallService, $route, $location, alertService) {

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
	            log('No data returned by getting portfolio.');
	            alertService.setErrorAlert('ERROR_PORTFOLIO_NOT_FOUND');
	            $location.url("/");
	    	}

	    	$scope.formatStudyPlanCreatedDate = function(createdDate) {
	    		return formatDatetoDayMonthYear(createdDate);
            }
    	}
    ]);
});