define(['app'], function(app) {
    app.controller('addPortfolioDialog', ['$scope', '$mdDialog', '$location', 'serverCallService', '$rootScope', 'portfolio',
        function($scope, $mdDialog, $location, serverCallService, $rootScope, portfolio) {
            $scope.saving = false;
            $scope.showHints = true;

            function init() {
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

                serverCallService.makePost(url, $scope.newPortfolio, createPortfolioSuccess, createPortfolioFailed);
            };

            function createPortfolioSuccess(portfolio) {
                if (isEmpty(portfolio)) {
                    createPortfolioFailed();
                } else {
                    $rootScope.savedPortfolio = portfolio;
                    
                    if ($scope.newPicture) {
                    	portfolio.hasPicture = true;
                    	portfolio.picture = $scope.newPicture.$ngfDataUrl
                    	uploadPicture(portfolio);                    	
                    } else {
                    	redirectToEditPage();
                    }
                }
            }

            function createPortfolioFailed() {
                log('Creating portfolio failed.');
            }
            
            function uploadPicture(portfolio) {
            	var url = "rest/portfolio/addPicture?portfolioId=" + portfolio.id;
            	picture = $scope.newPicture;
                var data = {
                		picture: picture
                }
                serverCallService.upload(url, data, redirectToEditPage, createPortfolioFailed, savePortfolioFinally);
            }

            function redirectToEditPage() {
                $mdDialog.hide();
                $location.url('/portfolio/edit?id=' + $rootScope.savedPortfolio.id);
            }

            $scope.update = function() {
                $scope.saving = true;
              
                var url = "rest/portfolio/update";
                $scope.portfolio.title = $scope.newPortfolio.title;
                $scope.portfolio.summary = $scope.newPortfolio.summary;
                $scope.portfolio.taxon = $scope.newPortfolio.taxon;
                $scope.portfolio.targetGroups = $scope.newPortfolio.targetGroups;
                $scope.portfolio.tags = $scope.newPortfolio.tags;
                serverCallService.makePost(url, $scope.portfolio, createPortfolioSuccess, createPortfolioFailed, savePortfolioFinally);
            };
            
            function savePortfolioFinally() {
                $scope.saving = false;
            }

            init();
        }
    ]);
});