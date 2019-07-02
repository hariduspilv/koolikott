'use strict'

{
    class controller extends Controller {

        constructor(...args) {
            super(...args)
            this.languagess = ['ET', 'EN', 'RU'];
            this.$scope.currentLanguage = this.translationService.getLanguage();

            this.$scope.introPage = {};
            this.$scope.video = {};
            this.$scope.intropageContent = {}
            this.$scope.intropageVideoUrl = {}

            this.getStartPageIntroText();
            this.getAllStartPageIntroTranslations();
            this.maintananceLang();
            this.$scope.editMode = false;
            this.$scope.isSubmittButtonEnabled = false;
            this.$scope.video.url = () => this.getVideoUrl();
            this.$rootScope.$on('logout:success', () => this.$scope.editMode = false)
        }

        maintananceLang() {
            if (this.$scope.currentLanguage)
                this.$scope.maintananceLang = this.convertLanguage(this.$scope.currentLanguage);
        }

        getVideoUrl() {
            return this.$translate.instant('FRONT_PAGE_VIDEO_URL')
        }

        getStartPageIntroText() {
            let languageKey = this.$scope.editMode ? this.$scope.maintananceLang : this.$scope.currentLanguage;

            this.serverCallService.makeGet('rest/translation/getTranslationForTranslationKey',
                {
                    translationKey: 'INTRO_TEXT',
                    languageKey: languageKey,
                })
                .then((response) => {
                    if (response) {
                        this.$scope.introPage.text = response.data
                    }
                })
                .catch(e => {
                    console.log(e)
                })

            this.serverCallService.makeGet('rest/translation/getTranslationForTranslationKey',
                {
                    translationKey: 'FRONT_PAGE_VIDEO_URL',
                    languageKey: languageKey,
                })
                .then((response) => {
                    if (response) {
                        this.$scope.video.url = response.data
                    }
                })
                .catch(e => {
                    console.log(e)
                })
        }

        getAllStartPageIntroTranslations() {
            this.serverCallService.makeGet('rest/translation/getAllTranslations',
                {
                    translationKey: 'INTRO_TEXT',
                })
                .then((response) => {
                    this.$scope.intropageContent.ET = response.data[0];
                    this.$scope.intropageContent.RU = response.data[1];
                    this.$scope.intropageContent.EN = response.data[2];
                })
                .catch(e => {
                    console.log(e)
                })

            this.serverCallService.makeGet('rest/translation/getAllTranslations',
                {
                    translationKey: 'FRONT_PAGE_VIDEO_URL',
                })
                .then((response) => {
                    this.$scope.intropageVideoUrl.ET = response.data[0];
                    this.$scope.intropageVideoUrl.RU = response.data[1];
                    this.$scope.intropageVideoUrl.EN = response.data[2];
                })
                .catch(e => {
                    console.log(e)
                })
        }

        save() {
            this.$scope.isSaving = true;
            this.$scope.introPage.translationKey = 'INTRO_TEXT';
            this.$scope.video.translationKey = 'FRONT_PAGE_VIDEO_URL';

            this.$scope.intropageContent[this.$scope.maintananceLang] = this.$scope.introPage.text;
            this.$scope.intropageVideoUrl[this.$scope.maintananceLang] = this.$scope.video.url;

            const LANGS = Object.keys(this.$scope.intropageContent);
            const VALUES_CONTENT = Object.values(this.$scope.intropageContent);
            const VALUES_URL = Object.values(this.$scope.intropageVideoUrl);

            this.serverCallService
                .makePost('rest/translation/updateTranslations',
                    {
                        translations: VALUES_CONTENT,
                        translationKey: this.$scope.introPage.translationKey,
                        languageKeys: LANGS
                    })
                .then(response => {
                    if (response.status === 200) {
                        this.toastService.show('FRONT_PAGE_CHANGES_SAVED')
                        this.$scope.isSaving = false
                        this.$scope.editMode = false
                        this.getStartPageIntroText();
                        this.$scope.maintananceLang = this.convertLanguage(this.$scope.currentLanguage);
                    }
                })
                .catch(() => this.toastService.show('USER_PROFILE_UPDATE_FAILED', 2000));

            this.serverCallService
                .makePost('rest/translation/updateTranslations',
                    {
                        translations: VALUES_URL,
                        translationKey: this.$scope.video.translationKey,
                        languageKeys: LANGS
                    })
                .then(response => {
                    if (response.status === 200) {
                        this.toastService.show('FRONT_PAGE_CHANGES_SAVED')
                        this.$scope.isSaving = false
                        this.$scope.editMode = false
                        this.getStartPageIntroText();
                        this.$scope.maintananceLang = this.convertLanguage(this.$scope.currentLanguage);
                    }
                })
                .catch(() => this.toastService.show('USER_PROFILE_UPDATE_FAILED', 2000));
        }

        cancelEdit() {
            this.$scope.introPage = {};
            this.$scope.video = {};
            this.$scope.editMode = false
            this.$scope.maintananceLang = this.convertLanguage(this.$scope.currentLanguage);
            this.getAllStartPageIntroTranslations();
            this.getStartPageIntroText();
            this.$scope.isSubmittButtonEnabled = false;
        }

        editIntroPage() {
            this.$scope.editMode = true
        }

        toggleIntroPageLanguageInputs(lang) {
            this.$scope.intropageContent[this.$scope.maintananceLang] = this.$scope.introPage.text;
            this.$scope.intropageVideoUrl[this.$scope.maintananceLang] = this.$scope.video.url;
            this.$scope.maintananceLang = lang
            this.$scope.introPage.text = this.$scope.intropageContent[lang];
            this.$scope.video.url = this.$scope.intropageVideoUrl[lang];
        }

        isSubmittEnabled() {
            return !this.$scope.isSubmittButtonEnabled;
        }

        getTranslation(key) {
            return this.$filter('translate')(key)
        }

        isAdmin() {
            return this.authenticatedUserService.isAdmin()
        }

        getCurrentLanguage() {
            return this.translationService.getLanguage()
        }
    }

    controller.$inject = [
        '$rootScope',
        '$http',
        '$translate',
        'serverCallService',
        '$scope',
        '$cookies',
        'authenticatedUserService',
        '$filter',
        'translationService',
        'toastService'
    ]
    component('dopStartpageIntro', {
        templateUrl: 'directives/startpageIntro/startpageIntro.html',
        controller
    })
}
