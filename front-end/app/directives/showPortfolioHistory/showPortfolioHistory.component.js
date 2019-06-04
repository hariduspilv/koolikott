'use strict';
{
    class controller extends Controller {
        $onInit() {
            this.$scope.$on('portfolioHistory:show', this.getPortfolioLogs.bind(this));
            this.selectPortfolioLog();
            this.$scope.showConfirmButton = false;
        }

        selectPortfolioLog(selectedPortfolioLog) {
            if (!this.$scope.originalPortfolio) {
                this.$scope.originalPortfolio = this.portfolio;
            }
            if (selectedPortfolioLog) {
                this.portfolio = selectedPortfolioLog;
                this.$scope.showConfirmButton = true;
                this.$rootScope.$broadcast('portfolioHistory:hideDeleteButton');
            }
        }

        restoreSelectedPortfolio() {
            const scope = this.$scope.$new(true);
            scope.portfolio = this.portfolio;
            scope.showlogselect = this.$scope.showlogselect;
            this.$rootScope.$broadcast('portfolioHistory:hide');

            this.$mdDialog.show({
                templateUrl: 'directives/showPortfolioHistory/showPortfolioLogConfirm.html',
                controller: 'showPortfolioLogController',
                scope,
                controllerAs: '$ctrl',
            })
                .then(() => {
                    this.showlogselect = false;
                    this.$scope.showConfirmButton = false;
                    this.$scope.selectedPortfolioLog = undefined;
                })
        }

        closePortfolioRestore() {
            this.showlogselect = false;
            this.portfolio = this.$scope.originalPortfolio;
            this.$scope.selectedPortfolioLog = undefined;
            this.$scope.showConfirmButton = false;
            this.$rootScope.$broadcast('portfolioHistory:hide');
        }

        getPortfolioLogs() {
            let urlEnd = this.portfolio.type === '.Portfolio' ? this.portfolio.id : this.portfolio.learningObject;

            if (!this.$scope.originalPortfolio) {
                this.$scope.originalPortfolio = this.portfolio;
            }

            let url = 'rest/portfolio/getPortfolioHistoryAll?portfolioId=' + urlEnd;
            this.serverCallService.makeGet(url)
                .then(({data}) => {
                    if (data) {
                        this.$scope.data = data;
                    }
                });
        }
    }

    controller.$inject = [
        '$scope',
        '$mdDialog',
        'serverCallService',
        '$rootScope',
        '$translate',
        'translationService',
    ]
    component('dopShowPortfolioHistory', {
        bindings: {
            portfolio: '=',
            showlogselect: '=',
        },
        templateUrl: 'directives/showPortfolioHistory/showPortfolioHistory.html',
        controller
    })
}
