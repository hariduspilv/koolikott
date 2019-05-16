'use strict';

angular.module('koolikottApp').controller('showPortfolioLogController', [
    '$scope', '$mdDialog', 'serverCallService', 'translationService', 'toastService', '$rootScope', '$filter',
    function ($scope, $mdDialog, serverCallService, translationService, toastService, $rootScope, $filter) {

        function init() {
            if (!$scope.portfolio) return;
        }

        $scope.setPortfolioHistoryToRestore = function () {
            let url = 'rest/portfolio/update';
            $scope.portfolio.type = '.Portfolio';
            $scope.portfolio.id = this.portfolio.learningObject;
            serverCallService.makePost(url, $scope.portfolio)
                .then(({data}) => {
                    if (data) {
                        $scope.showLogSelect = false;
                        $mdDialog.hide();
                    }
                });
        }

        $scope.hide = function (){
            return false;
        };

        $scope.getTranslation = function (key) {
            return $filter('translate')(key);
        };

        $scope.cancel = function () {
            $mdDialog.hide();
        };

        init();
    }
]);
