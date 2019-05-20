'use strict';
{
    class controller extends Controller {
        $onInit() {
            this.$scope.$on('portfolioHistory:show', this.getPortfolioLogs.bind(this));
            this.$scope.showlogselect = true;
            this.$scope.selectionMade = true;
            this.selectPortfolioLog();
        }

        selectPortfolioLog(selectedPortfolioLog) {
            if (!this.$scope.originalPortfolio) {
                this.$scope.originalPortfolio = this.portfolio;
            }
            if (selectedPortfolioLog) {
                this.portfolio = selectedPortfolioLog;
                this.$scope.selectionMade = false;
            }
        }

        showSelection() {
            this.$scope.selectionMade = !this.$scope.selectionMade;
        }

        restoreSelectedPortfolio() {
            const scope = this.$scope.$new(true);
            scope.portfolio = this.portfolio;
            scope.showlogselect = this.$scope.showlogselect;

            this.$mdDialog.show({
                templateUrl: 'directives/showPortfolioHistory/showPortfolioLogConfirm.html',
                controller: 'showPortfolioLogController',
                scope
            })
                .then(() => {
                    this.showlogselect = false;
                })
        }

        closePortfolioRestore() {
            this.showlogselect = false;
            this.portfolio = this.$scope.originalPortfolio;
            this.$rootScope.$broadcast('portfolioHistory:hide');
        }

        getPortfolioLogs() {
            let urlEnd = this.portfolio.type === '.Portfolio' ? this.portfolio.id : this.portfolio.learningObject;

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
