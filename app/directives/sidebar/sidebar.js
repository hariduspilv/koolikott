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
                
                var SIDE_ITEMS_AMOUNT = 5;

                var params = {
                    q: 'recommended:true',
                    start: 0,
                    sort: 'recommendation_timestamp',
                    sortDirection: 'desc',
                    limit: SIDE_ITEMS_AMOUNT
                };

                serverCallService.makeGet("rest/search", params, getRecommendationsSuccess, getRecommendationsFail);
                
                var params = {
                    maxResults: SIDE_ITEMS_AMOUNT
                };

                serverCallService.makeGet("rest/search/mostLiked", params, getMostLikedSuccess, getMostLikedFail);

                function getRecommendationsSuccess(data) {
                    if (isEmpty(data)) {
                        log('No data returned by recommended item search.');
                    } else {
                        $scope.recommendations = data.items;
                    }
                }

                function getRecommendationsFail(data, status) {
                    console.log('Session search failed.')
                }
                
                function getMostLikedSuccess(data) {
                    if (isEmpty(data)) {
                    	getMostLikedFail();
                    } else {
                        $scope.mostLiked = data;
                    }
                }

                function getMostLikedFail() {
                    console.log('Most liked search failed.')
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
                    if (item && item.type && item.id) {
                        if (item.type === '.Material') {
                            return "#/material?materialId=" + item.id;
                        } else if (item.type === '.Portfolio') {
                            return "#/portfolio?id=" + item.id;
                        }
                    }
                };
            }
        }
    }]);
});