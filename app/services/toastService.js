define(['angularAMD'], function(angularAMD) {
    angularAMD.factory('toastService', ['$mdToast', '$filter', '$rootScope', function($mdToast, $filter, $rootScope) {
        var instance;
        var toast;

        $rootScope.$on('$routeChangeSuccess', function() {
          if (toast) {
            instance.show(toast);
            toast = null;
          }
        });

        instance = {
            show: function(content) {
              $mdToast.show($mdToast.simple().position('right bottom').content($filter('translate')(content)));
            },

            showOnRouteChange: function(content) {
              toast = content;
            }
        };

        return instance;
    }]);
});
