define([
    'angularAMD',
    'services/recursionHelper',
], function (angularAMD) {
    angularAMD.directive('dopSidebarTaxon', ['RecursionHelper', '$location', function (RecursionHelper, $location) {
        return {
            scope: {
                taxon: '=',
                icon: '='
            },
            templateUrl: 'directives/sidebarTaxon/sidebarTaxon.html',
            compile: function(element) {
              return RecursionHelper.compile(element);
            },
            controller: function ($rootScope, $scope, $location) {
                $scope.id;

                if ($scope.taxon) {
                    $scope.id = $scope.taxon.id;

                    if (($scope.taxon.domains !== undefined && $scope.taxon.domains !== null) && $scope.taxon.domains.length > 0) {
                        $scope.taxonChildren = $scope.taxon.domains;
                        $scope.childrenCount = $scope.taxon.domains.length;
                        $scope.hasChildren = true;
                    }

                    if ($scope.taxon.educationalContext !== undefined && $scope.taxon.educationalContext !== null) {
                        if ($scope.taxon.educationalContext.name === 'PRESCHOOLEDUCATION') {
                            checkTaxonLevelAndAssignValues('.Domain', $scope.taxon.topics);
                        }

                        if ($scope.taxon.educationalContext.name === 'BASICEDUCATION' || $scope.taxon.educationalContext.name === 'SECONDARYEDUCATION') {
                            checkTaxonLevelAndAssignValues('.Domain', $scope.taxon.subjects);
                        }

                        if ($scope.taxon.educationalContext.name === 'VOCATIONALEDUCATION') {
                            checkTaxonLevelAndAssignValues('.Domain', $scope.taxon.specializations);
                        }

                    } else if (($scope.taxon.domain !== undefined && $scope.taxon.domain !== null) && ($scope.taxon.domain.educationalContext !== undefined && $scope.taxon.domain.educationalContext !== null)) {
                        if ($scope.taxon.domain.educationalContext.name === 'BASICEDUCATION' || $scope.taxon.domain.educationalContext.name === 'SECONDARYEDUCATION') {
                            checkTaxonLevelAndAssignValues('.Subject', $scope.taxon.topics);
                        }

                        if ($scope.taxon.domain.educationalContext.name === 'VOCATIONALEDUCATION') {
                            checkTaxonLevelAndAssignValues('.Specialization', $scope.taxon.modules);
                        }
                    }

                    // used under PRESCHOOLEDUCATION, BASICEDUCATION and SECONDARYEDUCATION
                    checkTaxonLevelAndAssignValues('.Topic', $scope.taxon.subtopics);
                    // only used under VOCATIONALEDUCATION
                    checkTaxonLevelAndAssignValues('.Module', $scope.taxon.topics);
                }

                function checkTaxonLevelAndAssignValues (level, children) {
                    if ($scope.taxon.level === level) {
                        if (children.length > 0) {
                            $scope.taxonChildren = children;
                            $scope.childrenCount = children.length;
                            $scope.hasChildren = true;
                        }
                    }
                }

                $scope.toggleChildren = function(id) {
                    if ($scope.opened == null) {
                        $location.url('search/result?q=&taxon=' + id);
                        $scope.opened = true;
                    } else if ($scope.opened == true) {
                        $scope.opened = false;
                    } else if ($scope.opened == false) {
                        $location.url('search/result?q=&taxon=' + id);
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

                $scope.$watch(function () {
                    return $location.url()
                }, function () {
                    $scope.isActive = ($location.url() === '/search/result?q=&taxon=' + $scope.taxon.id);
                });
            }
        }
    }]);
});
