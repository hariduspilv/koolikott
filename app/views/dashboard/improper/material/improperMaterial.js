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
            	var improperMaterials = [];

            	for (var i = 0; i < impropers.length; i++) {
            		var improper = impropers[i];

            		if (improper.learningObject.type === '.Material') {
                        // collect duplicates
                        var isAlreadyReported = false;

                        for (var j = 0; j < improperMaterials.length; j++) {
                            if (improperMaterials[j].learningObject.id === improper.learningObject.id) {
                                isAlreadyReported = true;

                                improperMaterials[j].reportCount++;

                                // show the newest date
                                if (new Date(improperMaterials[j].added) < new Date(improper.added)) {
                                    improperMaterials[j].added = improper.added;
                                }

                                break;
                            }
                        }

                        if (!isAlreadyReported) {
                            improper.reportCount = 1;
            			    improperMaterials.push(improper);
                        }
            		}
            	}

            	base.getItemsSuccess(improperMaterials, 'byReportCount');
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
