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
            controller: function ($scope, $rootScope) {
                $scope.learningObjectType = $scope.$parent.learningObjectType;

                function init () {
                    if ($scope.learningObject.type === '.Material') {
                        $scope.materialType = iconService.getMaterialIcon($scope.learningObject.resourceTypes);
                    }
                }

                $scope.pickMaterial = function ($event, material) {
                    $event.preventDefault();
                    $event.stopPropagation();

                    if ($rootScope.selectedMaterials) {
                        var index = $rootScope.selectedMaterials.indexOf(material);
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
                };

                init();
            }
        }
    }
]);
