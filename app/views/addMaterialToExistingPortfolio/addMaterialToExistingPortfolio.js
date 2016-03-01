define([
    'app',
    'services/serverCallService',
    'services/translationService',
    'services/metadataService',
    'services/authenticatedUserService'
], function (app) {
    return ['$scope', '$mdDialog', 'serverCallService', 'translationService', 'metadataService', '$rootScope', 'authenticatedUserService',
        function ($scope, $mdDialog, serverCallService, translationService, metadataService, $rootScope, authenticatedUserService) {
            $scope.step = {};
            $scope.step.currentStep = 0;
            getUsersPortfolios();

            $scope.cancel = function () {
                $mdDialog.hide();
            };

            $scope.step.previousStep = function () {
                $scope.step.currentStep -= 1;
            };

            $scope.chooseChapter = function (selectedPortfolio) {
                $scope.selectedPortfolio = selectedPortfolio;
                $scope.step.currentStep = 1;
            };

            $scope.addMaterialsToChapter = function (chapter) {

            };

            function getUsersPortfolios() {
                var user = authenticatedUserService.getUser();
                var params = {
                    'username': user.username
                };
                var url = "rest/portfolio/getByCreator";
                serverCallService.makeGet(url, params, getUsersPortfoliosSuccess, getUsersPortfoliosFail);
            }

            function getUsersPortfoliosSuccess(data) {
                if (isEmpty(data)) {
                    getUsersPortfoliosFail();
                } else {
                    $scope.portfolios = data;
                }
            }

            function getUsersPortfoliosFail() {
                console.log('Failed to get portfolios.');
            }
        }];
});
