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
                
                var RECOMMENDED_ITEMS = 5;

                var params = {
                    q: 'recommended:true',
                    start: 0,
                    sort: 'recommendation_timestamp',
                    sortDirection: 'desc'
                };

                serverCallService.makeGet("rest/search", params, getRecommendationsSuccess, getRecommendationsFail);

                function getRecommendationsSuccess(data) {
                    if (isEmpty(data)) {
                        log('No data returned by session search.');
                    } else {
                        if (data.items.length > RECOMMENDED_ITEMS) {
                            $scope.recommendations = data.items.slice(0, RECOMMENDED_ITEMS);
                        } else {
                            $scope.recommendations = data.items;
                        }
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

                $scope.getItemLink = function(item) {
                    if (item.type === '.Material') {
                        return "#/material?materialId={{item.id}}";
                    } else if (item.type === '.Portfolio') {
                        return "#/portfolio?id={{item.id}}";
                    }
                };
            }
        }
    }]);
});