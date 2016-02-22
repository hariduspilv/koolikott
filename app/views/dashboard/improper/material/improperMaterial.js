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
            }
            
            function filterResults(impropers) {
            	var improperMaterials = [];
            	
            	for (var i = 0; i < impropers.length; i++) {
            		var improper = impropers[i];
            		
            		if (improper.learningObject.type === '.Material') {
            			improperMaterials.push(improper);
            		}
            	}
            	
            	base.getItemsSuccess(improperMaterials);
            }
            
            $scope.getLearningObjectTitle = function(material)  {
            	return base.getCorrectLanguageTitle(material);
            }
            
            $scope.getLearningObjectUrl = function(learningObject) {
            	return "#/material?materialId=" + learningObject.id;
            }
        }
    ]);
});