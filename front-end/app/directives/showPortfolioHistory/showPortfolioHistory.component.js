'use strict'
{
    class controller extends Controller {
        $onInit() {
            this.$scope.$on('portfolioHistory:show', this.getPortfolioLogs.bind(this));
        }

        getPortfolioLogs() {
            let url = 'rest/portfolio/getPortfolioHistoryAll?portfolioId=' + 5475;
            this.serverCallService.makeGet(url)
                .then(({data}) => {
                    if (data) {
                        this.$scope.data = data;
                    }
                });
        }

        getPortfolioHistoryToRestore() {
            let url = 'rest/portfolio/getPortfolioHistory?portfolioHistoryId=' + 25;
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
        },
        templateUrl: 'directives/showPortfolioHistory/showPortfolioHistory.html',
        controller
    })
}
