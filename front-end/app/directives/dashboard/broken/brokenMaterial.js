define(['app',], function (app) {
    app.directive('dopBrokenMaterial', function () {
        return {
            templateUrl: 'directives/dashboard/broken/brokenMaterial.html',
            controller: function ($scope, $filter, serverCallService) {
                $scope = $scope.$parent;

                function init() {
                    $scope.title = $filter('translate')('BROKEN_MATERIALS');
                    serverCallService.makeGet("rest/material/getBroken", {}, success, fail);
                }

                function success(data) {
                    if (data) $scope.getItemsSuccess(removeDeletedFromBrokens(data), 'byReportCount', true);

                    else fail();
                }

                function fail() {
                    console.log("Failed to get broken materials")
                }

                //TODO: This should be done in the back-end
                function removeDeletedFromBrokens(brokens) {
                    var notDeletedBrokens = [];
                    brokens.forEach(function (item) {
                        if (!item.material.deleted) {
                            notDeletedBrokens.push(item);
                        }
                    });

                    return notDeletedBrokens;
                }

                init();
            }
        }
    })
});


