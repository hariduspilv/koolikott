'use strict';

angular.module('koolikottApp').controller('showPortfolioLogController', [
    '$scope', '$mdDialog', 'serverCallService',
    function ($scope, $mdDialog, serverCallService) {

        $scope.setPortfolioHistoryToRestore = function () {
            $scope.portfolio.type = '.Portfolio';
            $scope.portfolio.id = this.portfolio.learningObject;
            serverCallService.makePost(`rest/portfolio/update/${false}`, $scope.portfolio)
                .then(({data}) => {
                    if (data) {
                        $scope.showLogSelect = false;
                        $mdDialog.hide();
                    }
                });
        };

        $scope.hide = function () {
            return false;
        };

        $scope.cancel = function () {
            $mdDialog.hide();
        };
    }
]);
