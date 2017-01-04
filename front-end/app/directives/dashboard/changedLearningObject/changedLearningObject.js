'use strict';

angular.module('koolikottApp')
    .directive('dopChangedLearningObject', changedLearningObject);

function changedLearningObject() {

    function changedLearningObjectCtrl($scope, $filter, serverCallService) {
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
            if (!taxon || !taxon.name) return "";
            // Level names start with '.' which has to be removed
            var prefix = taxon.level.substring(1).toUpperCase();
            return prefix + "_" + taxon.name.toUpperCase();
        }

        init();

        // exports
        this.getTaxonTranslationKey = getTaxonTranslationKey;
    }

    return {
        templateUrl: 'directives/dashboard/changedLearningObject/changed.html',
        controller: ['$scope', '$filter', 'serverCallService', changedLearningObjectCtrl]
    }
}
