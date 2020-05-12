'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.showLoEmailCodeDialog = () => {

                if(this.$location.absUrl().contains('kogumik')){
                    gTagCaptureEventWithLabel('request', 'teaching portfolio', 'Feedback to author')
                } else {
                    gTagCaptureEventWithLabel('request', 'teaching material', 'Feedback to author')
                }

                if (typeof this.learningObject.creator !== 'undefined' && this.learningObject.creator !== null) {
                    this.userEmailService.userHasEmail(this.learningObject.creator.id)
                        .then(response => {
                            if (response.status === 200) {
                                this.showDialog();
                            } else {
                                this.showNoCreatorEmailDialog()
                            }
                        })
                        .catch(() => {
                            this.showNoCreatorEmailDialog()
                        });
                } else {
                    this.showNoCreatorEmailDialog()
                }
            };
        }

    showNoCreatorEmailDialog() {

    this.$mdDialog.show({
        template: `<md-dialog aria-label="Ekirja saatmise lõpu modaalaken" class="login-dialog">
                    <md-toolbar class="md-accent">
                      <div class="md-toolbar-tools" flex>
                        <span flex></span>
                        <md-button ng-click="cancel()" id="email-send-close" class="md-icon-button"
                                   aria-label="Sulge ekirja saatmise modaalaken">
                          <md-icon>close</md-icon>
                        </md-button>
                      </div>
                    </md-toolbar>
                    <md-dialog-content>
                      <md-content data-layout-padding>
                        <p><span id="location-restore-text" data-translate="SEND_EMAIL_NO_CREATOR_EMAIL"></span></p>
                      </md-content>
                    </md-dialog-content>
                   </md-dialog>`,
        controller: 'sendEmailDialogController',
        controllerAs: '$ctrl',
        clickOutsideToClose: false,
        locals: {
            learningObject: this.learningObject
        }

    })
}

        showDialog() {

            this.$mdDialog.show({
                templateUrl: '/directives/sendEmail/sendEmail.html',
                controller: 'sendEmailDialogController',
                controllerAs: '$ctrl',
                clickOutsideToClose: false,
                locals: {
                    learningObject: this.learningObject
                }
            })
        }
}

controller.$inject = [
    '$scope',
    '$rootScope',
    '$location',
    '$mdDialog',
    'userEmailService',
    'dialogService',
]

component('dopSendEmail', {
    controller,
    template: `
            <div ng-click="$ctrl.showLoEmailCodeDialog()">
                <md-button
                  class="md-icon-button">
                  <md-icon md-menu-align-target>email</md-icon>
                  <md-tooltip md-direction="top"><span data-translate="SEND_EMAIL_SPECIFY_QUESTION"></span></md-tooltip>
                </md-button>
              </div>
        `,
    bindings : {
        learningObject: '<'
    }
})
}
