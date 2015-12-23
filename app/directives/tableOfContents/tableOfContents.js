define(['app'], function(app)
{
    app.directive('dopTableOfContents', ['$filter', '$document', '$rootScope', 'translationService', '$mdToast', '$translate',
     function($filter, $document, $rootScope, translationService, $mdToast, $translate) {
        return {
            scope: {
                portfolio: '=',
                readonly: '=readonly'
            },
            templateUrl: 'directives/tableOfContents/tableOfContents.html',
            controller: function ($scope, $rootScope, $document) {
                $scope.isReadOnly = angular.isDefined($scope.isReadOnly) ? $scope.isReadOnly : false;

                $scope.gotoChapter = function(e, chapterId, subchapterId) {
                    e.preventDefault();

                    if (!$rootScope.isViewPortforlioPage && !$rootScope.isEditPortfolioPage) return;

                    var combinedId = 'chapter-' + chapterId;
                    
                    if (subchapterId != null) {
                    	combinedId += '-' + subchapterId;
                    }
                    
                    var $chapter = angular.element(document.getElementById(combinedId));
                    var $context = angular.element(document.getElementById('scrollable-content'));

                    $context.scrollToElement($chapter, 30, 200);
                };

                $scope.addNewSubChapter = function(index) {
                    var subChapters = $scope.portfolio.chapters[index].subchapters;

                    subChapters.push({
                        title: $filter('translate')('PORTFOLIO_DEFAULT_NEW_SUBCHAPTER_TITLE'),
                        materials: []
                    });
                };

                $scope.addNewChapter = function(index) {
                    if(!$scope.portfolio.chapters) {
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
                  
                	if(chapter && chapter.materials) {              		
                		if($rootScope.selectedSingleMaterial) {
                			if(!containsMaterial(chapter.materials, $rootScope.selectedSingleMaterial)) {
	                			chapter.materials.push($rootScope.selectedSingleMaterial);
	                			showToast($filter('translate')('PORTFOLIO_ADD_MATERIAL_SUCCESS'));
	                		}
                		} else {
                			var pushed = false;
		                	for(var i=0; i<$rootScope.selectedMaterials.length; i++) {
		                		var selectedMaterial = $rootScope.selectedMaterials[i];
		                		if(!containsMaterial(chapter.materials, selectedMaterial)) {
		                			chapter.materials.push(selectedMaterial);
		                			pushed = true;
		                		}
		                	}
		                	if(pushed) {
		                		showToast($filter('translate')('PORTFOLIO_ADD_MATERIAL_SUCCESS'));
		                	}
		                	
                		}
                	}
                	$rootScope.selectedMaterials = [];
                }
                
                function containsMaterial(materials, selectedMaterial) {
                	for(var i=0; i<materials.length; i++) {
                		var material = materials[i];
                		if(material.id == selectedMaterial.id) {
                			return true;
                		}
                	}
                	return false;
                }
                
                function showToast(message) {
                    $mdToast.show($mdToast.simple().position('right top').content(message));
                }
                
                $scope.$watch(function() { return $rootScope.selectedMaterials }, function(newValue, oldValue) {
                	handleAddMaterialButton();
                }, true);
                
                $scope.$watch(function() { return $rootScope.selectedSingleMaterial }, function(newValue, oldValue) {
                	handleAddMaterialButton();
                }, true);
                
                function handleAddMaterialButton() {
                	if($rootScope.selectedMaterials && $rootScope.selectedMaterials.length > 0 || $rootScope.selectedSingleMaterial) {
                		$scope.showAddMaterialButton = true;
                		return;
                	}
                	$scope.showAddMaterialButton = false;
                }
                
            }
        };
    }]);

    return app;
});
