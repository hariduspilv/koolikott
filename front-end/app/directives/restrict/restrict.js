'use strict'

angular.module('koolikottApp')
.directive('dopRestrict',
[
    'translationService', 'authenticatedUserService', 'serverCallService', 'toastService',
    function (translationService, authenticatedUserService, serverCallService, toastService) {
        return {
            scope: {
                learningObject: '='
            },
            templateUrl: 'directives/restrict/restrict.html',
            controller: function ($scope) {
                if ($scope.learningObject && $scope.learningObject.creator) {
                    $scope.isCreatorRestricted = isUserRestricted($scope.learningObject.creator);
                }

                $scope.restrictCreator = function() {
                    serverCallService.makePost("rest/user/restrictUser", $scope.learningObject.creator, restrictSuccess, restrictFail)
                };

                function restrictSuccess(user) {
                    if (isUserRestricted(user)) {
                        $scope.isCreatorRestricted = true;
                        toastService.show('USER_RESTRICTED');
                    } else {
                        restrictFail();
                    }
                }

                function restrictFail() {
                    console.log("Setting creators role to 'RESTRICTED' failed");
                }

                $scope.removeCreatorsRestriction = function() {
                    serverCallService.makePost("rest/user/removeRestriction", $scope.learningObject.creator, restrictRemoveSuccess, restrictRemoveFail)

                };

                function restrictRemoveSuccess(user) {
                    if (user.role === "USER") {
                        $scope.isCreatorRestricted = false;
                        toastService.show('USER_RESTRICTION_REMOVED');
                    } else {
                        restrictRemoveFail();
                    }
                }

                function restrictRemoveFail() {
                    console.log("Removing creators 'RESTRICTED' role has failed");
                }

                function isUserRestricted(user) {
                    return user.role === 'RESTRICTED';
                }
            }
        };
    }
]);
