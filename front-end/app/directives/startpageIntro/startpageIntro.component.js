'use strict'

{
    class controller extends Controller {

        constructor(...args) {
            super(...args)
            this.languagess = ['ET','RU','EN'];
            this.$scope.activeNoticeAndDescriptionLang = this.languagess[0];
            this.$scope.currentLanguage = this.translationService.getLanguage();

            this.$scope.introPage = {};
            this.$scope.video = {};
            this.$scope.intropageContent = {}
            this.$scope.intropageVideoUrl = {}

            this.getStartPageIntroText();
            this.getAllStartPageIntroTranslations();
            this.$scope.editMode = false;
            this.$scope.isSubmittButtonEnabled = false;
            this.$scope.video.url = () => this.getVideoUrl();
        }

        getVideoUrl() {
            return this.$translate.instant('FRONT_PAGE_VIDEO_URL')
        }

        getStartPageIntroText() {
            let languageKey
            if (!this.$scope.editMode) {
                languageKey = this.translationService.getLanguage();
            } else {
                languageKey = this.$scope.activeNoticeAndDescriptionLang;
            }

            this.serverCallService.makeGet('rest/translation/getTranslationForTranslationObject',
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

            this.serverCallService.makeGet('rest/translation/getTranslationForTranslationObject',
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
                    if (response) {
                        this.languagess.forEach((key, i) => {
                            this.$scope.intropageContent[key] = response.data[i]
                        });
                    }
                })
                .catch(e => {
                    console.log(e)
                })

            this.serverCallService.makeGet('rest/translation/getAllTranslations',
                {
                    translationKey: 'FRONT_PAGE_VIDEO_URL',
                })
                .then((response) => {
                    if (response) {
                        this.languagess.forEach((key, i) => {
                            this.$scope.intropageVideoUrl[key] = response.data[i]
                        });
                    }
                })
                .catch(e => {
                    console.log(e)
                })
        }

        save(){
            this.$scope.isSaving = true;
            this.$scope.introPage.translationKey = 'INTRO_TEXT';
            this.$scope.video.translationKey = 'FRONT_PAGE_VIDEO_URL';

            this.$scope.intropageContent[this.$scope.activeNoticeAndDescriptionLang] = this.$scope.introPage.text;
            this.$scope.intropageVideoUrl[this.$scope.activeNoticeAndDescriptionLang] = this.$scope.video.url;

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
                    }
                }).catch(() => this.toastService.show('USER_PROFILE_UPDATE_FAILED', 2000));

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
                    }
                })
                .catch(() => this.toastService.show('USER_PROFILE_UPDATE_FAILED', 2000));
        }

        cancelEdit() {
            this.$scope.introPage = {};
            this.$scope.video = {};
            this.$scope.editMode = false
            this.$scope.activeNoticeAndDescriptionLang = this.languagess[0];
            this.getAllStartPageIntroTranslations();
            this.getStartPageIntroText();
            this.$scope.isSubmittButtonEnabled = false;
        }

        editIntroPage() {
            this.$scope.editMode = true
        }

        toggleIntroPageLanguageInputs(lang) {
            this.$scope.intropageContent[this.$scope.activeNoticeAndDescriptionLang] = this.$scope.introPage.text;
            this.$scope.intropageVideoUrl[this.$scope.activeNoticeAndDescriptionLang] = this.$scope.video.url;
            this.$scope.activeNoticeAndDescriptionLang = lang
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
