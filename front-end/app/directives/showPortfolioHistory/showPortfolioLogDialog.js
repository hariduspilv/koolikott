'use strict';

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.setDialogInformation();
        }

        cancel() {
            this.$mdDialog.hide(event);
            return event;
        }

        setPortfolioHistoryToRestore() {
            let url = `rest/portfolio/update`;
            this.$scope.portfolio.type = '.Portfolio';
            this.$scope.portfolio.id = this.$scope.portfolio.learningObject;
            this.$scope.portfolio.saveType = 'MANUAL';
            this.serverCallService.makePost(url, this.$scope.portfolio)
                .then(({data}) => {
                    if (data) {
                        this.$scope.showLogSelect = false;
                        this.$mdDialog.hide();
                        this.toastService.show('PORTFOLIO_SAVED');
                        this.$rootScope.$broadcast('portfolioHistory:hide');
                    }
                })
                .catch(() => {
                    this.toastService.show('PORTFOLIO_SAVE_FAILED', 15000);
                })
        }

        setDialogInformation() {
            this.$translate('LOG_VERSION_STAY').then((value) => {
                this.$scope.confirmationNotice = (value.replace('${createdAtDate}', this.formatDateToDayMonthYear(this.$scope.portfolio.createdAt))
                    .replace('${createdAtTime}', this.formatDateToTime(this.$scope.portfolio.createdAt))
                    .replace('${creator}', this.$scope.portfolio.creator.name + ' ' + this.$scope.portfolio.creator.surname));
            })
        }
    }

    controller.$inject = [
        '$scope',
        '$rootScope',
        '$mdDialog',
        'serverCallService',
        'toastService',
        '$translate',
    ]
    angular.module('koolikottApp').controller('showPortfolioLogController', controller)
}
