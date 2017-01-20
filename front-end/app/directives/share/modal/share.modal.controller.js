'use strict';

angular.module('koolikottApp')
.controller('shareModalController', [
    '$mdDialog', 'locals', 'authenticatedUserService', '$translate', 'Socialshare', 'serverCallService', 'toastService',
    function ($mdDialog, locals, authenticatedUserService, $translate, Socialshare, serverCallService, toastService) {
        var vm = this;

        if ((locals.isOwner() || authenticatedUserService.isAdmin() || authenticatedUserService.isModerator()) && locals.isPrivate()) {
            vm.showButtons = true;

            vm.title = $translate.instant('THIS_IS_PRIVATE');
            vm.context = $translate.instant('SHARE_PRIVATE_PORTFOLIO');
            vm.ariaLabel = $translate.instant('THIS_IS_PRIVATE');
        } else {
            vm.title = $translate.instant('THIS_IS_UNLISTED');
            vm.context = $translate.instant('THINK_AND_SHARE');
            vm.ariaLabel = $translate.instant('THIS_IS_UNLISTED');
        }

        vm.updatePortfolio = (state) => {
            var portfolioClone = angular.copy(locals.portfolio);
            portfolioClone.visibility = state;
            serverCallService.makePost("rest/portfolio/update", portfolioClone, updateSuccess, updateFail);

            locals.setShareParams(locals.item);

            $mdDialog.cancel();
        };

        function updateSuccess(data) {
            if (isEmpty(data)) {
                updateFail();
            } else {
                locals.portfolio.visibility = data.visibility;
                toastService.show('PORTFOLIO_SAVED');
            }
        }

        function updateFail() {
            console.log("Updating portfolio failed");
        }

        vm.back = () => {
            $mdDialog.cancel();
        }

        vm.success = () => {
            locals.setShareParams(locals.item);

            $mdDialog.cancel();
        }
    }
]);
