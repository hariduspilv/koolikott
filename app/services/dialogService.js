define(['app'], function(app) {
    
    var ALERT_TYPE_ERROR = "alert-danger";
    var alert = {};
    
    app.factory('dialogService',['$mdDialog', '$filter',
        function($mdDialog, $filter) {
            return {
                showDeleteConfirmationDialog: function(title, content, onConfirm, onCancel) {
                    var confirm = $mdDialog.confirm()
                        .title($filter('translate')(title))
                        .content($filter('translate')(content))
                        .ok($filter('translate')('ALERT_CONFIRM_POSITIVE'))
                        .cancel($filter('translate')('ALERT_CONFIRM_NEGATIVE'));
                        
                    $mdDialog.show(confirm).then(function() {
                        onConfirm();     
                    }, function() {
                        if (onCancel){
                            onCancel();
                        }
                    });
                }
            };
        }
    ]);
});
