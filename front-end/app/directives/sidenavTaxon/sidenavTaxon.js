define([
    'angularAMD',
    'services/recursionHelper',
], function (angularAMD) {
    angularAMD.directive('dopSidenavTaxon', ['RecursionHelper', 'searchService', function (RecursionHelper, searchService) {
        return {
            scope: {
                taxon: '=',
                icon: '='
            },
            templateUrl: 'directives/sidenavTaxon/sidenavTaxon.html',
            compile: function (element) {
                return RecursionHelper.compile(element);
            },
            controller: function ($rootScope, $scope, $location, serverCallService, $timeout) {
                $scope.opened = null;
                $scope.hasChildren = false;

                if ($scope.taxon) {
                    $scope.taxonName = getTaxonTranslation($scope.taxon);

                    if ($scope.taxon.domains && $scope.taxon.domains.length > 0) {
                        $scope.taxonChildren = $scope.taxon.domains;
                        $scope.childrenCount = $scope.taxon.domains.length;
                        $scope.hasChildren = true;
                    }

                    if ($scope.taxon.educationalContext) {
                        if ($scope.taxon.educationalContext.name === 'PRESCHOOLEDUCATION') {
                            checkTaxonLevelAndAssignValues('.Domain', $scope.taxon.topics);
                        }

                        if ($scope.taxon.educationalContext.name === 'BASICEDUCATION' || $scope.taxon.educationalContext.name === 'SECONDARYEDUCATION') {
                            checkTaxonLevelAndAssignValues('.Domain', $scope.taxon.subjects);
                        }

                        if ($scope.taxon.educationalContext.name === 'VOCATIONALEDUCATION') {
                            checkTaxonLevelAndAssignValues('.Domain', $scope.taxon.specializations);
                        }

                    } else if ($scope.taxon.domain && $scope.taxon.domain.educationalContext) {
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

                    refreshThisTaxonCount();
                }

                function checkTaxonLevelAndAssignValues(level, children) {
                    if ($scope.taxon.level === level) {
                        if (children.length > 0) {
                            $scope.taxonChildren = children;
                            $scope.childrenCount = children.length;
                            $scope.hasChildren = true;
                        }
                    }
                }

                $scope.$watch(function () {
                    return localStorage.getItem(getTaxonCountKey($scope.taxon));
                }, function (newCount, oldCount) {
                    if (newCount && newCount !== oldCount) {
                        $scope.materialCount = localStorage.getItem(getTaxonCountKey($scope.taxon));
                    }
                });

                $scope.toggleChildren = function (id) {
                    if ($scope.materialCount == 0) {
                        return;
                    } else if ($scope.opened == null) {
                        searchService.setTaxon([id]);
                        $location.url(searchService.getURL());
                        $scope.opened = true;
                    } else if ($scope.opened == true) {
                        $scope.opened = false;
                    } else if ($scope.opened == false) {
                        searchService.setTaxon([id]);
                        $location.url(searchService.getURL());
                        $scope.opened = true;
                    }
                };

                function getTaxonTranslation() {
                    if ($scope.taxon.level !== '.EducationalContext') {
                        return $scope.taxon.level.toUpperCase().substr(1) + "_" + $scope.taxon.name.toUpperCase();
                    } else {
                        return $scope.taxon.name.toUpperCase();
                    }

                }

                $scope.$watch(function () {
                    return $location.url()
                }, function () {
                    $scope.isActive = ($location.url() === '/search/result?q=&taxon=' + $scope.taxon.id);
                });

                function refreshThisTaxonCount() {
                    $scope.materialCount = localStorage.getItem(getTaxonCountKey($scope.taxon));
                    $timeout(function () {
                        getTaxonMaterialsCount($scope.taxon)
                    }, 100);
                }

                function getTaxonMaterialsCount(taxon) {
                    serverCallService.makeGet('rest/search?q=&start=0&limit=0&taxon=' + taxon.id, {}, function (data) {
                        localStorage.setItem(getTaxonCountKey(taxon), data.totalResults);
                    }, function () {
                        console.log("Failed to get " + taxon.name + " count");
                    })
                }

                function getTaxonCountKey(taxon) {
                    return taxon.level.toUpperCase() + "_" + taxon.name.toUpperCase() + "_COUNT"

                }
            }
        }
    }]);
});
