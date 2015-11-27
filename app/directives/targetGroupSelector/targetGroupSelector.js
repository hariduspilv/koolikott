define(['app'], function(app)
{	
    app.directive('dopTargetGroupSelector', [
     function() {
        return {
            scope: {
                targetGroups: '='
            },
            templateUrl: 'directives/targetGroupSelector/targetGroupSelector.html',
            controller: function ($scope) {
            	
                init();

                function init() {
                    $scope.groups = ['PRESCHOOL', 'ZERO_FIVE', 'SIX_SEVEN', 'BASICEDUCATION', 'GRADE1', 'GRADE2', 
                                     'GRADE3', 'GRADE4', 'GRADE5', 'GRADE6', 'GRADE7', 'GRADE8', 'GRADE9'];

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
            	}
            	
            	function parseSelectedTargetGroup() {
            		if ($scope.selectedTargetGroup === 'PRESCHOOL') {
                        $scope.targetGroups = [];
                        $scope.targetGroups.push($scope.groups[1]);
                        $scope.targetGroups.push($scope.groups[2]);
                    } else if ($scope.selectedTargetGroup === 'BASICEDUCATION') {
                        $scope.targetGroups = [];
                        for (i = 0; i < 9; i++) { 
                            $scope.targetGroups.push($scope.groups[i + 4]);
                        }
                    } else {
                        $scope.targetGroups = [];
                        $scope.targetGroups.push($scope.selectedTargetGroup);
                    }
            	}

                function selectValue() {
                    if ($scope.targetGroups) {
                        // Check if values are valid
                        var invalid = [];
                        for (i = 0; i < $scope.targetGroups.length; i++) {
                            if ($scope.groups.indexOf($scope.targetGroups[i]) === -1) {
                                invalid.push($scope.targetGroups[i]);
                            }
                        }

                        // Remove invalid
                        for (i = 0; i < invalid.length; i++) {
                            var index = $scope.targetGroups.indexOf(invalid[i]);
                            $scope.targetGroups.splice(index, 1);
                        }

                        // Select correct value
                        if ($scope.targetGroups.length === 1) {
                            $scope.selectedTargetGroup = $scope.targetGroups[0];
                        } else if ($scope.targetGroups.length === 2) {
                            if ($scope.targetGroups.indexOf('ZERO_FIVE') > -1 &&
                                $scope.targetGroups.indexOf('SIX_SEVEN') > -1) {
                                $scope.selectedTargetGroup = 'PRESCHOOL';
                            }
                        } else if ($scope.targetGroups.length === 9) {
                            // Contains Grades 1 to 9
                            var containsGrades = true;
                            for (i = 0; i < 9; i++) {
                                if ($scope.targetGroups.indexOf($scope.groups[i + 4]) === -1) {
                                    containsGrades = false;
                                }
                            }

                            if (containsGrades) {
                                $scope.selectedTargetGroup = 'BASICEDUCATION';
                            }
                        }
                    }
                }

            }
        };
    }]);

    return app;
});
