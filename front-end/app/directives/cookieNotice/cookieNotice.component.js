'use strict'

{
    class controller extends Controller {

        constructor(...args) {
            super(...args)
            this.$scope.hasCookie = false
            this.hasCookie()
            this.isSubmittEnabled()

            this.landingPageLanguages = ['ET', 'EN', 'RU'];
            this.$scope.activeNoticeAndDescriptionLanguage = this.landingPageLanguages[0];

            this.$scope.currentLanguage = this.translationService.getLanguage();
            this.$scope.iseditMode = false;
            this.$scope.cookieNotice = {}
            // this.$scope.cookieNotice.text = this.getTranslation('COOKIE_AGREEMENT');
            this.getCookieNoticeTranslations();
            this.$scope.isSubmittButtonEnabled = false;

            this.$scope.isAdmin = this.authenticatedUserService.isAdmin();

            this.$scope.isAgreed= false;

            this.$scope.$watch(() => this.$scope.cookieNotice.text, (selectedValue, previousValue) => {
                if (selectedValue && (selectedValue !== previousValue)) {
                    this.$scope.isSubmittButtonEnabled = true;
                    // this.isSubmittEnabled = () => this.$scope.isSubmittButtonEnabled = true;
                }
            })
        }

        getCookieNoticeTranslations() {
            this.serverCallService.
            makeGet('rest/translation/getTranslationForTranslationObject',
                {
                // translationKey: this.$scope.cookieNotice.translationKey,
                translationKey: 'COOKIE_AGREEMENT',
                // languageKey: this.$scope.currentLanguage,
                languageKey: this.$scope.activeNoticeAndDescriptionLanguage,
            })
                .then((data) => {
                    if (data)
                        this.$scope.cookieNotice.text = data
                })
                .catch(e => {
                    console.log(e)
                })
        }

        toggleNoticeAndDescriptionLanguageInputs(lang) {
            this.$scope.activeNoticeAndDescriptionLanguage = lang
            this.getCookieNoticeTranslations();
        }

        // isLangFilled(lang) {
        //     let isFilled = false;
        //
        //     Object.keys(this.$scope.noticesAndDescriptions).forEach(key => {
        //         if (lang === key && !!this.$scope.noticesAndDescriptions[key].description) {
        //             isFilled = true;
        //         }
        //     });
        //
        //     return isFilled;
        // }

        cancelEdit() {
            this.$scope.iseditMode = false
            this.$scope.isSubmittButtonEnabled = false;
        }

        save(){
            this.$scope.isSaving = true;
            this.$scope.cookieNotice.translationKey = 'COOKIE_AGREEMENT';
            this.serverCallService
                .makePost('rest/translation/updateTranslation',
                    {
                        translationKey: this.$scope.cookieNotice.translationKey,
                        languageKey: this.$scope.currentLanguage,
                        translation: this.$scope.cookieNotice.text
                    })
                .then(response => {
                    if (response.status === 200) {
                        this.toastService.show('COOKIE_NOTICE_UPDATED')
                        this.$scope.isSaving = false
                        this.$scope.iseditMode = false
                        this.$scope.cookieNotice.text = () => this.getTranslation('COOKIE_AGREEMENT');
                    }
                })
        }

        isSubmittEnabled(){
            return !this.$scope.isSubmittButtonEnabled;
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
            this.$scope.cookieNotice.text = this.getTranslation('COOKIE_AGREEMENT');
            this.$scope.iseditMode = true
        }

        isAdmin() {
            return this.authenticatedUserService.isAdmin()
        }
    }

    controller.$inject = [
        '$translate',
        'serverCallService',
        '$scope',
        '$cookies',
        'authenticatedUserService',
        '$filter',
        'translationService',
        'toastService'
    ]
    component('dopCookieNotice', {
        templateUrl: 'directives/cookieNotice/cookieNotice.html',
        controller
    })
}
