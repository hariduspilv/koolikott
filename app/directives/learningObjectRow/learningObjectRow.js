define([
    'angularAMD',
    'services/translationService'
], function(angularAMD) {
    angularAMD.directive('dopLearningObjectRow', ['translationService', function(translationService) {
        return {
            scope: {
            	learningObjects: '='
            },
            templateUrl: 'directives/learningObjectRow/learningObjectRow.html',
            controller: function($scope) {
            	$scope.formatName = function(name) {
            		return formatNameToInitials(name);
            	}
            	
            	$scope.formatSurname = function(surname) {
            		return formatSurnameToInitialsButLast(surname);
            	}

                $scope.getCorrectLanguageString = function(languageStringList, language) {
                	if (languageStringList) {
                        return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), language);
                    }
                }

                $scope.getItemLink = function(item) {
                    if (item && item.type && item.id) {
                        if (item.type === '.Material') {
                            return "/material?materialId=" + item.id;
                        } else if (item.type === '.Portfolio') {
                            return "/portfolio?id=" + item.id;
                        }
                    }
                }
            }
        }
    }]);
});