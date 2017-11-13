'use strict';

angular.module('koolikottApp')
.factory('toastService',
[
    '$mdToast', '$translate', '$rootScope',
    function($mdToast, $translate, $rootScope) {
        var instance;
        var toast;

        $rootScope.$on('$routeChangeSuccess', function() {
            if (toast) {
                instance.show(toast);
                toast = null;
            }
        });

        instance = {
            show: function(translationKey) {
                $translate(translationKey).then(translatedStr =>
                    $mdToast.show($mdToast.simple().position('right bottom').content(translatedStr))
                )
            },

            showOnRouteChange: function(content) {
                toast = content;
            }
        };

        return instance;
    }
]);
