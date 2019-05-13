'use strict'
{
    class controller extends Controller {
        $onInit() {
            this.$scope.$on('portfolioHistory:show', this.getPortfolioLogs.bind(this));
            this.$scope.showLogSelect = true;
            this.$scope.selectedPortfolioLog = {};
            this._previousportfolio = this.portfolio
        }

        $doCheck() {
            const portfolioLogChanged =
                (this.portfolio && !this._previousportfolio) ||
                (!this.portfolio && this._previousportfolio) ||
                !_.isEqual(this.portfolio, this._previousportfolio)

            if (portfolioLogChanged)
                this._previousportfolio = this.portfolio

            // if (portfolioLogChanged)
            //     this.buildInstitutions()

        }

        dosmth(selectedPortfolioLog){

            this.portfolio = selectedPortfolioLog;
            this.$rootScope.$broadcast('portfolioHistory:loadHistory');
            this.eventService.notify('portfolioHistory:loadHistory');
            // this.$scope.selectedPortfolioLog = this.selectedPortfolioLog;

        }

        restoreSelectedPortfolio(){

        }

        closePortfolioRestore(){
            this.$scope.showLogSelect = false;
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

        getPortfolioHistoryToRestore(id) {
            //rest/portfolio/history/restore
            let url = 'rest/portfolio/getPortfolioHistory?portfolioHistoryId=' + id;
            this.serverCallService.makeGet(url)
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
