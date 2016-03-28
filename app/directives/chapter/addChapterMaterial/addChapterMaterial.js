define([
    'app',
    'angularAMD',
    'services/translationService',
    'directives/validate/validateUrl'
], function(app, angularAMD) {
    app.directive('dopAddChapterMaterial', ['translationService', '$mdDialog', '$rootScope', 'storageService', 'serverCallService',
        function(translationService, $mdDialog, $rootScope, storageService, serverCallService) {
        return {
            scope: {
                chapter: '='
            },
            templateUrl: 'directives/chapter/addChapterMaterial/addChapterMaterial.html',
            controller: function($scope) {
                $scope.isEditable = $rootScope.isEditPortfolioMode;

                $scope.addMaterialFromPermalink = function() {
                    if ($scope.resourcePermalinkForm.$valid) {
                        var encodedUrl   = encodeURIComponent($scope.chapter.resourcePermalink);
                        serverCallService.makeGet("rest/material/getBySource?source=" + encodedUrl, {}, getByUrlSuccess, getByUrlFail);
                    } else {
                        $scope.resourcePermalinkForm.url.$setDirty();
                        $scope.resourcePermalinkForm.url.$setTouched();
                    }
                };


                function getByUrlSuccess(materials) {
                    if(materials && materials[0]) {
                        $scope.chapter.resourcePermalink = "";
                        $scope.resourcePermalinkForm.url.$setPristine();
                        $scope.resourcePermalinkForm.url.$setUntouched();

                        if (!containsMaterial(materials[0])) {
                        	$scope.chapter.materials.push(materials[0]);
                        }
                    } else{
                        getByUrlFail();
                    }
                }

                function containsMaterial(material) {
                	return $scope.chapter.materials.indexOfWithComparator(material, function(obj1, obj2) {
                		return obj1.id - obj2.id;
                	}) >= 0;
                }

                function getByUrlFail() {
                    var addMaterialScope = $scope.$new(true);

                    addMaterialScope.material = {};
                    addMaterialScope.material.source = $scope.chapter.resourcePermalink;
                    addMaterialScope.isChapterMaterial = true;
                    storageService.setMaterial(null);

                    $mdDialog.show(angularAMD.route({
                        templateUrl: 'views/addMaterialDialog/addMaterialDialog.html',
                        controllerUrl: 'views/addMaterialDialog/addMaterialDialog',
                        scope: addMaterialScope
                    })).then(closeDialog);

                    function closeDialog(material) {
                        $scope.chapter.resourcePermalink = "";
                        $scope.resourcePermalinkForm.url.$setPristine();
                        $scope.resourcePermalinkForm.url.$setUntouched();
                        if (material) {
                            $scope.chapter.materials.push(material);
                        }
                    }
                }
            }
        }
    }]);
});
