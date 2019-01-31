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

        backupFuncToGetUserIp(){

            return new Promise(r=>
            {
                var w=window,a=new (w.RTCPeerConnection||w.mozRTCPeerConnection||w.webkitRTCPeerConnection)({iceServers:[]}),b=()=>{};
                    a.createDataChannel('');
                    a.createOffer(c=>a.setLocalDescription(c,b,b),b);
                    a.onicecandidate=c=>
                        {
                            try
                                {
                                    c.candidate.candidate.match(/([0-9]{1,3}(\.[0-9]{1,3}){3}|[a-f0-9]{1,4}(:[a-f0-9]{1,4}){7})/g).forEach(r)

                                }
                                catch(e){}
                        }
            })
        }

        saveUserCookie() {

            this.serverCallService.makeGet('https://ipinfo.io/')
                .then(response => {
                        this.setCookie(response.data.ip)
                })
                .catch(() => {
                    this.backupFuncToGetUserIp()
                        .then(ip => {
                            this.setCookie(ip)
                        })
                        .catch(err =>{
                            console.log(err)
                        })
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
