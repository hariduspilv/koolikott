'use strict';

angular.module('koolikottApp')
.factory('alertService', function() {
    var ALERT_TYPE_ERROR = "alert-danger";
    var alert = {};

    return {
        clearMessage: function() {
            alert = {};
        },

        getAlert: function(message) {
            return alert;
        },

        setErrorAlert: function(message) {
            alert.message = message;
            alert.type = ALERT_TYPE_ERROR;
        }
    };
});
