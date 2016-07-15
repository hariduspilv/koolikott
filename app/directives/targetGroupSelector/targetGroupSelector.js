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
                            parseSelectedTargetGroup();
                        }
                    }, false);

                    $scope.$watch('targetGroups', function (newGroups, oldGroups) {
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
                            $scope.targetGroupForm.targetGroup.$setPristine();
                        }
                    }
                }

            }
        };
    });
});
