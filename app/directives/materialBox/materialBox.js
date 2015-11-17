define(['app'], function(app)
{

	app.directive('dopMaterialBox', ['translationService', 'serverCallService', function(translationService, serverCallService) {
		return {
			scope: {
				material: '='
			},
			templateUrl: 'directives/materialBox/materialBox.html',
			controller: function ($scope, $location, $rootScope) {

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
					return formatNameToInitials(name);
				}

				$scope.formatSurname = function(surname){
					return formatSurnameToInitialsButLast(surname);
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
						return 'book';
					}

                    for (var i = 0; i < types.length; i++) {
						if (types[i].name.toLocaleLowerCase().trim() === 'audio')
							return 'audiotrack';
                        if (types[i].name.toLocaleLowerCase().trim() === 'video')
							return 'videocam';
					}

                    return 'book';
                }
			}
		};
	}]);

return app;
});
