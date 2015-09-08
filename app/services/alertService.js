define(['app'], function(app) {
    
    var ALERT_TYPE_ERROR = "alert-danger";
    var alert = {};
    
    app.factory('alertService',[function() {
            return {
                clearMessage : function() {
                    alert = {};
                },
                
                getAlert : function(message) {
                    return alert;
                },

                setErrorAlert : function(message) {
                    alert.message = message;
                    alert.type = ALERT_TYPE_ERROR;
                }
	    };
    }]);
});
