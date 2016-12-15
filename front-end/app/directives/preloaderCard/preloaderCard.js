'use strict'

angular.module('koolikottApp')
.directive('dopPreloaderCard',
function() {
    return {
        scope: false,
        templateUrl: 'directives/preloaderCard/preloaderCard.html'
    };
});
