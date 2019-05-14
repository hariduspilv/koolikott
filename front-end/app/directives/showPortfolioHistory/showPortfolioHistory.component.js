'use strict'
{
    class controller extends Controller {
        $onInit() {
            this.$scope.$on('portfolioHistory:show', this.getPortfolioLogs.bind(this));
            this.$scope.showLogSelect = true;
            // this.$scope.logSelectionInProgress = false;

        }

        selectPortfolioLog(selectedPortfolioLog) {
            if (!this.$scope.originalPortfolio) {
                this.$scope.originalPortfolio = this.portfolio;
                // this.historySelectionInProgress = true;
            }
            if (selectedPortfolioLog) {
                this.portfolio = selectedPortfolioLog;
                // this.$rootScope.$broadcast('portfolioHistory:loadHistory');
            }
        }

        restoreSelectedPortfolio() {

            this.$mdDialog.show({
                template: `<md-dialog aria-label="Ekirja saatmise modaalaken" flex-gt-sm="75" flex id="send-email-modal"
           class=" creator-email-dialog creator-email-full-screen">
    <md-toolbar data-ng-if="!emailSent" class="md-accent">
      <div class="md-toolbar-tools" flex>
        <div>
          <h2 data-translate="OLED_KINDEL"></h2>
        </div>
        <span flex></span>
      </div>
    </md-toolbar>
    <div>
      <md-content class="md-padding send-mail-ie">
        <div layout-xs="column" layout="column" layout-align-xs="start stretch" layout-align="start stretch" flex-order="1"
             flex-order-gt-sm="2" flex="100">
          <md-input-container class="md-block animate-if remaining-charaters-wrapper label-top-ie" flex>
            <h2>"SISU MIDA KUVADA"</h2>
            <div layout="row" layout-align="space-between center" layout-xs="column" layout-align-xs="end center">
              <md-dialog-actions style="padding: 0px">
                <span flex></span>
                <md-button aria-label="Tühista emaili saatmine" id="send-email-cancel-button"
                           data-ng-disabled="isSaving"
                           data-ng-click="cancel()">
                  <span data-translate="LOOBUN"></span>
                </md-button>
                <md-button class="md-raised md-primary"
                           data-ng-click="$ctrl.setPortfolioHistoryToRestore()"
                           aria-label="Send mail"
                >
                  <span data-translate="KINNITAN"></span>
                </md-button>
                <md-progress-circular class="md-accent ng-hide"
                                      md-mode="indeterminate"
                                      md-diameter="40"
                                      data-ng-show="isSaving"></md-progress-circular>
              </md-dialog-actions>
            </div>
          </md-input-container>
        </div>
      </md-content>
    </div>
</md-dialog>`,
                controllerAs: '$ctrl',
                clickOutsideToClose: false,
                locals: {
                    portfolio: this.portfolio
                }

            })
            // this.setPortfolioHistoryToRestore();
            // this.$scope.showLogSelect = false;
            // this.historySelectionInProgress = false;
        }

        closePortfolioRestore() {
            this.$scope.showLogSelect = false;
            this.portfolio = this.$scope.originalPortfolio;
            // this.historySelectionInProgress = false;
            // this.$rootScope.$broadcast('portfolioHistory:loadHistory');

            // this.showPortfolioHistoryDialog();
        }

        actionsAfterSelect() {


        }

        getPortfolioLogs() {
            let url = 'rest/portfolio/getPortfolioHistoryAll?portfolioId=' + this.$scope.$parent.learningObject.id;
            // let url = 'rest/portfolio/history?portfolioId=' + this.portfolio.id;
            this.serverCallService.makeGet(url)
                .then(({data}) => {
                    if (data) {
                        this.$scope.data = data;
                    }
                });
        }

        // getPortfolioHistoryToRestore(id) {
        //     rest/portfolio/history/restore
            // let url = 'rest/portfolio/getPortfolioHistory?portfolioHistoryId=' + id;
            // this.serverCallService.makeGet(url)
            //     .then(({data}) => {
            //         if (data) {
            //             this.$scope.returnedData = data;
            //         }
            //     });
        // }
           setPortfolioHistoryToRestore() {
            // let url = 'rest/portfolio/history/restore';
            let url = 'rest/portfolio/update';
            this.portfolio.type = '.Portfolio';
            this.portfolio.id = this.portfolio.learningObject;
            this.serverCallService.makePost(url,this.portfolio)
                .then(({data}) => {
                    if (data) {
                        this.$scope.returnedData = data;
                    }
                });
        }
    }

    controller.$inject = [
        '$scope',
        '$mdDialog',
        'serverCallService'
    ]
    component('dopShowPortfolioHistory', {
        bindings: {
            portfolio: '=',
        },
        templateUrl: 'directives/showPortfolioHistory/showPortfolioHistory.html',
        controller
    })
}
