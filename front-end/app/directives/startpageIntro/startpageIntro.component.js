'use strict'

{
    class controller extends Controller {

        constructor(...args) {
            super(...args)
            // this.isSubmittEnabled()
            this.$scope.languagess = ['ET', 'EN', 'RU'];
            this.$scope.activeNoticeAndDescriptionLang = this.$scope.languagess[0];

            this.$scope.introPage = {};

            // this.$scope.introPage.text = '';
            this.getStartPageIntroTranslationss();

            this.$scope.currentLanguage = this.translationService.getLanguage();
            this.$scope.editMode = false;
            this.$scope.isSubmittButtonEnabled = false;
            this.$scope.video = {};
            this.$scope.video.url = 'https://www.youtube.com/watch?v=8kVI621fZug';
            this.$scope.video.textUrl = 'www.youtube.com';
            this.$scope.video.createdAt = '01.08.2018';

            // this.$scope.isAdmin = this.authenticatedUserService.isAdmin();
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
        }

        save(){
            this.$scope.isSaving = true;
            this.$scope.cookieNotice.translationKey = 'INTRO_TEXT';
            this.serverCallService
                .makePost('rest/translation/updateTranslation',
                    {
                        translationKey: this.$scope.introPage.translationKey,
                        languageKey: this.$scope.currentLanguage,
                        translation: this.$scope.introPage.text
                    })
                .then(response => {
                    if (response.status === 200) {
                        this.toastService.show('COOKIE_NOTICE_UPDATED')
                        this.$scope.isSaving = false
                        this.$scope.editMode = false
                        // this.$scope.cookieNotice.text = () => this.getTranslation('COOKIE_AGREEMENT'); // miks ei tööta
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

        isCreateDialogOpen() {
            return this.createDialogOpen
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
