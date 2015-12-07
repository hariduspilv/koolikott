define(['app'], function(app) {
    app.factory('toastService',['$mdToast', '$filter',
        function($mdToast, $filter) {
            return {
                show: function(content) {
                	$mdToast.show($mdToast.simple().position('right top').content($filter('translate')(content)));
                }
            };
        }
    ]);
});
