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
                    $scope.groups = ['PRESCHOOL', 'ZERO_FIVE', 'SIX_SEVEN'];

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
                            if ($scope.targetGroups.indexOf($scope.groups[1]) > -1 &&
                                $scope.targetGroups.indexOf($scope.groups[2]) > -1) {
                                $scope.selectedTargetGroup = 'PRESCHOOL';
                            }
                        }
                    }
                }

            }
        };
    }]);

    return app;
});
