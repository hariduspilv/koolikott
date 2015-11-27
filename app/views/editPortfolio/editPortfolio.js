define(['app'], function(app)
{
    app.run(['$anchorScroll', function($anchorScroll) {
      $anchorScroll.yOffset = 50;
    }]);

    app.controller('editPortfolioController', ['$scope', 'translationService', 'serverCallService', '$route', '$location', 'alertService', '$rootScope', 'authenticatedUserService',
        function($scope, translationService, serverCallService, $route, $location, alertService, $rootScope, authenticatedUserService) {

            function init() {
				if ($rootScope.savedPortfolio) {
					$scope.portfolio = $rootScope.savedPortfolio;
				} else {
					getPortfolio(getPortfolioSuccess, getPortfolioFail);
				}
				$rootScope.isEditPortforlioMode = true;
			}

			function getPortfolio(success, fail) {
				var portfolioId = $route.current.params.id;
				serverCallService.makeGet("rest/portfolio?id=" + portfolioId, {}, success, fail);
			}

	        function getPortfolioSuccess(portfolio) {
	            if (isEmpty(portfolio)) {
	            	getPortfolioFail();
	            } else {
	                $scope.portfolio = portfolio;
	            }
	    	}

	    	function getPortfolioFail() {
                $rootScope.isEditPortforlioMode = false;
	            log('No data returned by getting portfolio.');
	            alertService.setErrorAlert('ERROR_PORTFOLIO_NOT_FOUND');
	            $location.url("/");
	    	}

            $scope.toggleSidenav = function (menuId) {
                $mdSidenav(menuId).toggle();
            };

            $scope.showEditPortfolioDialog = function($event) {
                $event.preventDefault();

                $mdDialog.show({
                    controller: 'addPortfolioDialog',
                    templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html'
                });
            };

            $scope.deleteSubChapter = function($event, chapterId, subChapterId) {
                var confirm = getDeleteConfirmationDialog(
                    'Kas oled kindel?',
                    'Alampeatüki kustutamist ei ole võimalik tagasi võtta',
                    $event, chapterId, subChapterId);

                $mdDialog.show(confirm).then(function() {
                    $scope.portfolio.chapters[chapterId].subChapters.splice(subChapterId, 1);
                }, null);
            };

            $scope.deleteChapter = function($event, chapterId) {
                var confirm = getDeleteConfirmationDialog(
                    'Kas oled kindel?',
                    'Peatüki kustutamist ei ole võimalik tagasi võtta',
                    $event, chapterId);;

                $mdDialog.show(confirm).then(function() {
                    $scope.portfolio.chapters.splice(chapterId, 1);
                }, null);
            };

            var getDeleteConfirmationDialog = function(title, content) {
                 var args = Array.prototype.slice.call(arguments, 2);

                return $mdDialog.confirm()
                          .title(title)
                          .content(content)
                          .targetEvent(args)
                          .ok('Jah')
                          .cancel('Ei');
            };

            $scope.savePortfolio = function() {
                // When saving action completed (error or success) give user feedback via toast notification.

                $mdToast.show($mdToast.simple().position('right top').content('Portfolio saved!'));
            };
            
            init();
    	}
    ]);
});
