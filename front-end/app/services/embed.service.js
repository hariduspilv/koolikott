'use strict'

{
class controller extends Controller {
    getEmbed(link) {
        return link
            ? this.$http.get('https://noembed.com/embed?url=' + link)
            : Promise.reject(
                new Error('Expecting a link as an argument to embedService.getEmbed()')
            )
    }
}
controller.$inject = ['$http']

factory('embedService', controller)
}
