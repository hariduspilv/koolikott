'use strict'

angular.module('koolikottApp')
.directive('dopErrorMessage',
[
    '$location', 'serverCallService', 'userDataService',
    function ($location, serverCallService, userDataService) {
        return {
            scope: {
                data: '='
            },
            templateUrl: 'directives/errorMessage/errorMessage.html',
            controller: function ($rootScope, $scope) {

                $rootScope.setReason = function (reason) {
                    $scope.reason = reason;
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
                }
            }
        }
    }
]);
