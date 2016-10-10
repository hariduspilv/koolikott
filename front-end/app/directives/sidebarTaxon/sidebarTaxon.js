define([
    'angularAMD',
    'services/recursionHelper',
], function (angularAMD) {
    angularAMD.directive('dopSidebarTaxon', ['RecursionHelper', function (RecursionHelper) {
        return {
            scope: {
                taxon: '='
            },
            templateUrl: 'directives/sidebarTaxon/sidebarTaxon.html',
            compile: function(element) {
              return RecursionHelper.compile(element);
            },
            controller: function ($rootScope, $scope) {

                $scope.toggleChildren = function() {
                    if($scope.opened == null) {
                        $scope.opened = true;
                    } else if ($scope.opened == true) {
                        $scope.opened = false;
                    } else if ($scope.opened == false) {
                        $scope.opened = true;
                    }
                }

                $scope.hasChildren = function(data) {
                    if(data.children.length > 0) {
                        return true;
                    } else {
                        return false;
                    }
                }

                $scope.getTaxonTranslation = function(data) {
                    if(data.level !== '.EducationalContext') {
                        return data.level.toUpperCase().substr(1) + "_" + data.name.toUpperCase();
                    } else {
                        return data.name.toUpperCase();
                    }

                }
            }
        }
    }]);
});
