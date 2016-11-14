define([
    'angularAMD',
    'services/serverCallService',
    'services/recursionHelper',
], function (angularAMD) {
    angularAMD.directive('dopSidebarTaxon', ['RecursionHelper', '$location', 'serverCallService', function (RecursionHelper, $location, serverCallService) {
        return {
            scope: {
                taxon: '=',
                icon: '='
            },
            templateUrl: 'directives/sidebarTaxon/sidebarTaxon.html',
            compile: function (element) {
                return RecursionHelper.compile(element);
            },
            controller: function ($rootScope, $scope, $location, serverCallService, $timeout) {
                if ($scope.taxon) {
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


                    $scope.materialCount = localStorage.getItem($scope.taxon.name.toUpperCase() + "_COUNT");
                    if (!$scope.materialCount) {
                        getTaxonMaterialsCount($scope.taxon);
                    }

                    if ($scope.taxon.children.length > 0) {

                        if (!localStorage.getItem($scope.taxon.children[0].name.toUpperCase() + "_COUNT")) {
                            refreshMaterialsCounts();
                        }

                        //Refresh the counts asynchronously after init
                        $timeout(function () {
                                refreshMaterialsCounts()
                            }, 3000
                        );
                    }
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

                $scope.$watch(function () {
                    return localStorage.getItem($scope.taxon.name.toUpperCase() + "_COUNT");
                }, function (newCount, oldCount) {
                    if (newCount && newCount !== oldCount) {
                        $scope.materialCount = localStorage.getItem($scope.taxon.name.toUpperCase() + "_COUNT");
                    }
                });

                $scope.toggleChildren = function (id) {
                    if ($scope.opened == null) {
                        $location.url('search/result?q=&taxon=' + id);
                        $scope.opened = true;
                    } else if ($scope.opened == true) {
                        $scope.opened = false;
                    } else if ($scope.opened == false) {
                        $location.url('search/result?q=&taxon=' + id);
                        $scope.opened = true;
                    }
                };

                $scope.getTaxonTranslation = function (data) {
                    if (data.level !== '.EducationalContext') {
                        return data.level.toUpperCase().substr(1) + "_" + data.name.toUpperCase();
                    } else {
                        return data.name.toUpperCase();
                    }

                };

                $scope.$watch(function () {
                    return $location.url()
                }, function () {
                    $scope.isActive = ($location.url() === '/search/result?q=&taxon=' + $scope.taxon.id);
                });

                function refreshMaterialsCounts() {
                    $scope.taxon.children.forEach(function (child) {
                        if (child) getTaxonMaterialsCount(child);
                    })
                }

                function getTaxonMaterialsCount(child) {
                    serverCallService.makeGet('rest/search?q=&start=0&limit=0&taxon=' + child.id, {}, function (data) {
                        localStorage.setItem(child.name.toUpperCase() + "_COUNT", data.totalResults);
                    }, function () {
                        console.log("Failed to get " + child.name + " count");
                    })
                }
            }
        }
    }]);
});
