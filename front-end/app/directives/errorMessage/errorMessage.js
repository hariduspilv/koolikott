'use strict'

angular.module('koolikottApp')
.directive('dopErrorMessage',
[
    '$location', 'serverCallService', 'userDataService', '$timeout', 'changedLearningObjectService', '$routeParams', 'taxonService',
    function ($location, serverCallService, userDataService, $timeout, changedLearningObjectService, $routeParams, taxonService) {
        return {
            scope: {
                data: '='
            },
            templateUrl: 'directives/errorMessage/errorMessage.html',
            controller: ['$rootScope', '$scope', function ($rootScope, $scope) {

                $timeout(function () {
                    setChangedData();
                });

                $scope.$on("errorMessage:updateChanged", function() {
                    setChangedData();
                });

                $scope.$watch($scope.showChanged, function(newValue, oldValue) {
                    if (newValue != oldValue && newValue === true) {
                        setChangedData();
                    }
                });

                function getChangedData() {
                    return changedLearningObjectService.getChangedData(getCurrentLearningObjectId());
                }

                function setChangedData() {
                    if ($scope.showChanged()) {
                        getChangedData().then(function(response) {
                            $scope.changes = response
                        })
                    }
                }

                function getCurrentLearningObjectId() {
                    if ($scope.$parent.material) {
                        return $scope.$parent.material.id;
                    } else if ($scope.$parent.portfolio) {
                        return $scope.$parent.portfolio.id;
                    } else {
                        return $routeParams.id;
                    }
                }

                $scope.getChangeType = function (item) {
                    if (item.taxon) return "DETAIL_VIEW_DOMAIN";
                    else if (item.resourceType) return "MATERIAL_VIEW_RESOURCE_TYPE";
                    else if (item.targetGroup) return "DETAIL_VIEW_TARGET_GROUP";
                    else return "";
                };

                $scope.getChangeName = function (item) {
                    if (item.taxon) return taxonService.getTaxonTranslationKey(item.taxon);
                    else if (item.resourceType) return item.resourceType.name;
                    else if (item.targetGroup) return "TARGET_GROUP_" + item.targetGroup;
                    else return "";
                };

                $scope.acceptChanges = function () {
                    changedLearningObjectService.acceptChanges(getCurrentLearningObjectId());
                };

                $scope.revertChanges = function () {
                    changedLearningObjectService.revertChanges(getCurrentLearningObjectId());
                };

                $rootScope.setReason = function (reason) {
                    $scope.reason = reason;
                };

                $scope.showChanged = function () {
                    return $rootScope.learningObjectDeleted == false
                        && $rootScope.learningObjectImproper == false
                        && $rootScope.learningObjectBroken == false
                        && $rootScope.learningObjectChanged == true;
                };

                $scope.showBroken = function () {
                    return $rootScope.learningObjectDeleted == false
                        && $rootScope.learningObjectImproper == false
                        && $rootScope.learningObjectBroken == true;
                };

                $scope.showImproper = function () {
                    return $rootScope.learningObjectDeleted == false
                        && $rootScope.learningObjectBroken == false
                        && $rootScope.learningObjectImproper == true;
                };

                $scope.showImproperAndBroken = function () {
                    return $rootScope.learningObjectDeleted == false
                        && $rootScope.learningObjectBroken == true
                        && $rootScope.learningObjectImproper == true;
                };

                $scope.showDeleted = function () {
                    return $rootScope.learningObjectDeleted == true;
                };

                $scope.restoreLearningObject = function () {
                    $scope.$emit("restore:learningObject");
                };

                $scope.deleteLearningObject = function () {
                    $scope.$emit("delete:learningObject");
                };

                $scope.setNotImproperLearningObject = function () {
                    $scope.$emit("setNotImproper:learningObject");
                };

                $scope.markCorrectMaterial = function () {
                    $scope.$emit("markCorrect:learningObject");
                };
            }]
        }
    }
]);
