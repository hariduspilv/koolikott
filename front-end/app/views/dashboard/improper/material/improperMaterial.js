define([
    'app',
    'ngload!angular-material-data-table',
    'services/serverCallService',
    'views/dashboard/baseTable/baseTable'
], function(app) {
    app.controller('improperMaterialsController', ['$scope', 'serverCallService', '$controller', '$filter',
        function($scope, serverCallService, $controller, $filter) {
            var base = $controller('baseTableController', {
                $scope: $scope
            });

            serverCallService.makeGet("rest/impropers", {}, filterResults, base.getItemsFail);

            $scope.title = $filter('translate')('DASHBOARD_IMRPOPER_MATERIALS');

            $scope.bindTable = function() {
              base.buildTable('#improper-materials-table', 'views/dashboard/improper/improper.html');
            };

            function filterResults(impropers) {
                console.log(impropers);
            	var improperMaterials = [];

            	for (var i = 0; i < impropers.length; i++) {
            		if (impropers[i].learningObject.type === '.Material' && !impropers[i].learningObject.deleted) {
                        improperMaterials.push(impropers[i]);
            		}
            	}

            	base.getItemsSuccess(improperMaterials, 'byReportCount', true);
            }

            $scope.getLearningObjectTitle = function(material)  {
            	return base.getCorrectLanguageTitle(material);
            };

            $scope.getLearningObjectUrl = function(learningObject) {
            	return "/material?materialId=" + learningObject.id;
            }
        }
    ]);
});
