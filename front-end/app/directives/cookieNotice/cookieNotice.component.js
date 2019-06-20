'use strict'

{
    class controller extends Controller {

        constructor(...args) {
            super(...args)
            this.$scope.hasCookie = false
            this.hasCookie()
            this.isSubmittEnabled()

            this.landingPageLanguages = ['ET', 'RU', 'EN',];
            this.$scope.maintenanceLanguage = this.landingPageLanguages[0];
            this.$scope.currentLanguage = this.translationService.getLanguage();

            this.$scope.iseditMode = false;
            this.$scope.cookieNotice = {}
            this.$scope.cookie = {}

            this.getCookieNoticeText();
            this.getTranslationsForAllLanguages();

            this.$scope.isSubmittButtonEnabled = false;
            this.$scope.isAgreed = false;
            this.$scope.afterSave = false;

            this.$scope.isAdmin = this.authenticatedUserService.isAdmin();

            this.$rootScope.$on('logout:success', this.moveNavbarHeaderUpForNotAdmin.bind(this));
            this.$rootScope.$on('cookie:showCookieNotice', this.$timeout(() => {
                this.moveNavbarHeaderUp()
            }, 1000));

            this.$scope.$watch(() => this.$scope.cookieNotice.text, (selectedValue, previousValue) => {
                if (selectedValue && (selectedValue !== previousValue)) {
                    this.$scope.isSubmittButtonEnabled = true;
                }
            });
        }

        getTranslationsForAllLanguages() {
            this.serverCallService.makeGet('rest/translation/getAllTranslations',
                {
                    translationKey: 'COOKIE_AGREEMENT',
                })
                .then((response) => {
                    if (response) {
                        this.landingPageLanguages.forEach((key, i) => {
                            this.$scope.cookie[key] = response.data[i]
                        });
                    }
                })
                .catch(e => {
                    console.log(e)
                });
        }

        translationFunction(lang) {
            this.serverCallService.makeGet('rest/translation/getTranslationForTranslationObject',
                {
                    translationKey: 'COOKIE_AGREEMENT',
                    languageKey: lang,
                })
                .then((response) => {
                    if (response)
                        this.$scope.cookieNotice.text = response.data
                })
                .catch(e => {
                    console.log(e)
                })
        }

        getCookieNoticeText() {
            let languageKey
            if (!this.$scope.iseditMode) {
                languageKey = this.$scope.currentLanguage;
            } else {
                languageKey = this.$scope.maintenanceLanguage;
            }
            this.translationFunction(languageKey);
        }

        toggleNoticeAndDescriptionLanguageInputs(lang) {
            this.$scope.cookie[this.$scope.maintenanceLanguage] = this.$scope.cookieNotice.text;
            this.$scope.maintenanceLanguage = lang;
            this.$scope.cookieNotice.text = this.$scope.cookie[lang];
        }

        cancelEdit() {
            this.$scope.cookie = {}
            this.$scope.iseditMode = false
            this.$scope.maintenanceLanguage = this.landingPageLanguages[0];
            this.getTranslationsForAllLanguages();
            this.translationFunction(this.landingPageLanguages[0]);
            this.$scope.isSubmittButtonEnabled = false;
            this.moveNavbarHeaderUp();
        }

        savve() {
            this.$scope.isSaving = true;
            this.$scope.cookieNotice.translationKey = 'COOKIE_AGREEMENT';
            this.$scope.cookie[this.$scope.maintenanceLanguage] = this.$scope.cookieNotice.text;

            const LANGS = Object.keys(this.$scope.cookie);
            const VALUES = Object.values(this.$scope.cookie);

            this.serverCallService
                .makePost('rest/translation/updateTranslations',
                    {
                        translations: VALUES,
                        translationKey: this.$scope.cookieNotice.translationKey,
                        languageKeys: LANGS
                    })
                .then(response => {
                    if (response.status === 200) {
                        this.toastService.show('COOKIE_NOTICE_UPDATED')
                        this.$scope.isSaving = false
                        this.$scope.iseditMode = false
                        this.$scope.afterSave = true;
                        this.getTranslationsForAllLanguages();
                        this.$scope.afterSave = false;
                        this.moveNavbarHeaderUp();
                    }
                })
                .catch(() => this.toastService.show('USER_PROFILE_UPDATE_FAILED', 2000));
        }

        isSubmittEnabled() {
            return !this.$scope.isSubmittButtonEnabled;
        }

        hasCookie() {
            this.$scope.hasCookie = !!this.$cookies.get('userAgent');
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
            if (this.$scope.hasCookie && !this.isAdmin()) {
                this.moveNavbarHeaderUpForNotAdmin();
            }
        }

        isAdmin() {
            return this.authenticatedUserService.isAdmin()
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
            sidenavElement.classList.remove('sidenav-cookie-related-upper');
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
    }

    controller.$inject = [
        '$http',
        '$translate',
        '$rootScope',
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
