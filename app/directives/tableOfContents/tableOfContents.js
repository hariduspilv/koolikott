define([
    'angularAMD',
    'angular-scroll',
    'services/translationService'
], function(angularAMD) {
    angularAMD.directive('dopTableOfContents', ['$filter', '$document', '$rootScope', 'translationService', '$mdToast', '$location', '$timeout', function($filter, $document, $rootScope, translationService, $mdToast, $location, $timeout) {
        return {
            scope: {
                portfolio: '=',
                readonly: '=readonly'
            },
            templateUrl: 'directives/tableOfContents/tableOfContents.html',
            controller: function($scope, $rootScope, $document) {
                $scope.isReadOnly = angular.isDefined($scope.isReadOnly) ? $scope.isReadOnly : false;

                $scope.gotoChapter = function(e, chapterId, subchapterId) {
                    e.preventDefault();

                    var combinedId = 'chapter-' + chapterId;
                    if (subchapterId != null) {
                        combinedId += '-' + subchapterId;
                    }

                    var $chapter = angular.element(document.getElementById(combinedId));
                    var $context = angular.element(document.getElementById('scrollable-content'));

                    if (!$rootScope.isViewPortforlioPage && !$rootScope.isEditPortfolioPage) {
                        $location.path('/portfolio/edit').search({
                            id: $scope.portfolio.id
                        });
                        var watchPage = $scope.$watch(function() {
                            return document.getElementById(combinedId)
                        }, function(newValue) {
                            if (newValue != null) {
                                $timeout(function() {
                                    $chapter = angular.element(document.getElementById(combinedId));
                                    $context = angular.element(document.getElementById('scrollable-content'));
                                    $context.scrollToElement($chapter, 30, 0);
                                    watchPage();
                                }, 0);
                            }
                        });
                    } else {
                        $context.scrollToElement($chapter, 30, 200);
                    }
                };

                $scope.addNewSubChapter = function(index) {
                    var subChapters = $scope.portfolio.chapters[index].subchapters;

                    subChapters.push({
                        title: $filter('translate')('PORTFOLIO_DEFAULT_NEW_SUBCHAPTER_TITLE'),
                        materials: []
                    });
                };

                $scope.addNewChapter = function(index) {
                    if (!$scope.portfolio.chapters) {
                        $scope.portfolio.chapters = [];
                    }

                    $scope.portfolio.chapters.push({
                        title: $filter('translate')('PORTFOLIO_DEFAULT_NEW_CHAPTER_TITLE'),
                        subchapters: [],
                        materials: []
                    });
                };

                $scope.addMaterialsToChapter = function($event, chapter) {
                    $event.preventDefault();
                    $event.stopPropagation();

                    if (chapter && chapter.materials) {
                        if ($rootScope.selectedSingleMaterial) {
                            if (!containsMaterial(chapter.materials, $rootScope.selectedSingleMaterial)) {
                                chapter.materials.push($rootScope.selectedSingleMaterial);
                                showToast($filter('translate')('PORTFOLIO_ADD_MATERIAL_SUCCESS'));
                            }
                        } else {
                            var pushed = false;
                            for (var i = 0; i < $rootScope.selectedMaterials.length; i++) {
                                var selectedMaterial = $rootScope.selectedMaterials[i];
                                if (!containsMaterial(chapter.materials, selectedMaterial)) {
                                    chapter.materials.push(selectedMaterial);
                                    pushed = true;
                                }
                            }
                            if (pushed) {
                                showToast($filter('translate')('PORTFOLIO_ADD_MATERIAL_SUCCESS'));
                            }

                        }
                    }
                    $rootScope.selectedMaterials = [];
                }

                function containsMaterial(materials, selectedMaterial) {
                    for (var i = 0; i < materials.length; i++) {
                        var material = materials[i];
                        if (material.id == selectedMaterial.id) {
                            return true;
                        }
                    }
                    return false;
                }

                function showToast(message) {
                    $mdToast.show($mdToast.simple().position('right top').content(message));
                }

                $rootScope.$watchCollection('selectedMaterials', function(newCollection) {
                    handleAddMaterialButton();
                });

                $rootScope.$watch('selectedSingleMaterial.id', function(newValue, oldValue) {
                    handleAddMaterialButton();
                }, true);

                $scope.navigateTo = function(e, portfolio) {
                    e.preventDefault();
                    if ($location.path() == '/portfolio/edit' || $location.path() == '/portfolio') {
                        var $element = angular.element(document.getElementById('portfolio-card'));
                        var $context = angular.element(document.getElementById('scrollable-content'));
                        $context.scrollToElement($element, 30, 200);
                    } else {
                        $location.path('/portfolio/edit').search({
                            id: portfolio.id
                        });
                    }
                };

                function handleAddMaterialButton() {
                    if ($rootScope.selectedMaterials && $rootScope.selectedMaterials.length > 0 || $rootScope.selectedSingleMaterial) {
                        $scope.showAddMaterialButton = true;
                        return;
                    }
                    $scope.showAddMaterialButton = false;
                }

            }
        };
    }]);
});