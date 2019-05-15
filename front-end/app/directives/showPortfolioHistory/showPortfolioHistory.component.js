'use strict';
{
    class controller extends Controller {
        $onInit() {
            this.$scope.$on('portfolioHistory:show', this.getPortfolioLogs.bind(this));
            this.$scope.showLogSelect = true;
            this.$scope.selectionMade = true;
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
            scope.showLogSelect = this.$scope.showLogSelect;

            this.$mdDialog.show({
                templateUrl: 'directives/showPortfolioHistory/showPortfolioLogConfirm.html',
                controller: 'showPortfolioLogController',
                scope
            })
                .then(() => {
                    this.$scope.showLogSelect = false;
                })
        }

        closePortfolioRestore() {
            this.$scope.showLogSelect = false;
            this.portfolio = this.$scope.originalPortfolio;
            // this.init();
            // this.$rootScope.$broadcast('portfolioHistory:show');
        }

        init() {
            this.getPortfolioLogs();
            this.$scope.showLogSelect = true;
            this.$scope.selectionMade = true;
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
    }

    controller.$inject = [
        '$scope',
        '$mdDialog',
        'serverCallService'
    ]
    component('dopShowPortfolioHistory', {
        bindings: {
            portfolio: '=',
            showLogSelect: '=',
        },
        templateUrl: 'directives/showPortfolioHistory/showPortfolioHistory.html',
        controller
    })
}
