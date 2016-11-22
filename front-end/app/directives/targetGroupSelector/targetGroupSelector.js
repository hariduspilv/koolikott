define([
    'angularAMD',
    'services/targetGroupService'
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
            controller: function ($scope, $rootScope, $timeout, targetGroupService) {
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
                            var diff = getDifference(newGroup, oldGroup);
                            var isParent = targetGroupService.isParent(diff.item);
                            if (!isParent) {
                                updateParents();
                                parseSelectedTargetGroup();
                            }
                        }
                    }, false);

                    $scope.$watch('targetGroups', function (newGroups, oldGroups) {
                        if (newGroups !== oldGroups) {
                            // Check that input is an array
                            if (!Array.isArray(newGroups)) {
                                $scope.targetGroups = [];
                                if (newGroups) {
                                    $scope.targetGroups = newGroups;
                                }
                            }

                            //selectValue();
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
                    var i = {};

                    if(newArray != null) {
                        newArray.forEach(function (item) {

                            if(oldArray != null) {
                                if (oldArray.indexOf(item) === -1) {
                                    i.removed = false;
                                    i.item = item;
                                }
                            } else {
                                i.removed = false;
                                i.item = item;
                            }
                        });
                    }
                    if(oldArray != null) {
                        oldArray.forEach(function (item) {
                            if(newArray != null) {
                                if (newArray.indexOf(item) === -1) {
                                    i.removed = true;
                                    i.item = item;
                                }
                            } else {
                                i.removed = true;
                                i.item = item;
                            }

                        });
                    }

                    return i;
                }

                function fill() {
                    var educationalContext = $rootScope.taxonUtils.getEducationalContext($scope.taxon);

                    if (educationalContext) {
                        $scope.groups = targetGroupService.getByEducationalContext(educationalContext);
                    } else {
                        $scope.groups = targetGroupService.getAll();
                    }
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
                            if (group && group.name) {
                                groupNames.push(group.name);
                            }
                        });
                    }

                    if (groupNames.indexOf($scope.selectedTargetGroup) === -1 || !$scope.groups) {
                        $scope.selectedTargetGroup = null;
                        $scope.targetGroups = [];
                        if ($scope.groups && $scope.groups.length === 1) {
                            $scope.selectedTargetGroup =  $scope.groups[0].name;
                        }
                    }
                }

                $scope.parentClick = function(e) {

                    if($scope.selectedTargetGroup.indexOf(e.$parent.group.label) == -1) {
                        added = true;
                    } else if ($scope.selectedTargetGroup === []){
                        added = true;
                    } else {
                        added = false;
                    }

                    if(added) {
                        addMissingGrades(e.$parent.group.children);
                    } else {
                        removeGrades(e.$parent.group.children);
                    }
                    parseSelectedTargetGroup();
                };

                $scope.getSelectedText = function() {
                    if ($scope.targetGroups && $scope.targetGroups.length > 0) {
                        return "you might have selected: " + $scope.targetGroups;
                    } else {
                        return "Translation";
                    }
                };

                function getMissingGrades(array, items) {
                    var result = [];
                    for (var i = 0; i < items.length; i++) {
                        if (array.indexOf(items[i]) == -1) {
                            result.push(items[i]);
                        }
                    }
                    return result;
                }

                function addMissingGrades(items) {
                    if(!$scope.selectedTargetGroup) {
                        $scope.selectedTargetGroup = [];
                    }
                    var grades = getMissingGrades($scope.selectedTargetGroup,items);
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
                            var index = $scope.selectedTargetGroup.indexOf($scope.groups[i].label);
                            if(index != -1) {
                                $scope.selectedTargetGroup.splice(index, 1);
                            }
                        }

                    }
                }

            }
        };
    });
});
