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
                $scope.learningObjectType;

                function init () {
                    if ($scope.learningObject.type === '.Material') {
                        $scope.learningObjectType = 'material';
                    } else if ($scope.learningObject.type === '.Portfolio') {
                        $scope.learningObjectType = 'portfolio';
                    }
                }

                $scope.formatName = function(name) {
                    return formatNameToInitials(name);
                }

                $scope.formatSurname = function(surname) {
                    return formatSurnameToInitialsButLast(surname);
                }

                $scope.getCorrectLanguageTitle = function (material) {
                    if (material) {
                        return getCorrectLanguageString(material.titles, material.language);
                    }
                };

                function getCorrectLanguageString(languageStringList, materialLanguage) {
                    if (languageStringList) {
                        return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), materialLanguage);
                    }
                }

                init();
            }
        }
    }
]);
