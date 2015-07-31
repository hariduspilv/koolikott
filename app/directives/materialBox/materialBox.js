define(['app'], function(app)
{

	app.directive('dopMaterialBox', ['translationService', 'serverCallService', function(translationService, serverCallService) {
		return {
			scope: {
				material: '='
			},
			templateUrl: 'app/directives/materialBox/materialBox.html',
			controller: function ($scope, $location, $rootScope) {

				$scope.saveMaterial = function(material) {
					$rootScope.savedMaterial = material;
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
					return arrayToInitials(name.split(" "));
				}

				function arrayToInitials(array) {
					var res = "";
					for(var i = 0; i < array.length; i++) {
						res += wordToInitial(array[i]) + " ";
					}

					return res.trim();
				}

				function wordToInitial(name){
					return name.charAt(0).toUpperCase() + ".";
				}

				$scope.formatSurname = function(surname){
					var array = surname.split(" ");
					var last = array.length - 1;
					var res = "";

					if (last > 0) {
						res = arrayToInitials(array.slice(0, last)) + " ";
					}

					res += array[last];
					return res;
				}
			}
		};
	}]);

app.directive('fallbackSrc', function () {
	var fallbackSrc = {
		link: function postLink(scope, iElement, iAttrs) {
			iElement.bind('error', function() {
				angular.element(this).attr("src", iAttrs.fallbackSrc);
			});
		}
	}
	return fallbackSrc;
});

return app;
});