'use strict'

{
    class controller extends Controller {
        $onInit() {

            // this.$scope.showPortfolioHistoryDialog = this.showPortfolioHistoryDialog.bind(this)
        }

        // showPortfolioHistoryLog() {
        //     this.$scope.showPortfolioHistoryDialog = true;
        //     let menu = document.getElementById('historymenu');
        //     menu.dispatchEvent(this.returnEvent());
        // }
        //
        // returnEvent() {
        //     let event;
        //     if (typeof (MouseEvent) === 'function') {
        //         event = new MouseEvent('click');
        //     } else {
        //         event = document.createEvent('MouseEvent');
        //         event.initEvent('click', true, true);
        //     }
        //     return event;
        // }

        $doCheck() {
            if (this.$scope.portfolio !== this.portfolio)
                this.$scope.portfolio = this.portfolio
        }


    }
    controller.$inject = [
        '$rootScope',
        '$scope',
        '$location',
        '$mdDialog',
        '$rootScope',
        'authenticatedUserService',
        '$route',
        'dialogService',
        'serverCallService',
        'toastService',
        'storageService',
        'targetGroupService',
        'taxonService',
        'taxonGroupingService',
        'eventService',
        'portfolioService'
    ]
    component('dopShowPortfolioHistory', {
        bindings: {
            portfolio: '=',
        },
        templateUrl: 'directives/showPortfolioHistory/showPortfolioHistory.html',
        controller
    })
}
