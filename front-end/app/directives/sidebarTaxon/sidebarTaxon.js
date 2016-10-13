define([
    'angularAMD',
    'services/recursionHelper',
], function (angularAMD) {
    angularAMD.directive('dopSidebarTaxon', ['RecursionHelper', function (RecursionHelper) {
        return {
            scope: {
                taxon: '=',
                icon: '=',
                tree: '='
            },
            templateUrl: 'directives/sidebarTaxon/sidebarTaxon.html',
            compile: function(element) {
              return RecursionHelper.compile(element);
            },
            controller: function ($rootScope, $scope) {

                if ($scope.taxon) {
                    if ($scope.taxon.children.length > 0) {
                        $scope.hasChildren = true;
                        $scope.childrenCount = $scope.taxon.children.length;
                    }

                    if($scope.tree) {
                        if ($scope.taxon.id == $scope.tree.id) {
                            $scope.opened = true;
                        }
                    }

                }

                $scope.toggleChildren = function() {
                    if($scope.opened == null) {
                        $scope.opened = true;
                    } else if ($scope.opened == true) {
                        $scope.opened = false;
                    } else if ($scope.opened == false) {
                        $scope.opened = true;
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
