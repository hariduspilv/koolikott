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
        let text = vm.text ? vm.text : "";
        // max char - text length - newlines
        let remaining = MAX_CHARACTERS - stripHtml(text).length - countOccurrences('</p>', text);
        return remaining >= 0 ? remaining : 0;
    }
}
