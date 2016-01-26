define([
    'angularAMD',
    'services/translationService',
    'services/serverCallService'
], function(angularAMD) {
    angularAMD.directive('dopSidebar', ['translationService', '$location', 'serverCallService', function(translationService, $location) {
        return {
            scope: true,
            templateUrl: 'directives/sidebar/sidebar.html',
            controller: function($scope, serverCallService) {
                //TODO: replace with recommended materials call after service is finished
                serverCallService.makeGet("rest/material/getNewestMaterials?numberOfMaterials=5", {}, getRecommendationsSuccess, getRecommendationsFail);

                function getRecommendationsSuccess(data) {
                    if (isEmpty(data)) {
                        log('No data returned by session search.');
                    } else {
                        $scope.recommendations = data;
                    }
                }

                function getRecommendationsFail(data, status) {
                    console.log('Session search failed.')
                }

                $scope.getCorrectLanguageString = function(languageStringList, language) {
                    if (languageStringList) {
                        return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), language);
                    }
                }

                $scope.formatName = function(name) {
                    if (name) {
                        return formatNameToInitials(name);
                    }
                };

                $scope.formatSurname = function(surname) {
                    if (surname) {
                        return formatSurnameToInitialsButLast(surname);
                    }
                };
            }
        }
    }]);
});