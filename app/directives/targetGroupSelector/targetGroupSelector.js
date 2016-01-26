define(['angularAMD'], function(angularAMD) {
    angularAMD.directive('dopTargetGroupSelector', function() {
        return {
            scope: {
                targetGroups: '=',
                taxon: '='
            },
            templateUrl: 'directives/targetGroupSelector/targetGroupSelector.html',
            controller: function($scope, $rootScope) {

                var preschoolGroups = ['PRESCHOOL', 'ZERO_FIVE', 'SIX_SEVEN'];
                var level1Groups = ['LEVEL1', 'GRADE1', 'GRADE2', 'GRADE3'];
                var level2Groups = ['LEVEL2', 'GRADE4', 'GRADE5', 'GRADE6'];
                var level3Groups = ['LEVEL3', 'GRADE7', 'GRADE8', 'GRADE9'];
                var secondaryGroups = ['GYMNASIUM'];

                init();

                function init() {
                    fill();
                    addListeners();
                    selectValue();
                }

                function addListeners() {
                    $scope.$watch('selectedTargetGroup', function(newGroup, oldGroup) {
                        if (newGroup !== oldGroup) {
                            parseSelectedTargetGroup();
                        }
                    }, false);

                    $scope.$watch('targetGroups', function(newGroups, oldGroups) {
                        if (newGroups !== oldGroups) {
                            // Check that input is an array
                            if (!Array.isArray(newGroups)) {
                                $scope.targetGroups = [];
                                if (newGroups) {
                                    $scope.targetGroups.push(newGroups);
                                }
                            }

                            selectValue();
                        }
                    }, false);

                    $scope.$watch('taxon', function(newTaxon, oldTaxon) {
                        if (newTaxon !== oldTaxon) {
                            var newEdCtx = $rootScope.taxonUtils.getEducationalContext(newTaxon);
                            var oldEdCtx = $rootScope.taxonUtils.getEducationalContext(oldTaxon);

                            if (!oldEdCtx || newEdCtx.name !== oldEdCtx.name) {
                                fill();
                            }
                        }
                    });
                }

                function fill() {
                    var educationalContext = $rootScope.taxonUtils.getEducationalContext($scope.taxon);

                    if (educationalContext) {
                        if (educationalContext.name === 'PRESCHOOLEDUCATION') {
                            $scope.groups = map(preschoolGroups);
                        } else if (educationalContext.name === 'BASICEDUCATION') {
                            $scope.groups = map(level1Groups).concat(map(level2Groups), map(level3Groups));
                        } else if (educationalContext.name === 'SECONDARYEDUCATION') {
                            $scope.groups = map(secondaryGroups);
                        }
                    } else {
                        $scope.groups = map(preschoolGroups).concat(map(level1Groups), map(level2Groups), map(level3Groups), map(secondaryGroups));
                    }
                }

                function map(group) {
                    return group.map(function(item, index) {
                        return {
                            name: item,
                            parent: index === 0
                        };
                    });
                }

                function parseSelectedTargetGroup() {
                    switch ($scope.selectedTargetGroup) {
                        case 'PRESCHOOL':
                            $scope.targetGroups = preschoolGroups.slice();
                            $scope.targetGroups.splice(0, 1); // Remove PRESCHOOL label
                            break;
                        case 'LEVEL1':
                            $scope.targetGroups = level1Groups.slice();
                            $scope.targetGroups.splice(0, 1);
                            break;
                        case 'LEVEL2':
                            $scope.targetGroups = level2Groups.slice();
                            $scope.targetGroups.splice(0, 1);
                            break;
                        case 'LEVEL3':
                            $scope.targetGroups = level3Groups.slice();
                            $scope.targetGroups.splice(0, 1);
                            break;
                        default:
                            $scope.targetGroups = [];
                            $scope.targetGroups.push($scope.selectedTargetGroup);
                            break;
                    }
                }

                function selectValue() {
                    if ($scope.targetGroups) {
                        // Select correct value
                        if ($scope.targetGroups.length === 1) {
                            $scope.selectedTargetGroup = $scope.targetGroups[0];
                        } else if ($scope.targetGroups.length === 2) {
                            if ($scope.targetGroups.indexOf('ZERO_FIVE') > -1 &&
                                $scope.targetGroups.indexOf('SIX_SEVEN') > -1) {
                                $scope.selectedTargetGroup = 'PRESCHOOL';
                            }
                        } else if ($scope.targetGroups.length === 3) {
                            if ($scope.targetGroups.indexOf('GRADE1') > -1 &&
                                $scope.targetGroups.indexOf('GRADE2') > -1 &&
                                $scope.targetGroups.indexOf('GRADE3') > -1) {
                                $scope.selectedTargetGroup = 'LEVEL1';
                            }

                            if ($scope.targetGroups.indexOf('GRADE4') > -1 &&
                                $scope.targetGroups.indexOf('GRADE5') > -1 &&
                                $scope.targetGroups.indexOf('GRADE6') > -1) {
                                $scope.selectedTargetGroup = 'LEVEL2';
                            }

                            if ($scope.targetGroups.indexOf('GRADE7') > -1 &&
                                $scope.targetGroups.indexOf('GRADE8') > -1 &&
                                $scope.targetGroups.indexOf('GRADE9') > -1) {
                                $scope.selectedTargetGroup = 'LEVEL3';
                            }
                        } else {
                            $scope.selectedTargetGroup = null;
                        }
                    } else {
                        $scope.selectedTargetGroup = null;
                    }
                }

            }
        };
    });
});