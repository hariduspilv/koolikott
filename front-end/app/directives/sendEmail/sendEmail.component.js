'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)


            this.showLoEmailCodeDialog = () => {

                this.userEmailService.userHasEmail(this.$rootScope.portfolioCreator)
                    .then(data => {
                        if ({data}) {
                            if (data.email !== '')
                                this.$scope.userEmail = data.data.email;
                            else
                                this.showNoCreatorEmailDialog()

                            this.showDialog();
                        } else if (!{data}) {

                            this.showNoCreatorEmailDialog()
                        }
                    })
                    .catch((err) => {
                        console.log(err);
                    });
            };
        }

    showNoCreatorEmailDialog() {

    this.$mdDialog.show({
        template: `<md-dialog aria-label="Sessiooni lõppemise modaalaken" class="login-dialog">
                    <md-toolbar class="md-accent">
                      <div class="md-toolbar-tools" flex>
                        <span flex></span>
                        <md-button ng-click="cancel()" id="add-portfolio-close" class="md-icon-button"
                                   aria-label="Sulge kogumiku lisamise modaalaken">
                          <md-icon>close</md-icon>
                        </md-button>
                      </div>
                    </md-toolbar>
                    <md-dialog-content>
                      <md-content data-layout-padding>
                        <p><span id="location-restore-text" data-translate="KEY"></span></p>
                      </md-content>
                    </md-dialog-content>
                   </md-dialog>`,
        controller: 'sendEmailDialogController',
        controllerAs: '$ctrl',
        clickOutsideToClose: true,

    })
}
    showDialog(){

        this.$mdDialog.show({
            templateUrl: 'directives/sendEmail/sendEmail.html',
            controller: 'sendEmailDialogController',
            controllerAs: '$ctrl',
            clickOutsideToClose: true,
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
                <md-tooltip><span data-translate="TOOLTIP_SHOW_QR_CODE"></span></md-tooltip>
                <md-button
                  class="md-icon-button">
                  <md-icon md-menu-align-target>email</md-icon>
                  <md-tooltip><span data-translate="FULL_SCREEN_ON"></span></md-tooltip>
                </md-button>
              </div>
        `
})
}
