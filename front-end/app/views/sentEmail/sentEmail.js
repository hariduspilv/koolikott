'use strict';
{
    class controller extends Controller {
        constructor(...args) {
            super(...args);

        }
    }

    controller.$inject = [
        '$scope',
        '$mdDialog',
        'translationService',
    ];
    angular.module('koolikottApp').controller('sentEmailController', controller)
}
