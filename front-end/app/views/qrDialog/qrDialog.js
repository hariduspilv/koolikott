

angular.module('koolikottApp')
    .controller('qrDialogController',
        [
            '$scope', '$mdDialog', '$location', 'monospaced.qrcode',
            function ($scope, $mdDialog, $location) {

                function init() {
                }

                $scope.cancel = function () {
                    $mdDialog.hide();
                };

                init();
            }
        ]);
