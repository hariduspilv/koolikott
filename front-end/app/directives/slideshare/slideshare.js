'use strict'

{
class controller extends Controller {
    $onInit() {
        this.$scope.width = this.width
        this.$scope.height = this.height

        this.serverCallService
            .makeJsonp(`https://www.slideshare.net/api/oembed/2?url=${this.source}&format=jsonp`)
            .then(({ data }) => {
                if (data) {
                    const embedCode = data.html.match(/(?:embed_code\/key\/)((\w|-)+)(?:\")/)[1]
                    this.$scope.embedLink = 'https://www.slideshare.net/slideshow/embed_code/key/'+embedCode
                }
                else if (typeof this.failCallback === 'function')
                    this.failCallback()
            })
    }
}
controller.$inject = ['$scope', 'serverCallService']

angular.module('koolikottApp').component('dopSlideshare', {
    bindings: {
        source: '<',
        width: '<',
        height: '<',
        failCallback: '&'
    },
    templateUrl: 'directives/slideshare/slideshare.html',
    controller
})
}
