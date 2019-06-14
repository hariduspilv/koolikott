'use strict'

{
    class controller extends Controller {

        constructor(...args) {
            super(...args)
            // this.isSubmittEnabled()
            this.languagess = ['ET', 'EN', 'RU'];
            this.$scope.activeNoticeAndDescriptionLang = this.languagess[0];

            this.$scope.introPage = {};
            this.getStartPageIntroTranslationss();
            this.$scope.currentLanguage = this.translationService.getLanguage();
            this.$scope.editMode = false;
            this.$scope.isSubmittButtonEnabled = false;
            this.$scope.video = {};
            this.$scope.video.url = () => this.getVideoUrl();
            // this.$scope.isAdmin = this.authenticatedUserService.isAdmin();
        }

        getVideoUrl() {
            return this.$translate.instant(
                'FRONT_PAGE_VIDEO_URL'
            )
        }

        getStartPageIntroTranslationss() {
            this.serverCallService.makeGet('rest/translation/getTranslationForTranslationObject',
                {
                    translationKey: 'INTRO_TEXT',
                    languageKey: this.$scope.activeNoticeAndDescriptionLang,
                })
                .then((response) => {
                    if (response)
                        this.$scope.introPage.text = response.data
                })
                .catch(e => {
                    console.log(e)
                })

            this.serverCallService.makeGet('rest/translation/getTranslationForTranslationObject',
                {
                    translationKey: 'FRONT_PAGE_VIDEO_URL',
                    languageKey: this.$scope.activeNoticeAndDescriptionLang,
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

        save(){
            this.$scope.isSaving = true;
            this.$scope.introPage.translationKey = 'INTRO_TEXT';
            this.$scope.video.translationKey = 'FRONT_PAGE_VIDEO_URL';
            this.serverCallService
                .makePost('rest/translation/updateTranslation',
                    {
                        translationKey: this.$scope.introPage.translationKey,
                        languageKey: this.$scope.activeNoticeAndDescriptionLang,
                        translation: this.$scope.introPage.text
                    })
                .then(response => {
                    if (response.status === 200) {
                        this.toastService.show('COOKIE_NOTICE_UPDATED')
                        this.$scope.isSaving = false
                        this.$scope.editMode = false
                    }
                })
            this.serverCallService
                .makePost('rest/translation/updateTranslation',
                    {
                        translationKey: this.$scope.video.translationKey ,
                        languageKey: this.$scope.activeNoticeAndDescriptionLang,
                        translation: this.$scope.video.url
                    })
                .then(response => {
                    if (response.status === 200) {
                        this.toastService.show('COOKIE_NOTICE_UPDATED')
                        this.$scope.isSaving = false
                        this.$scope.editMode = false
                    }
                })
        }

        cancelEdit() {
            this.$scope.editMode = false
            this.$scope.isSubmittButtonEnabled = false;
        }

        editIntroPage() {
            this.$scope.editMode = true
        }

        toggleNoticeAndDescriptionLanguageInputss(lang) {
            this.$scope.activeNoticeAndDescriptionLang = lang
            this.getStartPageIntroTranslationss();
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
