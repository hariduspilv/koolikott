define(['app'], function(app)
{
    app.run(['$anchorScroll', function($anchorScroll) {
      $anchorScroll.yOffset = 50;
    }]);

    app.controller('editPortfolioController', ['$scope', 'serverCallService', 'translationService', '$mdSidenav', '$mdDialog', '$mdToast', '$document',
        function($scope, serverCallService, translationService, $mdSidenav, $mdDialog, $mdToast, $document) {
            $scope.portfolio = {
                chapters: []
            };

            // example data

            $scope.portfolio.chapters.push({
                title: 'Esimene peatükk',
                subchapters: [
                    {
                        title: 'Alampeatükk 1',
                        materials: []
                    },
                    {
                        title: 'Alampeatükk 2',
                        materials: []
                    }
                ]
            },
            {
                title: 'Teine peatükk',
                subchapters: [
                    {
                        title: 'Alampeatükk 1',
                        materials: []
                    }
                ]
            },
            {
                title: 'Kolmas peatükk',
                subchapters: [
                    {
                        title: 'Alampeatükk 1',
                        materials: []
                    },
                    {
                        title: 'Alampeatükk 2',
                        materials: []
                    },
                    {
                        title: 'Alampeatükk 3',
                        materials: []
                    }
                ]
            });

            // example data end


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
    	}
    ]);
});
