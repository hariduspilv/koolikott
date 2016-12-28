'use strict'

angular.module('koolikottApp')
.directive('dopCardMedia',
[
    'iconService',
    function (iconService) {
        return {
            scope: {
                learningObject: '=',
                isAuthenticated: '='
            },
            templateUrl: 'directives/card/cardMedia/cardMedia.html',
            controller: ['$scope', '$rootScope', function ($scope, $rootScope) {

                function init () {
                    if ($scope.learningObject.type === '.Material') {
                        $scope.learningObjectType = 'material';
                        $scope.materialType = iconService.getMaterialIcon($scope.learningObject.resourceTypes);
                    } else if ($scope.learningObject.type === '.Portfolio') {
                        $scope.learningObjectType = 'portfolio';
                    }
                }

                $scope.pickMaterial = function ($event, material) {
                    $event.preventDefault();
                    $event.stopPropagation();

                    if ($rootScope.selectedMaterials) {
                        let index = $rootScope.selectedMaterials.indexOf(material);
                        if (index == -1) {
                            $rootScope.selectedMaterials.push(material);
                            material.selected = true;
                        } else {
                            $rootScope.selectedMaterials.splice(index, 1);
                            material.selected = false;
                        }
                    } else {
                        $rootScope.selectedMaterials = [];
                        $rootScope.selectedMaterials.push(material);
                        material.selected = true;
                    }

                    $rootScope.$broadcast("detailedSearch:close");
                };

                init();
            }]
        }
    }
]);
