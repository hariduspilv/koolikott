'use strict';

angular.module('koolikottApp')
    .directive('dopChangedLearningObject', changedLearningObject);

function changedLearningObject() {

    function changedLearningObjectCtrl($scope, $filter, serverCallService, taxonService) {
        var $parent = $scope.$parent;

        function init() {
            $parent.title = $filter('translate')('DASHBOARD_CHANGED_LEARNING_OBJECTS');
            serverCallService.makeGet("rest/changed", {}, success, fail);
        }

        function success(data) {
            if (data) {
                $scope.getItemsSuccess(data, 'byTitle', true);
            } else {
                fail();
            }
        }

        function fail() {
            console.log("Failed to get improper materials")
        }

        function getTaxonTranslationKey(taxon) {
            return taxonService.getTaxonTranslationKey(taxon);
        }

        init();

        // exports
        this.getTaxonTranslationKey = getTaxonTranslationKey;
    }

    return {
        templateUrl: 'directives/dashboard/changedLearningObject/changed.html',
        controller: ['$scope', '$filter', 'serverCallService', 'taxonService', changedLearningObjectCtrl]
    }
}
