define([
    'angularAMD',
    'services/targetGroupService',
    'directives/selectClose/selectClose'
], function (angularAMD) {
    angularAMD.directive('dopTargetGroupSelector', function () {
        return {
            scope: {
                targetGroups: '=',
                taxon: '=',
                isRequired: '=',
                markRequired: '='
            },
            templateUrl: 'directives/targetGroupSelector/targetGroupSelector.html',
            controller: function ($scope, $rootScope, $timeout, targetGroupService, $translate) {
                init();

                function init() {
                    $scope.selectedTargetGroup = targetGroupService.getLabelByTargetGroups($scope.targetGroups);
                    fill();
                    addListeners();
                    selectValue();
                    $timeout(function () {
                        $scope.isReady = true;
                    });
                }

                function addListeners() {
                    $scope.$watch('selectedTargetGroup', function (newGroup, oldGroup) {
                        if (newGroup !== oldGroup) {
                            debugger;
                            var diff = getDifference(newGroup, oldGroup);
                            var isParent = targetGroupService.isParent(diff.item);
                            if (!isParent) {
                                updateParents();
                                parseSelectedTargetGroup();
                            }
                        }
                        if (!newGroup) {
                            $scope.selectedTargetGroup = [];
                        }
                    }, false);

                    $scope.$watch('taxon', function (newTaxon, oldTaxon) {
                        if (newTaxon !== oldTaxon) {
                            var newEdCtx = $rootScope.taxonUtils.getEducationalContext(newTaxon);
                            var oldEdCtx = $rootScope.taxonUtils.getEducationalContext(oldTaxon);

                            if (!oldEdCtx || (newEdCtx && newEdCtx.name !== oldEdCtx.name) || !newEdCtx) {
                                fill();
                                resetIfInvalid();
                            }
                        }
                    });
                }

                function getDifference(newArray, oldArray) {
                    var result = {};

                    if(newArray != null) {
                        newArray.forEach(function (item) {

                            if(oldArray != null) {
                                if (oldArray.indexOf(item) === -1) {
                                    result.removed = false;
                                    result.item = item;
                                }
                            } else {
                                result.removed = false;
                                result.item = item;
                            }
                        });
                    }
                    if(oldArray != null) {
                        oldArray.forEach(function (item) {
                            if(newArray != null) {
                                if (newArray.indexOf(item) === -1) {
                                    result.removed = true;
                                    result.item = item;
                                }
                            } else {
                                result.removed = true;
                                result.item = item;
                            }

                        });
                    }

                    return result;
                }

                function fill() {
                    $scope.groups = targetGroupService.getAll();
                }

                function parseSelectedTargetGroup() {
                    $scope.targetGroups = targetGroupService.getByLabel($scope.selectedTargetGroup);
                }

                function selectValue() {
                    $scope.selectedTargetGroup = targetGroupService.getLabelByTargetGroups($scope.targetGroups);
                }

                function resetIfInvalid() {
                    var groupNames = [];

                    if ($scope.groups) {
                        $scope.groups.forEach(function (group) {
                            if (group) {
                                groupNames.push(group);
                            }
                        });
                    }

                    if (groupNames.indexOf($scope.selectedTargetGroup) === -1 || !$scope.groups) {
                        $scope.selectedTargetGroup = null;
                        $scope.targetGroups = [];
                        if ($scope.groups && $scope.groups.length === 1) {
                            $scope.selectedTargetGroup =  $scope.groups;
                        }
                    }
                }

                $scope.parentClick = function(e) {
                    if(!$scope.selectedTargetGroup) {
                        $scope.selectedTargetGroup = [];
                    }

                    if(e.group && $scope.selectedTargetGroup.indexOf(e.group.label) == -1) {
                        added = true;
                    } else {
                        added = $scope.selectedTargetGroup === [];
                    }

                    if(added) {
                        addMissingGrades(e.group.children);
                    } else {
                        removeGrades(e.group.children);
                    }

                    parseSelectedTargetGroup();
                };

                // Reduced text for select label
                $scope.getSelectedText = function() {
                    if ($scope.targetGroups && $scope.targetGroups.length > 0) {
                        return targetGroupService.getSelectedText($scope.targetGroups);
                    } else {
                        return $translate.instant('DETAILED_SEARCH_TARGET_GROUP');
                    }
                };

                function getMissingGrades(selectedTargetGroup, targetGroups) {
                    var result = [];
                    for (var i = 0; i < targetGroups.length; i++) {
                        if (selectedTargetGroup.indexOf(targetGroups[i]) == -1) {
                            result.push(targetGroups[i]);
                        }
                    }
                    return result;
                }

                function addMissingGrades(targetGroups) {
                    if(!$scope.selectedTargetGroup) {
                        $scope.selectedTargetGroup = [];
                    }
                    var grades = getMissingGrades($scope.selectedTargetGroup,targetGroups);
                    for (var i = 0; i < grades.length; i++) {
                        $scope.selectedTargetGroup.push(grades[i]);
                    }
                }

                function removeGrades(items) {
                    for (var i = 0; i < items.length; i++) {
                        var index = $scope.selectedTargetGroup.indexOf(items[i]);
                        $scope.selectedTargetGroup.splice(index, 1);
                    }
                }

                function updateParents() {
                    for(var i = 0; i < $scope.groups.length; i++) {
                        var hasChildren = targetGroupService.hasAllChildren($scope.groups[i], $scope.selectedTargetGroup);

                        if(hasChildren) {
                            if($scope.selectedTargetGroup.indexOf($scope.groups[i].label) == -1) {
                                $scope.selectedTargetGroup.push($scope.groups[i].label);
                            }
                        } else {
                            if($scope.selectedTargetGroup) {
                                var index = $scope.selectedTargetGroup.indexOf($scope.groups[i].label);
                                if(index != -1) {
                                    $scope.selectedTargetGroup.splice(index, 1);
                                }
                            }
                        }

                    }
                }

                $scope.update = function() {
                    if(!$scope.selectedTargetGroup) {
                        $scope.selectedTargetGroup = targetGroupService.getLabelByTargetGroups($scope.targetGroups);
                    }
                }
            }
        };
    });
});
