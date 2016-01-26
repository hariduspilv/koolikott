define([
    'app',
    'ng-file-upload',
    'services/serverCallService',
    'services/storageService'
], function(app) {
    return ['$scope', '$mdDialog', '$location', 'serverCallService', '$rootScope', 'storageService', function($scope, $mdDialog, $location, serverCallService, $rootScope, storageService) {
        $scope.saving = false;
        $scope.showHints = true;

        function init() {
            var portfolio = storageService.getPortfolio();
          
            $scope.newPortfolio = createPortfolio();
            $scope.portfolio = portfolio;

            if ($scope.portfolio.id != null) {
                $scope.isEditPortfolio = true;

                var portfolioClone = angular.copy(portfolio);

                $scope.newPortfolio.title = portfolioClone.title;
                $scope.newPortfolio.summary = portfolioClone.summary;
                $scope.newPortfolio.taxon = portfolioClone.taxon;
                $scope.newPortfolio.targetGroups = portfolioClone.targetGroups;
                $scope.newPortfolio.tags = portfolioClone.tags;
            }
        }

        $scope.cancel = function() {
            $mdDialog.hide();
        };

        $scope.create = function() {
            $scope.saving = true;

            var url = "rest/portfolio/create";
            $scope.newPortfolio.picture = getPicture($scope.newPortfolio);

            serverCallService.makePost(url, $scope.newPortfolio, createPortfolioSuccess, createPortfolioFailed, savePortfolioFinally);
        };

        function getPicture(portfolio) {
            if (portfolio && portfolio.picture) {
                var base64Picture = portfolio.picture.$ngfDataUrl;
            }
            return base64Picture;
        }

        function createPortfolioSuccess(portfolio) {
            if (isEmpty(portfolio)) {
                createPortfolioFailed();
            } else {
                $rootScope.savedPortfolio = portfolio;
                $mdDialog.hide();
                $location.url('/portfolio/edit?id=' + portfolio.id);
            }
        }

        function createPortfolioFailed() {
            log('Creating portfolio failed.');
        }

        $scope.update = function() {
            $scope.saving = true;

            var url = "rest/portfolio/update";
            $scope.portfolio.title = $scope.newPortfolio.title;
            $scope.portfolio.summary = $scope.newPortfolio.summary;
            $scope.portfolio.taxon = $scope.newPortfolio.taxon;
            $scope.portfolio.targetGroups = $scope.newPortfolio.targetGroups;
            $scope.portfolio.tags = $scope.newPortfolio.tags;
            $scope.portfolio.picture = getPicture($scope.newPortfolio);
            serverCallService.makePost(url, $scope.portfolio, updatePortfolioSuccess, createPortfolioFailed, savePortfolioFinally);
        };

        function updatePortfolioSuccess(portfolio) {
            if (isEmpty(portfolio)) {
                createPortfolioFailed();
            } else {
                var picture = $scope.portfolio.picture;
                portfolio.picture = picture;

                $rootScope.savedPortfolio = portfolio;
                $scope.portfolio = portfolio;
                $mdDialog.hide();
            }
        }

        function savePortfolioFinally() {
            $scope.saving = false;
        }

        init();
    }];
});