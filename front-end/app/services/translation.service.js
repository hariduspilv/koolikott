'use strict';

angular.module('koolikottApp')
.factory('translationService',
[
    "$rootScope", "$translate",
    function($rootScope, $translate) {
        var instance;

        $rootScope.$watch(function() {
            return localStorage.getItem("userPreferredLanguage");
        }, function(newLanguage, oldLanguage) {
            if (newLanguage !== oldLanguage) {
                instance.setLanguage(newLanguage);
            }
        }, true);

        instance = {
            setLanguage: function(language) {
                if(language) {
                    $translate.use(language);
                    localStorage.setItem("userPreferredLanguage", language);
                }
            },

            getLanguage: function() {
                return $translate.proposedLanguage() || $translate.use() || $translate.preferredLanguage();
            },

            instant : function(translationId, interpolateParams, interpolationId, forceLanguage) {
                return $translate.instant(translationId, interpolateParams, interpolationId, forceLanguage);
            }
        };

        return instance;
    }
]);
