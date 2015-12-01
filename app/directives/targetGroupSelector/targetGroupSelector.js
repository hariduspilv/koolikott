define(['app'], function(app)
{	
    app.directive('dopTargetGroupSelector', [
     function() {
        return {
            scope: {
                targetGroups: '=',
                educationalContext: '='
            },
            templateUrl: 'directives/targetGroupSelector/targetGroupSelector.html',
            controller: function ($scope) {

                var preschoolGroups = ['PRESCHOOL', 'ZERO_FIVE', 'SIX_SEVEN'];
                var level1Groups = ['LEVEL1', 'GRADE1', 'GRADE2', 'GRADE3'];
                var level2Groups = ['LEVEL2', 'GRADE4', 'GRADE5', 'GRADE6'];
                var level3Groups = ['LEVEL3', 'GRADE7', 'GRADE8', 'GRADE9'];
                var secondaryGroups = ['GYMNASIUM'];
                var vocationalGroups = ['VOCATIONAL'];

                init();

                function init() {
                    fill();
                    addListeners();
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
                                var group = newGroups;
                                $scope.targetGroups = [];
                                $scope.targetGroups.push(group);
                            }

                            selectValue();
                        }
                    }, false);

                    $scope.$watch('educationalContext', function(newContext, oldContext) {
                        if (newContext !== oldContext) {
                            fill();
                        }
                    }, false);
            	}

                function fill() {
                    if ($scope.educationalContext) {
                        switch ($scope.educationalContext.id) {
                            case 1:
                                $scope.groups = preschoolGroups.slice();
                                break;
                            case 2:
                                $scope.groups = level1Groups.concat(level2Groups, level3Groups);
                                break;
                            case 3:
                                $scope.groups = secondaryGroups.slice();
                                break;
                            case 4:
                                $scope.groups = vocationalGroups.slice();
                                break;
                            default:
                                $scope.groups = [];
                                break;
                        }

                        // Clear if the selected value does not belong under this educational context
                        if (!scope.groups.indexOf($scope.selectedTargetGroup) === -1) {
                            $scope.selectedTargetGroup = null;
                        }
                    } else {
                        $scope.groups = preschoolGroups.concat(level1Groups, level2Groups, level3Groups, secondaryGroups, vocationalGroups);
                    }
                }
            	
            	function parseSelectedTargetGroup() {
                    switch($scope.selectedTargetGroup) {
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
                        }
                    }
                }

            }
        };
    }]);

    return app;
});
