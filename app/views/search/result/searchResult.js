define(['app'], function(app)
{
    app.controller('searchResultController', ['$scope', "serverCallService", "$filter", 
             function($scope, serverCallService, $filter) {
    	var params = {};
    	serverCallService.makeGet("rest/material/getAll", params, getAllMaterialSuccess, getAllMaterialFail);
    	
    	function getAllMaterialSuccess(data) {

            if (isEmpty(data)) {
                log('No data returned by session search.');
                } else {
                        $scope.materials = data;
                }
    	}
    	
    	function getAllMaterialFail(data, status) {
            console.log('Session search failed.')
    	}
    	
    	$scope.formatIssueDate = function(issueDate) {
    		if (!issueDate) {
    			return;
    		}
    		
    		if (issueDate.day && issueDate.month && issueDate.year) {
    			// full date
    			return formatDay(issueDate.day) + "." + formatMonth(issueDate.month) + "." + formatYear(issueDate.year); 
    		} else if (issueDate.month && issueDate.year) {
    			// month date
    			return formatMonth(issueDate.month) + "." + formatYear(issueDate.year); 
    		} else if (issueDate.year) {
    			// year date
    			return formatYear(issueDate.year); 
    		}
    	}
    	
    	function formatDay(day) {
    		return day > 9 ? "" + day : "0" + day; 
    	}
    	
    	function formatMonth(month) {
    		return month > 9 ? "" + month : "0" + month; 
    	}
    	
    	function formatYear(year) {
    		return year < 0 ? year * -1 : year; 
    	}

        $scope.formatName = function(name){
            var array = name.split(" ");
            var res = "";
            for(var i = 0; i < array.length; i++) res += (array[i].charAt(0).toUpperCase() + ". ");
            return res;
        }

        $scope.formatSurname = function(surname){
            var array = surname.split(" ");
            var res = "";
            console.log(array)
            for(var i = 0; i < array.length - 1; i++) res += (array[i].charAt(0).toUpperCase() + ". ");
            res += array[array.length - 1];
            return res;
        }
    }]);
});