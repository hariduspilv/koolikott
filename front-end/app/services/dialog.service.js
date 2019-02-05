'use strict';

angular.module('koolikottApp')
.factory('dialogService',
[
    '$mdDialog', '$filter',
    function($mdDialog, $filter) {
        return {
            showDeleteConfirmationDialog: function(title, content, onConfirm, onCancel) {
                this.showConfirmationDialog(title, content, 'BUTTON_REMOVE', 'LOOBU', onConfirm, onCancel)
            },
            showCancelConfirmationDialog: function(title, content, onConfirm, onCancel) {
                this.showConfirmationDialog(title, content, 'BUTTON_TERMS_YES', 'BUTTON_TERMS_NO', onConfirm, onCancel)
            },
            showConfirmationDialog: function(title, content, ok, cancel, onConfirm, onCancel) {
                let confirm = $mdDialog.confirm()
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
