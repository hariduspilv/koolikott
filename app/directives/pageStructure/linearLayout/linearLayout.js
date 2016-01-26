define(['angularAMD'], function(angularAMD) {
    angularAMD.directive('dopLinearLayout',
        function() {
            return {
                scope: true,
                templateUrl: 'directives/pageStructure/linearLayout/linearLayout.html'
            };
        }
    );
});