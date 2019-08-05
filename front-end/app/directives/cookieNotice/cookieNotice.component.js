'use strict'

{
    class controller extends Controller {

        constructor(...args) {
            super(...args)
            this.$scope.hasCookie = false
            this.hasCookie()
            this.isSubmitEnabled()

            this.landingPageLanguages = ['ET', 'EN', 'RU'];
            this.$scope.currentLanguage = this.translationService.getLanguage();

            this.$scope.isEditMode = false;
            this.$scope.cookieNotice = {}
            this.$scope.cookie = {}

            this.getCookieNoticeText();
            this.getTranslationsForAllLanguages();
            this.maintananceLang();

            this.$scope.isSubmittButtonEnabled = false;
            this.$scope.isAgreed = false;
            this.$scope.afterSave = false;

            this.$scope.isAdmin = this.authenticatedUserService.isAdmin();

            this.$rootScope.$on('logout:success', this.moveNavbarHeaderUpForNotAdmin.bind(this));
            this.$rootScope.$on('cookie:showCookieNotice', this.$timeout(() => {
                this.moveNavbarHeaderUp()
                this.moveDetailedSearchUp()
                this.moveAddMaterialsToolbarUp()
            }, 1000));

            this.$scope.$watch(() => this.$scope.cookieNotice.text, (selectedValue, previousValue) => {
                if (selectedValue && (selectedValue !== previousValue)) {
                    this.$scope.isSubmittButtonEnabled = true;
                }
            });
        }

        maintananceLang() {
            if (this.$scope.currentLanguage)
                this.$scope.maintenanceLanguage = this.convertLanguage(this.$scope.currentLanguage);
        }

        getTranslationsForAllLanguages() {
            this.serverCallService.makeGet('rest/translation/getAllTranslations',
                {
                    translationKey: 'COOKIE_AGREEMENT',
                })
                .then((response) => {
                    if (response) {
                        this.$scope.cookie.ET = response.data[0];
                        this.$scope.cookie.RU = response.data[1];
                        this.$scope.cookie.EN = response.data[2];
                    }
                })
                .catch(e => {
                    console.log(e)
                });
        }

        getTranslationByKey(lang) {
            this.serverCallService.makeGet('rest/translation/getTranslationForTranslationKey',
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
            let languageKey = this.$scope.isEditMode ? this.$scope.maintenanceLanguage : this.$scope.currentLanguage;
            this.getTranslationByKey(languageKey);
        }

        toggleNoticeAndDescriptionLanguageInputs(lang) {
            this.$scope.cookie[this.$scope.maintenanceLanguage] = this.$scope.cookieNotice.text;
            this.$scope.maintenanceLanguage = lang;
            this.$scope.cookieNotice.text = this.$scope.cookie[lang];
        }

        cancelEdit() {
            this.$scope.cookie = {}
            this.$scope.isEditMode = false
            this.getTranslationsForAllLanguages();
            this.$scope.maintenanceLanguage = this.convertLanguage(this.$scope.currentLanguage);
            this.getCookieNoticeText()
            this.$scope.isSubmittButtonEnabled = false;
            this.moveNavbarHeaderUp();
            this.moveDetailedSearchUp();
            this.moveAddMaterialsToolbarUp();
        }

        save() {
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
                        this.$scope.isEditMode = false
                        this.$scope.afterSave = true;
                        this.getTranslationsForAllLanguages();
                        this.getCookieNoticeText()
                        this.$scope.maintenanceLanguage = this.convertLanguage(this.$scope.currentLanguage);
                        this.$scope.afterSave = false;
                        this.moveNavbarHeaderUp();
                        this.moveDetailedSearchUp()
                        this.moveAddMaterialsToolbarUp()
                        this.$scope.isSubmittButtonEnabled = false
                    }
                })
                .catch(() => {
                    this.toastService.show('COOKIE_NOTICE_UPDATED_FAILED', 2000)
                    this.$scope.isSaving = false
                })
        }

        isSubmitEnabled() {
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
            this.moveDetailedSearchTop();
            this.moveNavBarHeaderTop();
            this.moveAddMaterialsToolbarTop();
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

        showCookieNotice() {
            return (!this.$scope.hasCookie && !this.$scope.isEditMode) || !this.$scope.isEditMode
        }

        isAdmin() {
            return this.authenticatedUserService.isAdmin()
        }

        getUserAgent() {
            return window.navigator.appVersion
        }

        editCookieNotice() {
            this.$scope.isEditMode = true
            this.moveNavbarHeaderDown();
            this.moveDetailedSearchDown();
            this.moveAddMaterialsToolbarDown();
        }

        moveNavbarHeaderDown() {
            const headerElement = document.getElementById('md-toolbar-header');
            const sidenavElement = document.getElementById('sidebar-left');
            sidenavElement.classList.remove('sidenav-cookie-related');
            headerElement.style.top = 98 + 'px';
            sidenavElement.style.top = 98 + 'px';
        }
        moveDetailedSearchDown() {
            const detailedSearchElement = document.getElementById('detailedSearch');
            detailedSearchElement.style.top = 98 + 'px';
        }
        moveAddMaterialsToolbarDown() {
            const addMaterialsToolbarElement = document.getElementById('addMaterialsToolbar');
            if (addMaterialsToolbarElement !== null)
                addMaterialsToolbarElement.style.top = 98 + 'px';
        }
        moveNavbarHeaderUp() {
            const headerElement = document.getElementById('md-toolbar-header');
            const sidenavElement = document.getElementById('sidebar-left');
            sidenavElement.classList.remove('sidenav-cookie-related-upper');
            headerElement.style.top = 58 + 'px';
            sidenavElement.style.top = 58 + 'px';
        }
        moveDetailedSearchUp() {
            const detailedSearchElement = document.getElementById('detailedSearch');
            detailedSearchElement.style.top = 58 + 'px';
        }
        moveAddMaterialsToolbarUp() {
            const addMaterialsToolbarElement = document.getElementById('addMaterialsToolbar');
            if (addMaterialsToolbarElement !== null)
                addMaterialsToolbarElement.style.top = 58 + 'px';
        }
        moveAddMaterialsToolbarTop() {
            const addMaterialsToolbarElement = document.getElementById('addMaterialsToolbar');
            if (addMaterialsToolbarElement !== null)
                addMaterialsToolbarElement.style.top = '0';
        }
        moveNavBarHeaderTop() {
            const headerElement = document.getElementById('md-toolbar-header');
            const sidenavElement = document.getElementById('sidebar-left');
            sidenavElement.classList.remove('sidenav-cookie-related-upper');
            headerElement.style.top = '0';
            sidenavElement.style.top = '0';
        }
        moveDetailedSearchTop() {
            const detailedSearchElement = document.getElementById('detailedSearch');
            if (detailedSearchElement !== null)
                detailedSearchElement.style.top = '0';
        }
        moveNavbarHeaderUpForNotAdmin() {
            const headerElement = document.getElementById('md-toolbar-header');
            const mainContent = document.getElementById('main-content');
            const sidenavElement = document.getElementById('sidebar-left');
            sidenavElement.classList.remove('sidenav-cookie-related');

            if (this.hasCookie()) {
                this.$rootScope.showCookieBanner = false
                headerElement.style.top = 0 + 'px';
                // mainContent.style.paddingTop = 0 + 'px';
                sidenavElement.classList.add('sidenav-cookie-related-upper');
                sidenavElement.style.top = 0 + 'px';
            }
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
