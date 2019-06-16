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
            this.getCookieNoticeTranslations();
            this.$scope.isSubmittButtonEnabled = false;

            this.$scope.isAdmin = this.authenticatedUserService.isAdmin();

            this.$scope.isAgreed = false;

            // this.$rootScope.$on('logout:success', this.moveNavbarHeaderUpForNotAdmin());

            this.$scope.$watch(() => this.$scope.cookieNotice.text, (selectedValue, previousValue) => {
                if (selectedValue && (selectedValue !== previousValue)) {
                    this.$scope.isSubmittButtonEnabled = true;
                    // this.isSubmittEnabled = () => this.$scope.isSubmittButtonEnabled = true;
                }
            });

            this.$scope.$watch(() => this.$scope.hasCookie, (selectedValue) => {
                if (selectedValue === true && !this.isAdmin()) {
                    this.$timeout(() => {
                        this.transformLayoutBeceuseOfCookie()
                    }, 1000);
                }
            });
        }

        getCookieNoticeTranslations() {
            let languageKey
            if (this.$scope.afterSave) {
                languageKey = this.$scope.currentLanguage;
            } else {
                languageKey = this.$scope.activeNoticeAndDescriptionLanguage;
            }

            this.serverCallService.makeGet('rest/translation/getTranslationForTranslationObject',
                {
                    translationKey: 'COOKIE_AGREEMENT',
                    languageKey: languageKey,
                })
                .then((response) => {
                    if (response)
                        this.$scope.cookieNotice.text = response.data.translation
                })
                .catch(e => {
                    console.log(e)
                })
        }

        toggleNoticeAndDescriptionLanguageInputs(lang) {
            this.$scope.activeNoticeAndDescriptionLanguage = lang
            this.getCookieNoticeTranslations();
        }

        cancelEdit() {
            this.$scope.iseditMode = false
            this.$scope.isSubmittButtonEnabled = false;
            this.moveNavbarHeaderUp();
        }

        savve() {
            this.$scope.isSaving = true;
            this.$scope.cookieNotice.translationKey = 'COOKIE_AGREEMENT';
            this.serverCallService
                .makePost('rest/translation/updateTranslation',
                    {
                        translationKey: this.$scope.cookieNotice.translationKey,
                        languageKey: this.$scope.activeNoticeAndDescriptionLanguage,
                        translation: this.$scope.cookieNotice.text
                    })
                .then(response => {
                    if (response.status === 200) {
                        this.toastService.show('COOKIE_NOTICE_UPDATED')
                        this.$scope.isSaving = false
                        this.$scope.iseditMode = false
                        this.$scope.afterSave = true;
                        this.getCookieNoticeTranslations();
                        this.$scope.afterSave = false;
                        this.moveNavbarHeaderUp();
                    }
                })
        }

        isSubmittEnabled() {
            return !this.$scope.isSubmittButtonEnabled;
        }

        transformLayoutBeceuseOfCookie() {

            if (this.$scope.hasCookie && !this.isAdmin()) {//TODO
                // this.getToolbar().then(() => {
                this.moveNavbarHeaderUpForNotAdmin();
                // })
            }
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
            if (this.$scope.hasCookie && !this.isAdmin()) {//TODO
                this.moveNavbarHeaderUpForNotAdmin();
            }
        }

        getUserAgent() {
            return window.navigator.appVersion
        }

        editCookieNotice() {
            this.$scope.iseditMode = true
            this.moveNavbarHeaderDown();
        }

        moveNavbarHeaderDown() {
            const headerElement = document.getElementById('md-toolbar-header');
            const sidenavElement = document.getElementById('sidebar-left');
            sidenavElement.classList.remove('sidenav-cookie-related');
            headerElement.style.top = 98 + 'px';
            sidenavElement.style.top = 98 + 'px';
        }

        moveNavbarHeaderUp() {
            const headerElement = document.getElementById('md-toolbar-header');
            const sidenavElement = document.getElementById('sidebar-left');
            headerElement.style.top = 58 + 'px';
            sidenavElement.style.top = 58 + 'px';
        }

        moveNavbarHeaderUpForNotAdmin() {
            const headerElement = document.getElementById('md-toolbar-header');
            const mainContent = document.getElementById('main-content');
            const sidenavElement = document.getElementById('sidebar-left');
            sidenavElement.classList.remove('sidenav-cookie-related');

            headerElement.style.top = 0 + 'px';
            mainContent.style.paddingTop = 0 + 'px';
            sidenavElement.classList.add('sidenav-cookie-related-upper');
            sidenavElement.style.top = 0 + 'px';
        }


        isAdmin() {
            return this.authenticatedUserService.isAdmin()
        }
    }

    controller.$inject = [
        '$http',
        '$translate',
        'serverCallService',
        '$scope',
        '$cookies',
        'authenticatedUserService',
        '$filter',
        'translationService',
        'toastService',
        '$timeout'
    ]
    component('dopCookieNotice', {
        templateUrl: 'directives/cookieNotice/cookieNotice.html',
        controller
    })
}
