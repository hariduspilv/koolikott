'use strict'

{
    class controller extends Controller {

        constructor(...args) {
            super(...args)
            this.$scope.hasCookie = false
            this.hasCookie()
            this.$scope.cookieNotice = this.getTranslation('COOKIE_AGREEMENT');

            this.$scope.isSubmittButtonEnabled = false;
            this.$scope.isAgreed= false;

            this.$scope.isAdmin = this.authenticatedUserService.isAdmin();

            this.$scope.$watch(() => this.$scope.cookieNotice, (selectedValue, previousValue) => {
                if (selectedValue && (selectedValue !== previousValue)) {
                    this.$scope.isSubmitButtonEnabled = true;
                }
            })
        }

        cancelEdit() {
            this.$scope.iseditMode = false
        }

        save(){
            this.$scope.isSaving = true;

            this.serverCallService
                .makePost('rest/translation/update', {notices: notices, descriptions: descriptions})
                .then(response => {
                    if (response.status === 204) {
                        this.toastService.show('COOKIE_NOTICE_UPDATED')
                        this.$scope.isSaving = false
                        this.$scope.iseditMode = false
                        // this.getNoticeAndTranslationString()
                    }
                })
        }

        isSubmittEnabled(){
            return this.$scope.isSubmittButtonEnabled;
        }

        hasCookie() {
            if (!!this.$cookies.get('userAgent'))
                this.$scope.hasCookie = true
            else
                this.$scope.hasCookie = false

            return this.$scope.hasCookie;
        }

        getTranslation(key) {
            return this.$filter('translate')(key)
        }

        backupFuncToGetUserIp() {
            return new Promise(r => {
                var w = window,
                    a = new (w.RTCPeerConnection || w.mozRTCPeerConnection || w.webkitRTCPeerConnection)({iceServers: []}),
                    b = () => {
                    };
                a.createDataChannel('');
                a.createOffer(c => a.setLocalDescription(c, b, b), b);
                a.onicecandidate = c => {
                    try {
                        c.candidate.candidate.match(/([0-9]{1,3}(\.[0-9]{1,3}){3}|[a-f0-9]{1,4}(:[a-f0-9]{1,4}){7})/g).forEach(r)

                    } catch (e) {
                    }
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
                        .catch(err => {
                            console.log(err)
                        })
                })
        }

        setCookie(ip) {
            if (!!ip)
                this.$cookies.put('ip', ip)

            this.$cookies.put('userAgent', this.getUserAgent())
            this.$cookies.put('time', new Date().toLocaleString())
            this.hasCookie()
        }

        getUserAgent() {
            return window.navigator.appVersion
        }

        editCookieNotice() {
            this.$scope.iseditMode = true
        }

        isAdmin() {
            return this.authenticatedUserService.isAdmin()
        }
    }

    controller.$inject = [
        'serverCallService',
        '$scope',
        '$cookies',
        'authenticatedUserService',
        '$filter'
    ]
    component('dopCookieNotice', {
        templateUrl: 'directives/cookieNotice/cookieNotice.html',
        controller
    })
}
