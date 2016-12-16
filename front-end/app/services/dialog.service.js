'use strict';

angular.module('koolikottApp')
.factory('dialogService',
[
    '$mdDialog', '$filter',
    function($mdDialog, $filter) {
        var ALERT_TYPE_ERROR = "alert-danger";
        var alert = {};

        return {
            showDeleteConfirmationDialog: function(title, content, onConfirm, onCancel) {
                this.showConfirmationDialog(title, content, 'ALERT_CONFIRM_POSITIVE', 'ALERT_CONFIRM_NEGATIVE', onConfirm, onCancel)
            },
            showConfirmationDialog: function(title, content, ok, cancel, onConfirm, onCancel) {
                var confirm = $mdDialog.confirm()
                .title($filter('translate')(title))
                .content($filter('translate')(content))
                .ok($filter('translate')(ok))
                .cancel($filter('translate')(cancel));

                $mdDialog.show(confirm).then(function() {
                    onConfirm();
                }, function() {
                    if (onCancel){
                        onCancel();
                    }
                });
            },
        };
    }
]);
