'use strict';

angular.module('koolikottApp')
    .component('dopRemainingCharacters', {
        bindings: {
            text: '<',
        },
        templateUrl: 'directives/remainingCharacters/remaining-char.html',
        controller: DopRemainingCharactersController
    });

function DopRemainingCharactersController() {
    let vm = this;

    const MAX_CHARACTERS = 850;

    vm.getRemainingCharacters = function () {
        let remaining = MAX_CHARACTERS - stripHtml(vm.text ? vm.text : "").length;
        return remaining >= 0 ? remaining : 0;
    }
}
