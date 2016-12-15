'use strict'

angular.module('koolikottApp')
.directive('dopTableOfContents',
[
    '$filter', '$document', '$rootScope', 'translationService', '$mdToast', '$location', '$timeout',
    function($filter, $document, $rootScope, translationService, $mdToast, $location, $timeout) {
        return {
            scope: {
                portfolio: '=',
                readonly: '=readonly'
            },
            templateUrl: 'directives/tableOfContents/tableOfContents.html',
            controller: function($scope, $rootScope, $mdSidenav) {

                function init() {
                    // Scroll to hash
                    if ($location.hash()) {
                        var listener = $scope.$watch(function() {
                            return document.getElementById($location.hash())
                        }, function(newValue) {
                            if (newValue != null) {
                                $timeout(function() {
                                    goToElement($location.hash());
                                    listener();
                                });
                            }
                        });
                    }
                }

                $scope.gotoChapter = function(e, chapterId, subchapterId) {
                    e.preventDefault();

                    var combinedId = 'chapter-' + chapterId;
                    if (subchapterId != null) {
                        combinedId += '-' + subchapterId;
                    }

                    goToElement(combinedId);
                };

                function goToElement(elementID) {
                    var $chapter = angular.element(document.getElementById(elementID));

                    if (!$rootScope.isViewPortforlioPage && !$rootScope.isEditPortfolioPage) {
                        $location.path('/portfolio/edit').search({
                            id: $scope.portfolio.id
                        });
                        var watchPage = $scope.$watch(function() {
                            return document.getElementById(elementID)
                        }, function(newValue) {
                            if (newValue != null) {
                                $timeout(function() {
                                    $chapter = angular.element(document.getElementById(elementID));
                                    $document.scrollToElement($chapter, 60, 0);
                                    watchPage();
                                }, 0);
                            }
                        });
                    } else {
                        $document.scrollToElement($chapter, 60, 200);
                    }
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

                $scope.closeSidenav = function(id) {
                    if(window.innerWidth < 1280) {
                        $mdSidenav(id)
                        .close();
                    }
                };

                init();

            }
        };
    }
]);
