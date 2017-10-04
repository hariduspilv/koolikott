'use strict'

angular.module('koolikottApp').directive('dopErrorMessage', [
    'serverCallService',
    '$timeout',
    'changedLearningObjectService',
    '$routeParams',
    'taxonService',
    'authenticatedUserService',
    function (
        serverCallService,
        $timeout,
        changedLearningObjectService,
        $routeParams,
        taxonService,
        authenticatedUserService
    ) {
        return {
            scope: {
                data: '='
            },
            templateUrl: 'directives/errorMessage/errorMessage.html',
            controller: ['$rootScope', '$scope', function ($rootScope, $scope) {
                function init() {
                    var show = function (icon, messageKey, buttons, cb) {
                        $scope.show = true
                        $scope.icon = icon
                        $scope.messageKey = messageKey
                        $scope.buttons = buttons

                        if (typeof cb === 'function')
                            cb()
                    }
                    show('', '', [])
                    $scope.show = false

                    console.log('$rootScope:', $rootScope)

                    $scope.isAdmin = authenticatedUserService.isAdmin()
                    $scope.isModerator = authenticatedUserService.isModerator()

                    $scope.showDeleted =
                        $rootScope.learningObjectDeleted
                    $scope.showBroken =
                        !$rootScope.learningObjectDeleted &&
                        !$rootScope.learningObjectImproper &&
                        $rootScope.learningObjectBroken
                    $scope.showImproper =
                        !$rootScope.learningObjectDeleted &&
                        $rootScope.learningObjectImproper &&
                        !$rootScope.learningObjectBroken
                    $scope.showImproperAndBroken =
                        !$rootScope.learningObjectDeleted &&
                        $rootScope.learningObjectImproper &&
                        $rootScope.learningObjectBroken
                    $scope.showChanged =
                        !$rootScope.learningObjectDeleted &&
                        !$rootScope.learningObjectImproper &&
                        !$rootScope.learningObjectBroken &&
                        $rootScope.learningObjectChanged
                    $scope.showUnreviewed =
                        !$rootScope.learningObjectDeleted &&
                        !$rootScope.learningObjectImproper &&
                        !$rootScope.learningObjectBroken &&
                        !$rootScope.learningObjectChanged &&
                        $rootScope.learningObjectUnreviewed
                    /*$scope.showUnreviewedAndImproper =
                        !$rootScope.learningObjectDeleted &&
                        $rootScope.learningObjectImproper &&
                        !$rootScope.learningObjectBroken &&
                        $rootScope.learningObjectUnreviewed
                    $scope.showUnreviewedAndBroken =
                        !$rootScope.learningObjectDeleted &&
                        !$rootScope.learningObjectImproper &&
                        $rootScope.learningObjectBroken &&
                        $rootScope.learningObjectUnreviewed
                    $scope.showUnreviewedAndImproperAndBroken =
                        !$rootScope.learningObjectDeleted
                        $rootScope.learningObjectImproper &&
                        $rootScope.learningObjectBroken &&
                        $rootScope.learningObjectUnreviewed &&*/

                    if ($scope.showDeleted)
                        show('delete', 'ERROR_MSG_DELETED', [{
                            icon: 'restore_page',
                            label: 'BUTTON_RESTORE',
                            onClick: restoreLearningObject,
                            show: $scope.isAdmin,
                        }])

                    else if ($scope.showChanged)
                        show('priority_high', 'MESSAGE_CHANGED_LEARNING_OBJECT', [{
                            icon: 'undo',
                            label: 'UNDO_CHANGES',
                            onClick: revertChanges,
                            show: $scope.isAdmin,
                        }, {
                            icon: 'done',
                            label: 'ACCEPT_CHANGES',
                            onClick: acceptChanges,
                            show: $scope.isAdmin,
                        }])

                    else if ($scope.showBroken)
                        show('report', 'ERROR_MSG_BROKEN', [{
                            icon: 'delete',
                            label: 'BUTTON_REMOVE',
                            onClick: $scope.$parent.confirmMaterialDeletion,
                            show: $scope.isAdmin || $scope.isModerator,
                        }, {
                            icon: 'done',
                            label: 'REPORT_BROKEN_LINK_CORRECT',
                            onClick: markCorrectMaterial,
                            show: $scope.isAdmin,
                        }])

                    else if ($scope.showImproper)
                        show('report', 'ERROR_MSG_IMPROPER', [{
                            icon: 'delete',
                            label: 'BUTTON_REMOVE',
                            onClick: deleteLearningObject,
                            show: $scope.isAdmin || $scope.isModerator,
                        }, {
                            icon: 'done',
                            label: 'REPORT_NOT_IMPROPER',
                            onClick: setNotImproperLearningObject,
                            show: $scope.isAdmin || $scope.isModerator,
                        }], getReasons)

                    else if ($scope.showImproperAndBroken)
                        show('report', 'ERROR_MSG_IMPROPER_AND_BROKEN', [{
                            icon: 'delete',
                            label: 'BUTTON_REMOVE',
                            onClick: deleteLearningObject,
                            show: $scope.isAdmin || $scope.isModerator,
                        }, {
                            icon: 'done',
                            label: 'REPORT_NOT_IMPROPER',
                            onClick: function () {
                                setNotImproperLearningObject()
                                markCorrectMaterial()
                            },
                            show: $scope.isAdmin,
                        }], getReasons)

                    else if ($scope.showUnreviewed) {
                        var messageKey = $scope.data && $scope.data.type == '.Portfolio'
                            ? 'ERROR_MSG_UNREVIEWED_PORTFOLIO'
                            : 'ERROR_MSG_UNREVIEWED'
                        show('lightbulb_outline', messageKey, [{
                            icon: 'done',
                            label: 'BUTTON_REVIEW',
                            onClick: markLearningObjectReviewed,
                            show: $scope.isAdmin || $scope.isModerator,
                        }])
                    }
                }

                init()
                $scope.$watch('data', function (newData, oldData) {
                    if (newData && (!oldData || newData.id != oldData.id))
                        init()
                })
                $scope.$on('dashboard:adminCountsUpdated', init)
                $rootScope.$watch('learningObjectChanged', function(newValue, oldValue) {
                    if (newValue != oldValue)
                        init()
                })

                function getReasons() {
                    if ($scope.data && $scope.data.id)
                        serverCallService.makeGet('rest/impropers/'+$scope.data.id, {}, function (reports) {
                            $scope.reasons = reports.map(function (report) {
                                return report.reason
                            })
                        })
                }

                $scope.toggleReasons = function (evt) {
                    evt.preventDefault()
                    if (!$scope.showReasons) {
                        var reasons = document.querySelector('.error-message-reasons')
                        reasons.style.height = reasons.scrollHeight+'px'
                        $scope.showReasons = true
                    } else
                        $scope.showReasons = false
                }

                /**
                 * @todo
                 */
                /*$timeout(setChangedData);

                $scope.$on("errorMessage:updateChanged", setChangedData);

                function setChangedData() {
                    if ($scope.showChanged)
                        changedLearningObjectService
                            .getChangedData(getCurrentLearningObjectId())
                            .then(function (response) {
                                console.log('changed:', response.map(res => JSON.stringify(res, null, 2)))
                                // $scope.reasons = response
                            })
                }*/

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

                function acceptChanges() {
                    changedLearningObjectService.acceptChanges(getCurrentLearningObjectId());
                };

                function revertChanges() {
                    changedLearningObjectService.revertChanges(getCurrentLearningObjectId());
                };

                $rootScope.setReason = function (reason) {
                    $scope.reason = reason;
                };

                function restoreLearningObject() {
                    $scope.$emit("restore:learningObject");
                };

                function deleteLearningObject() {
                    $scope.$emit("delete:learningObject");
                };

                function setNotImproperLearningObject() {
                    $scope.$emit("setNotImproper:learningObject");
                };

                function markCorrectMaterial() {
                    $scope.$emit("markCorrect:learningObject");
                };

                function markLearningObjectReviewed() {
                    $scope.$emit("markReviewed:learningObject");
                }
            }]
        }
    }
]);
