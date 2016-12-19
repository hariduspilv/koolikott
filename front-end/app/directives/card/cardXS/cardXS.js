'use strict'

angular.module('koolikottApp')
.directive('dopCardXs',
[
    'translationService',
    function(translationService) {
        return {
            scope: {
                learningObject: '='
            },
            templateUrl: 'directives/card/cardXS/cardXS.html',
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
            }
        }
    }
]);
