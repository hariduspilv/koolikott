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
            controller: ['$scope', '$location', function($scope, $location) {

                $scope.navigateTo = function (learningObject, $event) {
                    $event.preventDefault();

                    if (isMaterial(learningObject.type)) {
                        storageService.setMaterial(learningObject);

                        $location.path('/material').search({
                            id: learningObject.id
                        });
                    }

                    if (isPortfolio(learningObject.type)) {
                        storageService.setPortfolio(learningObject);

                        $location.path('/portfolio').search({
                            id: learningObject.id
                        });
                    }
                };

                $scope.formatName = function(name) {
                    return formatNameToInitials(name);
                };

                $scope.formatSurname = function(surname) {
                    return formatSurnameToInitialsButLast(surname);
                };

                $scope.getCorrectLanguageTitle = function (material) {
                    if (material) {
                        return getCorrectLanguageString(material.titles, material.language);
                    }
                };

                $scope.isMaterial = function (type) {
                    return isMaterial(type);
                };

                $scope.isPortfolio = function (type) {
                    return isPortfolio(type);
                };

                function getCorrectLanguageString(languageStringList, materialLanguage) {
                    if (languageStringList) {
                        return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), materialLanguage);
                    }
                }
            }]
        }
    }
]);
