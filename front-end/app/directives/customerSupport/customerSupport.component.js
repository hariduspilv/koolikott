'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.validEmail = VALID_EMAIL
            this.$scope.customerSupport = {}
            this.$scope.captchaKey = ''
            this.getCaptchaKey()

            this.$scope.files = []
            this.$scope.ngfFiles = []

            this.$scope.customerSupportTitle = false

            this.$scope.isFileBtnVisible = false
            this.$scope.fileSizeTogether = 0
            this.$scope.filesCount = 0

            this.getUserManualTitles()
            this.$rootScope.$on('logout:success', this.clearData.bind(this));

            this.$scope.$watch('customerSupport.subject', (selectedValue, previousValue) => {
                if (selectedValue && selectedValue !== previousValue) {
                    this.handleSelectChange(this.$scope.customerSupport.subject)
                    this.$scope.showUserManualsHelped = false
                }
            })
        }

        clearData() {
            this.$scope.customerSupport = {}
            this.$scope.showCustomerSupportInput = false
        }

        setResponse() {
            this.$translate('CUSTOMER_SUPPORT_WILL_SEND_RESPONSE').then((value) => {
                this.$scope.finalresponse = (value.replace('${email}', this.$scope.customerSupport.email));
            })
        }

        isUserModeratorOrAdmin() {
            return this.authenticatedUserService.isModeratorOrAdmin()
        }

        openNewTab() {
            this.$window.open(window.location.origin + '/usermanuals', '_blank')
            this.$scope.showUserManualsHelped = true
            this.$scope.allowDialogClose = false
        }

        getUserManualTitles() {
            this.userManualsAdminService.getUserManuals()
                .then(response => {
                    this.$scope.titles = response.data.map(um => {
                        return um.title
                    })
                    this.$scope.titles.push('Muu')
                })
        }

        canAddMoreFile() {

            return !(this.$scope.tooManyFiles || this.$scope.fileSizeTooLarge)
        }

        remove(fileToRemove) {
            this.$scope.files.forEach((chipFile, index) => {
                if (chipFile.name === fileToRemove.name) {
                    this.$scope.files.splice(index, 1)
                }
            });
            this.$scope.ngfFiles.forEach((chipFile, index) => {
                if (chipFile.name === fileToRemove.name) {
                    this.$scope.ngfFiles.splice(index, 1)
                }
            });

            this.validateAttachments(this.$scope.files);
        }

        validateAttachments(files) {
            this.$scope.fileSizeTooLarge = false
            this.$scope.tooManyFiles = false

            if (files.length > 3) {
                this.$scope.tooManyFiles = true
            }

            this.$scope.fileSizeTogether = files.map(item => item.size)
                .reduce((prev, next) => prev + next, 0) / 1024 / 1024;

            if (this.$scope.fileSizeTogether > 10) {
                this.$scope.fileSizeTooLarge = true
            }
            this.canAddMoreFile();
        }

        changeFiles(uploadedFiles) {
            this.$scope.ngfFiles = uploadedFiles;
            this.$scope.files = [];
            let promises = uploadedFiles.map(file => this.convertToBase64(file));
            promises.map(p => p.then(file => this.$scope.files.push(file)))

            Promise.all(promises).then(() => {
                this.validateAttachments(this.$scope.files);
            }).catch(rejected => console.log(rejected))
        }

        addFiles(uploadedFiles) {
            this.$scope.files = [];
            let promises = uploadedFiles.map(file => this.convertToBase64(file));
            promises.map(p => p.then(file => this.$scope.files.push(file)))

            Promise.all(promises).then(() => {
                this.validateAttachments(this.$scope.files);
            }).catch(rejected => console.log(rejected))
        }

        convertToBase64(file) {
            return new Promise((resolve, reject) => {
                let reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onload = () => resolve({name: file.name, content: reader.result, size: file.size});
                reader.onerror = error => reject(error);
            });
        }

        saveCustomerSupportRequest() {

            this.$scope.isSaving = true

            this.setResponse()

            this.$scope.customerSupport.subject = this.$scope.customerSupport.title
            delete this.$scope.customerSupport.title


            this.$scope.customerSupport.files = this.$scope.files

            this.serverCallService.makePost('/rest/admin/customerSupport', this.$scope.customerSupport)
                .then(response => {
                        this.$scope.isSaving = false
                        if (response.status === 200) {
                            this.$scope.showCustomerSupportInput = false
                            this.$scope.finalStep = true
                            this.$scope.captchaSuccess = false
                        } else {
                            this.$scope.showCustomerSupportDialog = false
                            this.$scope.captchaSuccess = false
                        }
                    }, () =>
                        this.$scope.isSaving = false
                )
        }

        getLoggedInUserData() {
            let user = this.authenticatedUserService.getUser();
            if (user) {
                const {name, surname} = user
                this.$scope.customerSupport.name = name + ' ' + surname
            }
        }

        showCustomerSupportInput() {
            this.$scope.showCustomerSupportInput = true
            this.$scope.userManualExists = false
            this.$scope.showUserManualsHelped = false
            this.$scope.isFileBtnVisible = true
        }

        toggleCustomerSupportDialog() {
            this.getLoggedInUserData()
            this.$scope.showCustomerSupportDialog = true
        }

        isSendDisabled() {
            const {name, email, subject, message} = this.$scope.customerSupport;
            return !(name && email && subject && message && this.$scope.captchaSuccess && !this.$scope.tooManyFiles && !this.$scope.fileSizeTooLarge)
        }

        back() {
            this.$scope.captchaSuccess = false
            this.$scope.tooManyFiles = false
            this.$scope.fileSizeTooLarge = false
            if (this.$scope.customerSupport.subject === 'Muu') {
                this.$scope.customerSupport.subject = ''
                this.$scope.userManualExists = false
                this.$scope.showCustomerSupportInput = false
            } else {

                this.$scope.showCustomerSupportInput = false
                this.$scope.backClickedWhileOther = true
                this.handleSelectChange(this.$scope.customerSupport.subject)
            }
        }

        close() {
            this.$scope.showCustomerSupportInput = false
            this.$scope.showCustomerSupportDialog = false
            this.$scope.userManualExists = false
            this.$scope.finalStep = false
            this.$scope.showUserManualsHelped = false
            this.$scope.customerSupport = {}
            this.$scope.captchaSuccess = false
            this.$scope.files = []
            this.$scope.ngfFiles = []
            this.$scope.isFileBtnVisible = false
        }

        handleSelectChange(subject) {
            this.$scope.userManualExists = subject !== 'Muu';
            this.$scope.showCustomerSupportInput = subject === 'Muu';
            this.$scope.customerSupportTitle = subject === 'Muu';
        }

        clickOutside() {
            if (!this.$scope.isSaving && this.$scope.allowDialogClose) {
                this.$scope.showCustomerSupportDialog = false
            }
            this.$scope.allowDialogClose = false
        }

        captchaSuccess() {
            this.$scope.captchaSuccess = true
        }

        getCaptchaKey() {
            this.serverCallService.makeGet('/rest/captcha')
                .then(({data}) => {
                    this.$scope.captchaKey = data
                })
        }

        getLanguage() {
            let language = this.translationService.getLanguage();
            return language === 'est' ? 'et' : language === 'rus' ? 'ru' : 'en';

        }
    }

    controller.$inject = [
        'userManualsAdminService',
        'serverCallService',
        'toastService',
        '$scope',
        'authenticatedUserService',
        '$location',
        '$translate',
        '$window',
        '$rootScope',
        'vcRecaptchaService',
        'translationService'
    ]
    component('dopCustomerSupport', {
        templateUrl: 'directives/customerSupport/customerSupport.html',
        controller
    })
}
