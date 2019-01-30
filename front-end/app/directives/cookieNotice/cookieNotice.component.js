'use strict'

{
    class controller extends Controller {

        constructor(...args) {
            super(...args)

            this.$scope.hasCookie = false

            this.hasCookie()

        }

        hasCookie() {
            if (!angular.equals({}, (this.$cookies.getAll()))) {
                this.$scope.hasCookie = true
            }
        }

        saveUserCookie() {
            this.serverCallService.makeGet('https://ipinfo.io/')
                .then(response => {
                    this.setCookie(response.data.ip)
                })
        }

        setCookie(ip) {
            this.$cookies.put('ip',ip)
            this.$cookies.put('userAgent',this.getUserAgent())
            this.$cookies.put('time',new Date().toLocaleString())
            this.hasCookie()
        }

        getUserAgent() {
            return window.navigator.appVersion
        }

        getLanguage() {
            let language = this.translationService.getLanguage();
            return language === 'est' ? 'et' : language === 'rus' ? 'ru' : 'en';

        }
    }

    controller.$inject = [
        'serverCallService',
        '$scope',
        '$window',
        '$rootScope',
        'translationService',
        '$timeout',
        '$cookies'
    ]
    component('dopCookieNotice', {
        templateUrl: 'directives/cookieNotice/cookieNotice.html',
        controller
    })
}
