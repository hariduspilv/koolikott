'use strict'

angular.module('koolikottApp')
.directive('dopCardXs',
[
    'translationService', 'storageService',
    function(translationService, storageService) {
        return {
            scope: {
                learningObject: '='
            },
            templateUrl: 'directives/card/cardXS/cardXS.html',
            controller: function($scope, $location) {

                $scope.navigateTo = function (learningObject, $event) {
                    $event.preventDefault();

                    if (learningObject.type == '.Material') {
                        storageService.setMaterial(learningObject);

                        $location.path('/material').search({
                            id: learningObject.id
                        });
                    }

                    if (learningObject.type == '.Portfolio') {
                        storageService.setPortfolio(learningObject);

                        $location.path('/portfolio').search({
                            id: learningObject.id
                        });
                    }
                };

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
            }
        }
    }
]);
