define([
    'app',
    'angular-material-data-table',
    'services/serverCallService',
    'views/dashboard/baseTable/baseTable'
], function(app) {
    app.controller('brokenMaterialsController', ['$scope', 'serverCallService', '$controller', '$filter',
        function ($scope, serverCallService, $controller, $filter) {
            var base = $controller('baseTableController', { $scope: $scope });

            $scope.title = $filter('translate')('BROKEN_MATERIALS');

            $scope.bindTable = function() {
                base.buildTable('#broken-materials-table', 'views/dashboard/broken/material/brokenMaterial.html');
            };

            serverCallService.makeGet("rest/material/getBroken", {}, getBrokenMaterialsSuccess, base.getItemsFail);

            function getBrokenMaterialsSuccess (brokens) {
                if (brokens) {
                    base.getItemsSuccess(removeDeletedFromBrokens(brokens), 'byReportCount', true);
                } else {
                    base.getItemsFail();
                }
            }

            function removeDeletedFromBrokens(brokens) {
                var notDeletedBrokens = [];
                brokens.forEach(function(item) {
                    if (!item.material.deleted) {
                        notDeletedBrokens.push(item);
                    }
                });

                return notDeletedBrokens;
            }

    }]);
});


