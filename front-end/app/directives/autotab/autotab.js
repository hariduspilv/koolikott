(function() {
    'use strict';

    angular
        .module('auto-tab', [])
        .directive('autoTab', [
            autoTab
        ]);

    function autoTab() {
        return {
            restrict: "A",
            link: function ($scope, element, attrs) {
                let index = parseInt(attrs.tabindex)
                element.on("input", function (e) {
                    if (element.val().length == element.attr("maxlength")) {
                        let next = angular.element(document.body).find('[tabindex=' + (index + 1) + ']')
                        if (next.length) {
                            next.focus();
                        }
                    }
                });
            }
        }
    }
})()
