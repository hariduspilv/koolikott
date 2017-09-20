'use strict'

angular.module('koolikottApp').directive('dopUnReviewed', function () {
    return {
        templateUrl: 'directives/dashboard/unReviewed/unReviewed.html',
        controller: ['$scope', '$filter', 'serverCallService', function ($scope, $filter, serverCallService) {
            const $parent = $scope.$parent

            function init() {
                $parent.title = $filter('translate')('DASHBOARD_UNREVIEWED')
                serverCallService.makeGet("rest/admin/firstReview/unReviewed", {}, success, fail)
            }

            function success(data) {
                console.log('data:', data)
                data
                    ? $parent.getItemsSuccess(data, 'byAddedAt', true)
                    : fail()
            }

            function fail() {
                console.log("Failed to get unreviewed materials")
            }

            init()
        }]
    }
})
