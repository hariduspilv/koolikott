define(['app'], function(app)
{
    app.directive('dopRaportBrokenLink', ['translationService', '$mdDialog', '$translate',
     function(translationService, $mdDialog, $translate) {
        return {
            scope: false,
            templateUrl: 'directives/raport/brokenLink/brokenLink.html',
            controller: function($scope) {
                $scope.showConfirmationDialog = function() {
                    var confirm = $mdDialog.confirm()
                        .title($translate.instant('REPORT_BROKEN_LINK_TITLE'))
                        .content($translate.instant('REPORT_BROKEN_LINK_CONTENT'))
                        .ok($translate.instant('BUTTON_REPORT'))
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