define(['app'], function(app)
{
    app.directive('dopRaportImporper', ['translationService', '$mdDialog', '$translate',
     function(translationService, $mdDialog, $translate) {
        return {
            scope: false,
            templateUrl: 'directives/raport/improper/improper.html',
            controller: function($scope) {
                $scope.showConfirmationDialog = function() {
                    var confirm = $mdDialog.confirm()
                        .title($translate.instant('REPORT_IMPROPER_TITLE'))
                        .content($translate.instant('REPORT_IMPROPER_CONTENT'))
                        .ok($translate.instant('BUTTON_NOTIFY'))
                        .cancel($translate.instant('BUTTON_CANCEL'));
            
                    $mdDialog.show(confirm).then(function() {
                        // todo: when user user clicks "OK" button send the notification
                    });
                }
            }
        };
    }]);

    return app;
});
