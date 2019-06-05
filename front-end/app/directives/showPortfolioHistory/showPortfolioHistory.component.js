'use strict';
{
    class controller extends Controller {
        $onInit() {
            this.$rootScope.$on('portfolioHistory:show', this.getPortfolioLogs.bind(this));
            this.$rootScope.$on('portfolioHistory:hideLogSelect', this.hideLogSelect.bind(this));

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

        hideLogSelect(){
            this.showlogselect = false;

        }
        restoreSelectedPortfolio() {
            const scope = this.$scope.$new(true);
            scope.portfolio = this.portfolio;
            scope.showlogselect = this.$scope.showlogselect;

            this.$mdDialog.show({
                templateUrl: 'directives/showPortfolioHistory/showPortfolioLogConfirm.html',
                controller: 'showPortfolioLogController',
                scope,
                controllerAs: '$ctrl',
            })
                .then((e) => {
                    if (!e) {
                        this.showlogselect = false;
                        this.$rootScope.$broadcast('portfolioHistory:hide');
                    }
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
