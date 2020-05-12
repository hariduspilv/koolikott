'use strict'

{
const MAX_CHARACTERS = 850

class controller extends Controller {
    getRemainingCharacters() {
        if (typeof this.text !== 'string')
            return MAX_CHARACTERS

        // max char - text length - newlines
        let remaining =
            MAX_CHARACTERS -
            this.stripHtml(this.text).length/* -
            this.countOccurrences('<br>', this.text)*/

        return remaining >= 0 ? remaining : 0;
    }
    countOccurrences(value, text) {
        let count = 0
        let index = text.indexOf(value)

        while (index !== -1) {
            count++
            index = text.indexOf(value, index + 1)
        }

        return count
    }
}
controller.$inject = ['$scope']
component('dopRemainingCharacters', {
    bindings: {
        text: '<',
    },
    templateUrl: '/directives/remainingCharacters/remaining-char.html',
    controller
})
}
