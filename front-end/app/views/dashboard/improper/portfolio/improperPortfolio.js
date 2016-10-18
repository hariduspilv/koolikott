define([
    'app',
    'angular-material-data-table',
    'services/serverCallService',
    'views/dashboard/baseTable/baseTable'
], function(app) {
    app.controller('imporperPortfoliosController', ['$scope', 'serverCallService', '$controller', '$filter',
        function($scope, serverCallService, $controller, $filter) {
            var base = $controller('baseTableController', {
                $scope: $scope
            });

            serverCallService.makeGet("rest/impropers", {}, sortImpropers, base.getItemsFail);

            $scope.title = $filter('translate')('DASHBOARD_IMRPOPER_PORTFOLIOS');

            $scope.bindTable = function() {
                base.buildTable('#improper-portfolios-table', 'views/dashboard/improper/improper.html');
            };

            $scope.getLearningObjectTitle = function(portfolio) {
            	return portfolio.title;
            };

            $scope.getLearningObjectUrl = function(learningObject) {
            	return "/portfolio?id=" + learningObject.id;
            }

            function sortImpropers(impropers) {
                var improperMaterials = [];

                for (var i = 0; i < impropers.length; i++) {
                    if (impropers[i].learningObject.type === '.Portfolio' && !impropers[i].learningObject.deleted) {
                        improperMaterials.push(impropers[i]);
                    }
                }

                base.getItemsSuccess(improperMaterials, 'byReportCount', true);
            }
        }
    ]);
});
