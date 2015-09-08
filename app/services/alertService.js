define(['app'], function(app) {
    
    var ALERT_TYPE_ERROR = "alert-danger";
    
    app.factory('alertService',['$rootScope',
        function($rootScope) {
		
            return {
                clearMessages : function() {
                    $rootScope.messages = [];
                },

                setErrorAlert : function(message) {
                    $rootScope.alert = {};
                    $rootScope.alert.message = message;
                    $rootScope.alert.type = ALERT_TYPE_ERROR;
                }
	    };
    }]);
});
