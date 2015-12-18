define(['app'], function(app)
{

	app.directive('dopMaterialBox', ['translationService', 'serverCallService', '$rootScope', function(translationService, serverCallService, $rootScope) {
		return {
			scope: {
				material: '='
			},
			templateUrl: 'directives/materialBox/materialBox.html',
			controller: function ($scope, $location) {

				$scope.selected = false;
				
				$scope.isEditPortfolioMode = $rootScope.isEditPortfolioMode;
				
				$scope.navigateTo = function(material) {
					$rootScope.savedMaterial = material;

                    $location.path('/material').search({materialId:  material.id});
				}

				$scope.getCorrectLanguageTitle = function(material) {
					if (material) {
						return getCorrectLanguageString(material.titles, material.language);
					}
				}

				function getCorrectLanguageString(languageStringList, materialLanguage) {
					if (languageStringList) {
						return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), materialLanguage);
					}
				}

				$scope.formatMaterialIssueDate = function(issueDate) {
					return formatIssueDate(issueDate);

				}

				$scope.formatName = function(name) {
					if(name) {
						return formatNameToInitials(name);
					}
				}

				$scope.formatSurname = function(surname){
					if(surname) {
						return formatSurnameToInitialsButLast(surname);
					}
				}

				$scope.isOfType = function(type) {
					var types = $scope.material.resourceTypes;
					if(types.length == 0) {
						return false;
					}

					for (var i = 0; i < types.length; i++) {
						if (types[i].name === type) {
							return true;
						}
					}

					return false;
				}

                $scope.getType = function() {
                    var types = $scope.material.resourceTypes;
					if (types.length == 0) {
						return 'description';
					}

                    for (var i = 0; i < types.length; i++) {
						if (types[i].name.toLocaleLowerCase().trim() === 'audio')
							return 'audiotrack';
                        if (types[i].name.toLocaleLowerCase().trim() === 'video')
							return 'videocam';
					}

                    return 'description';
                }
                
                $scope.pickMaterial = function($event, material) {
                	$event.stopPropagation();
                	var index = $rootScope.selectedMaterials.indexOf(material);
                	if(index == -1) {
                		$rootScope.selectedMaterials.push(material); 
                		$scope.selected = true;
                	} else {
                		$rootScope.selectedMaterials.splice(index, 1);
                	}
                }
                
			}
		};
	}]);

return app;
});
