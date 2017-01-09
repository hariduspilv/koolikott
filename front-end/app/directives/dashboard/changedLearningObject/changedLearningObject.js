'use strict';

angular.module('koolikottApp')
    .directive('dopChangedLearningObject', changedLearningObject);

function changedLearningObject() {

    function changedLearningObjectCtrl($scope, $filter, changedLearningObjectService, taxonService) {
        const $parent = $scope.$parent;

        function init() {
            $parent.title = $filter('translate')('DASHBOARD_CHANGED_LEARNING_OBJECTS');

            changedLearningObjectService.getChangedList()
                .then((data) => {
                    if (data) {
                        $scope.getItemsSuccess(data, 'byTitle', true);
                    }
                });
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
        controller: ['$scope', '$filter', 'changedLearningObjectService', 'taxonService', changedLearningObjectCtrl]
    }
}
