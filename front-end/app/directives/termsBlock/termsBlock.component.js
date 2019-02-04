'use strict'

{
    const ICON_SVG_CONTENTS = {
        h3: '<path d="M20,14.3400002 L21.4736328,14.34375 C22.5810602,14.34375 23.1347656,13.8017632 23.1347656,12.7177734 C23.1347656,12.2958963 23.002931,11.9516615 22.7392578,11.6850586 C22.4755846,11.4184557 22.103518,11.2851562 21.6230469,11.2851562 C21.2304668,11.2851562 20.8891616,11.3994129 20.5991211,11.6279297 C20.3090806,11.8564465 20.1640625,12.1406233 20.1640625,12.4804688 L17.2021484,12.4804688 C17.2021484,11.8066373 17.3896466,11.2060573 17.7646484,10.6787109 C18.1396503,10.1513646 18.6596646,9.73974757 19.324707,9.44384766 C19.9897494,9.14794774 20.7206991,9 21.5175781,9 C22.9414134,9 24.0605428,9.32519206 24.875,9.97558594 C25.6894572,10.6259798 26.0966797,11.5195256 26.0966797,12.65625 C26.0966797,13.207034 25.9282243,13.7270483 25.5913086,14.2163086 C25.2543928,14.7055689 24.7636751,15.1025375 24.1191406,15.4072266 C24.7988315,15.6533215 25.3320293,16.026853 25.71875,16.527832 C26.1054707,17.0288111 26.2988281,17.6484338 26.2988281,18.3867188 C26.2988281,19.5293026 25.8593794,20.4433559 24.9804688,21.1289062 C24.1015581,21.8144566 22.9472728,22.1572266 21.5175781,22.1572266 C20.6796833,22.1572266 19.9018591,21.9975602 19.184082,21.6782227 C18.466305,21.3588851 17.9228534,20.9165067 17.5537109,20.3510742 C17.1845685,19.7856417 17,19.1425817 17,18.421875 L19.9794922,18.421875 C19.9794922,18.8144551 20.1376937,19.1542954 20.4541016,19.4414062 C20.7705094,19.7285171 21.1601539,19.8720703 21.6230469,19.8720703 C22.1445339,19.8720703 22.5605453,19.7270522 22.8710938,19.4370117 C23.1816422,19.1469712 23.3369141,18.7763694 23.3369141,18.3251953 C23.3369141,17.6806608 23.1757829,17.2236342 22.8535156,16.9541016 C22.5312484,16.684569 22.0859403,16.5498047 21.5175781,16.5498047 L20,16.5498047 L20,14.3400002 Z M16,22 L13,22 L13,17 L9,17 L9,22 L6,22 L6,9 L9,9 L9,14 L13,14 L13,9 L16,9 L16,22 Z"></path>',
        p: '<path d="M20.9816895,17.2968826 C20.9816895,18.7734525 20.6491732,19.9526399 19.9841309,20.8344803 C19.3190885,21.7163206 18.2460994,22.1715698 17.1152344,22.1715698 C16.2421831,22.1715698 15.5625028,21.8489164 15,21.2102413 L15,25 L12,25 L12,11.9957809 L14.7685547,11.9957809 L14.8599997,12.8699999 C15.4283619,12.1668714 16.1718704,11.8199997 17.0976562,11.8199997 C18.2695371,11.8199997 19.3557096,12.2377886 20.0061035,13.1020508 C20.6564974,13.9663129 20.9816895,15.1542893 20.9816895,16.6660156 L20.9816895,17.2968826 Z M18.0197754,16.6220703 C18.0197754,14.9345619 17.352544,14.1051559 16.3681641,14.1051559 C15.6650355,14.1051559 15.2343762,14.3571066 15,14.8610153 L15,19.0953979 C15.2578138,19.6227443 15.6943325,19.8864136 16.3857422,19.8864136 C17.3291063,19.8864136 17.9904784,19.057633 18.0197754,17.4287186 L18.0197754,16.6220703 Z"></path>',
        anchor: '<path d="M15,12 L13,12 C10.790861,12 9,13.790861 9,16 C9,18.209139 10.790861,20 13,20 L15,20 L15,22 L17,22 L17,20 L19,20 C21.209139,20 23,18.209139 23,16 C23,13.790861 21.209139,12 19,12 L17,12 L17,10 L15,10 L15,12 Z M13,10 L19,10 C22.3137085,10 25,12.6862915 25,16 C25,19.3137085 22.3137085,22 19,22 L13,22 C9.6862915,22 7,19.3137085 7,16 C7,12.6862915 9.6862915,10 13,10 Z M12,15 L20,15 L20,17 L12,17 L12,15 Z"></path>',
        bold: '<path d="M19.6,15.79 C20.57,15.12 21.25,14.02 21.25,13 C21.25,10.74 19.5,9 17.25,9 L11,9 L11,23 L18.04,23 C20.13,23 21.75,21.3 21.75,19.21 C21.75,17.69 20.89,16.39 19.6,15.79 L19.6,15.79 Z M14,11.5 L17,11.5 C17.83,11.5 18.5,12.17 18.5,13 C18.5,13.83 17.83,14.5 17,14.5 L14,14.5 L14,11.5 Z M17.5,20.5 L14,20.5 L14,17.5 L17.5,17.5 C18.33,17.5 19,18.17 19,19 C19,19.83 18.33,20.5 17.5,20.5 Z"></path>',
        italic: '<polygon points="14 9 14 12 16.21 12 12.79 20 10 20 10 23 18 23 18 20 15.79 20 19.21 12 22 12 22 9"></polygon>',
        quote: '<path d="M10,21 L13,21 L15,17 L15,11 L9,11 L9,17 L12,17 L10,21 Z M18,21 L21,21 L23,17 L23,11 L17,11 L17,17 L20,17 L18,21 Z"></path>',
        unorderedlist: '<path d="M8,14.5 C7.17,14.5 6.5,15.17 6.5,16 C6.5,16.83 7.17,17.5 8,17.5 C8.83,17.5 9.5,16.83 9.5,16 C9.5,15.17 8.83,14.5 8,14.5 Z M8,8.5 C7.17,8.5 6.5,9.17 6.5,10 C6.5,10.83 7.17,11.5 8,11.5 C8.83,11.5 9.5,10.83 9.5,10 C9.5,9.17 8.83,8.5 8,8.5 Z M8,20.5 C7.17,20.5 6.5,21.18 6.5,22 C6.5,22.82 7.18,23.5 8,23.5 C8.82,23.5 9.5,22.82 9.5,22 C9.5,21.18 8.83,20.5 8,20.5 Z M11,23 L25,23 L25,21 L11,21 L11,23 Z M11,17 L25,17 L25,15 L11,15 L11,17 Z M11,9 L11,11 L25,11 L25,9 L11,9 Z"/>',
        orderedlist: '<path xmlns="http://www.w3.org/2000/svg" d="M2 17h2v.5H3v1h1v.5H2v1h3v-4H2v1zm1-9h1V4H2v1h1v3zm-1 3h1.8L2 13.1v.9h3v-1H3.2L5 10.9V10H2v1zm5-6v2h14V5H7zm0 14h14v-2H7v2zm0-6h14v-2H7v2z"/>'
    }
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.termLanguages = ['ET', 'EN', 'RU']
            this.$scope.activeTermLanguages = this.termLanguages[0]
            this.getCurrentLanguage()
            this.$scope.notifyOfGDPRUpdate = false
        }

        notifyOfGDPRUpdate() {
            this.$scope.notifyOfGDPRUpdate = !this.$scope.notifyOfGDPRUpdate
        }

        toggleFaqLanguageInputs(term, lang) {
            term.activeTermLanguage = lang
        }

        isLangFilled(lang, term) {
            let isFilled = false;

            if ((lang === 'ET') && !!(term.titleEst && term.contentEst))
                isFilled = true

            if ((lang === 'EN') && !!(term.titleEng && term.contentEng))
                isFilled = true

            if ((lang === 'RU') && !!(term.titleRus && term.contentRus))
                isFilled = true

            return isFilled;
        }

        save(term) {
            this.$scope.isSaving = true

            if (this.$scope.notifyOfGDPRUpdate)
                this.createAgreement()

            this.termsService.saveTerm(term)
                .then(response => {
                    if (response.status === 200) {
                        this.createDialogOpen = false
                        this.$scope.isSaving = false
                        term.edit = !term.edit
                        this.getTerms()
                        this.toastService.show('TERMS_SAVED')
                    } else {
                        this.$scope.isSaving = false
                        this.toastService.show('TERMS_SAVE_FAILED')
                    }
                })
            this.$scope.isSaving = false
        }

        getCurrentLanguage() {
            return this.translationService.getLanguage()
        }

        isTermsEditMode() {
            return this.editMode
        }

        isCreateDialogOpen() {
            return this.createDialogOpen
        }

        editTerm(term) {
            this.createDialogOpen = !this.createDialogOpen
            term.edit = !term.edit;
        }

        cancelEdit(term) {
            if (term.new) {
                this.removeTerm()
            } else {
                term.edit = !term.edit;
            }
            this.createDialogOpen = false
            this.getTerms()
        }

        delete(term) {
            this.dialogService.showDeleteConfirmationDialog(
                'ARE_YOU_SURE_DELETE',
                '',
                () => this.termsService.deleteTerm(term)
                    .then(() => {
                        this.getTerms()
                        this.toastService.show('TERM_DELETED')
                        this.createDialogOpen = false
                    })
            )
        }

        isSubmitDisabled(term) {
            return !(term.titleEst && term.titleEng &&
                term.titleRus && term.contentEst &&
                term.contentEng && term.contentRus)

        }

        createAgreement() {
            this.serverCallService
                .makePost('rest/admin/agreement', {url: '/terms', version: 1, validFrom: new Date})
                .then((response) => {
                    if (response.status === 200) {
                        console.log('agreement added')
                    }
                })
        }
    }

    controller.$inject = [
        '$scope',
        'dialogService',
        'serverCallService',
        'translationService',
        'searchService',
        'termsService',
        'toastService',
    ]
    component('dopTermsBlock', {
        bindings: {
            terms: '<',
            editMode: '<',
            removeTerm: '&',
            getTerms: '&',
            createDialogOpen: '='
        },
        templateUrl: 'directives/termsBlock/termsBlock.html',
        controller
    })
}
