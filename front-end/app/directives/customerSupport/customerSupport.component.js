'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.validEmail = VALID_EMAIL
            this.$scope.customerSupport = {}
            this.$scope.captchaKey = ''
            this.getCaptchaKey()
            this.setPlaceholder()
            this.$scope.fileSizeTooLarge = false
            this.$scope.maxFiles = false

            this.$scope.files = []
            this.$scope.ngfFiles = []

            this.$scope.isFileBtnVisible = false
            this.$scope.fileSizeTogether = 0
            this.$scope.filesCount = 0

            this.$scope.showCustomerSupportTitle = false

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

        isAuthenticatedUser() {
            return this.authenticatedUserService.isAuthenticated();
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
            this.$scope.maxFiles = false

            if(files.length !== 0){
                this.$timeout(() =>  {
                    this.$scope.customerSupportForm.file.$error.maxSize =false  },
                    100000);
            }

            if(files.length === 3){
                this.$scope.maxFiles = true
            }

            this.$scope.fileSizeTogether = files.map(item => item.size)
                .reduce((prev, next) => prev + next, 0) / 1024 / 1024;

            if (this.$scope.fileSizeTogether > 10) {
                this.$scope.customerSupportForm.file.$error.maxSize = true
                this.$scope.fileSizeTooLarge = true
            }
        }

        changeFiles(uploadedFiles) {
            this.$scope.isSaving = true
            this.$scope.ngfFiles = uploadedFiles;
            this.$scope.files = [];
            let promises = uploadedFiles.map(file => this.convertToBase64(file));
            promises.map(p => p.then(file => this.$scope.files.push(file)))

            Promise.all(promises).then(() => {
                this.$scope.isSaving = false
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
            let subject
            this.$scope.isSaving = true

            this.setResponse()

            if (!!this.$scope.customerSupport.title)
                subject = this.$scope.customerSupport.title
            else
                subject = this.$scope.customerSupport.subject

            const {name, email, message} = this.$scope.customerSupport

            this.serverCallService.makePost('/rest/admin/customerSupport',  {name: name, email: email, message: message, subject: subject, files: this.$scope.files})
                .then(response => {
                        this.$scope.isSaving = false
                        if (response.status === 200) {
                            this.$scope.showCustomerSupportInput = false
                            this.$scope.finalStep = true
                            this.$scope.captchaSuccess = false
                            this.$scope.customerSupport.message = ''
                            this.$translate('CUSTOMER_SUPPORT_MESSAGE_PLACEHOLDER').then(value => {
                                this.$scope.placeholder = value
                            })
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
            const {name, email, subject, message, title} = this.$scope.customerSupport;
            if (this.$scope.showCustomerSupportTitle)
                return !(this.check(name, email, subject, message) && title)
            else
                return !(this.check(name, email, subject, message))
        }

        check(name, email, subject, message) {
            return name && email && subject && message && this.$scope.captchaSuccess && !this.$scope.tooManyFiles && !this.$scope.fileSizeTooLarge;
        }

        back() {
            this.$scope.captchaSuccess = false
            this.$scope.tooManyFiles = false
            this.$scope.fileSizeTooLarge = false
            if (this.$scope.customerSupport.subject === 'Muu') {
                this.$scope.customerSupport.subject = ''
                this.$scope.userManualExists = false
                this.$scope.showCustomerSupportInput = false
                this.$scope.customerSupport.message = ''
                this.$translate('CUSTOMER_SUPPORT_MESSAGE_PLACEHOLDER').then(value => {
                    this.$scope.placeholder = value
                })

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
            this.$scope.maxFiles = false
        }

        handleSelectChange(subject) {
            this.$scope.captchaSuccess = false
            this.$scope.userManualExists = subject !== 'Muu';
            this.$scope.showCustomerSupportInput = subject === 'Muu';

            if (this.$scope.showCustomerSupportInput) {
                this.$scope.showCustomerSupportTitle = true
                setTimeout(this.resetCaptcha, 1000);

            } else {
                this.$scope.showCustomerSupportTitle = false
            }
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

        captchaExpired() {
            this.$scope.captchaSuccess = false
        }

        resetCaptcha() {
            if (this.grecaptcha.getResponse() !== '') {
                this.grecaptcha.reset();
            }
        }

        setPlaceholder() {
            if (!this.$scope.customerSupport.message || this.$scope.customerSupport.message.length <= 0) {
                this.$translate('CUSTOMER_SUPPORT_MESSAGE_PLACEHOLDER').then(value => {
                    this.$scope.placeholder = value
                })
            } else {
                this.$translate('SEND_EMAIL_CONTENT_COUNTER').then(value => {
                    this.$scope.placeholder = (value.replace('${counter}', 500 - this.$scope.customerSupport.message.length))
                })
            }
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
        'translationService',
        '$timeout'
    ]
    component('dopCustomerSupport', {
        templateUrl: 'directives/customerSupport/customerSupport.html',
        controller
    })
}
