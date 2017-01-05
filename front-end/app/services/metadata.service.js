'use strict';

angular.module('koolikottApp').factory('metadataService', [
    'serverCallService', '$filter', '$timeout', 'taxonService', '$rootScope',
    function (serverCallService, $filter, $timeout, taxonService, $rootScope) {
        var instance;

        var CROSS_CURRICULAR_THEMES;
        var KEY_COMPETENCES;
        var LANGUAGES;
        var LICENSE_TYPES;
        var RESOURCE_TYPES;
        var USED_RESOURCE_TYPES;
        var USED_LANGUAGES;
        var EDUCATIONAL_CONTEXT;

        var crossCurricularThemesCallbacks = [];
        var keyCompetencesCallbacks = [];
        var languagesCallbacks = [];
        var licenseTypesCallbacks = [];
        var resourceTypesCallbacks = [];
        var usedResourceTypesCallbacks = [];
        var educationalContextsCallbacks = [];
        var usedLanguagesContextsCallbacks = [];

        $timeout(function () {
            init()
        });

        function init() {
            serverCallService.makeGet("rest/learningMaterialMetadata/educationalContext", {}, getEducationalContextSuccess, getEducationalContextFail);
            serverCallService.makeGet("rest/learningMaterialMetadata/crossCurricularTheme", {}, getCrossCurricularThemesSuccess, getCrossCurricularThemesFail);
            serverCallService.makeGet("rest/learningMaterialMetadata/keyCompetence", {}, getKeyCompetencesSuccess, getKeyCompetencesFail);
            serverCallService.makeGet("rest/learningMaterialMetadata/licenseType", {}, getLicenseTypeSuccess, getLicenseTypeFail);
            serverCallService.makeGet("rest/learningMaterialMetadata/resourceType/used", {}, getUsedResourceTypeSuccess, getResourceTypeFail);
            serverCallService.makeGet("rest/learningMaterialMetadata/usedLanguages", {}, getUsedLanguagesSuccess, getUsedLanguagesFail);
        }

        function getUsedLanguagesSuccess(data) {
            if (isEmpty(data)) {
                getUsedLanguagesFail();
            } else {
                data = data.sort(function (a, b) {
                    if ($filter('translate')(getLanguage(a)) < $filter('translate')(getLanguage(b))) return -1;
                    if ($filter('translate')(getLanguage(a)) > $filter('translate')(getLanguage(b))) return 1;
                    return 0;
                });
                USED_LANGUAGES = data;
                usedLanguagesContextsCallbacks.forEach(function (callback) {
                    callback(data);
                });
            }
        }

        function getLanguage(languageCode) {
            return 'LANGUAGE_' + languageCode.toUpperCase();
        }

        function getUsedLanguagesFail() {
            console.log("failed to get used languages.")
        }

        function getCrossCurricularThemesSuccess(data) {
            if (isEmpty(data)) {
                getCrossCurricularThemesFail();
            } else {
                CROSS_CURRICULAR_THEMES = data;
                crossCurricularThemesCallbacks.forEach(function (callback) {
                    callback(data);
                });
            }
        }

        function getCrossCurricularThemesFail() {
            console.log('Failed to get cross curricular themes.')
        }

        function getKeyCompetencesSuccess(data) {
            if (isEmpty(data)) {
                getKeyCompetencesFail();
            } else {
                KEY_COMPETENCES = data;
                keyCompetencesCallbacks.forEach(function (callback) {
                    callback(data);
                });
            }
        }

        function getKeyCompetencesFail() {
            console.log('Failed to get key comptences.')
        }

        function getLanguagesSuccess(data) {
            if (isEmpty(data)) {
                getLanguagesFail();
            } else {
                LANGUAGES = data;
                languagesCallbacks.forEach(function (callback) {
                    callback(data);
                });
            }
        }

        function getLanguagesFail() {
            console.log('Failed to get languages.')
        }

        function getLicenseTypeSuccess(data) {
            if (!isEmpty(data)) {
                LICENSE_TYPES = data;
                licenseTypesCallbacks.forEach(function (callback) {
                    callback(data);
                });
            }
        }

        function getLicenseTypeFail() {
            console.log('Failed to get license types.');
        }

        function getResourceTypeSuccess(data) {
            if (!isEmpty(data)) {
                RESOURCE_TYPES = data;
                resourceTypesCallbacks.forEach(function (callback) {
                    callback(data);
                });
            }
        }

        function getUsedResourceTypeSuccess(data) {
            if (!isEmpty(data)) {
                USED_RESOURCE_TYPES = data;
                usedResourceTypesCallbacks.forEach(function (callback) {
                    callback(data);
                });
            }
        }

        function getResourceTypeFail() {
            console.log('Failed to get resource types.');
        }

        function getEducationalContextSuccess(data) {
            if (!isEmpty(data)) {
                taxonService.setTaxons(data);
                taxonService.setSidenavTaxons(data);

                EDUCATIONAL_CONTEXT = data;
                educationalContextsCallbacks.forEach(function (callback) {
                    callback(data);
                });
            }
        }

        function getEducationalContextFail() {
            console.log('Failed to get educational contexts.');
        }

        instance = {

            loadCrossCurricularThemes: function (callback) {
                if (CROSS_CURRICULAR_THEMES) {
                    callback(CROSS_CURRICULAR_THEMES);
                } else {
                    // Save callback, call it when data arrives
                    crossCurricularThemesCallbacks.push(callback);
                }
            },

            loadKeyCompetences: function (callback) {
                if (KEY_COMPETENCES) {
                    callback(KEY_COMPETENCES);
                } else {
                    // Save callback, call it when data arrives
                    keyCompetencesCallbacks.push(callback);
                }
            },

            loadLanguages: function (callback) {
                if (LANGUAGES) {
                    callback(LANGUAGES);
                } else {
                    serverCallService.makeGet("rest/learningMaterialMetadata/language", {}, getLanguagesSuccess, getLanguagesFail);
                    // Save callback, call it when data arrives
                    languagesCallbacks.push(callback);
                }
            },

            loadLicenseTypes: function (callback) {
                if (LICENSE_TYPES) {
                    callback(LICENSE_TYPES);
                } else {
                    // Save callback, call it when data arrives
                    licenseTypesCallbacks.push(callback);
                }
            },

            loadResourceTypes: function (callback) {
                if (RESOURCE_TYPES) {
                    callback(RESOURCE_TYPES);
                } else {
                    serverCallService.makeGet("rest/learningMaterialMetadata/resourceType", {}, getResourceTypeSuccess, getResourceTypeFail);
                    // Save callback, call it when data arrives
                    resourceTypesCallbacks.push(callback);
                }
            },

            loadUsedResourceTypes: function (callback) {
                if (USED_RESOURCE_TYPES) {
                    callback(USED_RESOURCE_TYPES);
                } else {
                    usedResourceTypesCallbacks.push(callback);
                }
            },

            loadEducationalContexts: function (callback) {
                if (EDUCATIONAL_CONTEXT) {
                    callback(EDUCATIONAL_CONTEXT);
                } else {
                    // Save callback, call it when data arrives
                    if (callback) educationalContextsCallbacks.push(callback);
                }
            },

            loadUsedLanguages: function (callback) {
                if (USED_LANGUAGES) {
                    callback(USED_LANGUAGES);
                } else {
                    // Save callback, call it when data arrives
                    usedLanguagesContextsCallbacks.push(callback);
                }
            },

            updateUsedResourceTypes: function (callback) {
                serverCallService.makeGet("rest/learningMaterialMetadata/resourceType/used", {}, getUsedResourceTypeSuccess, getResourceTypeFail);
                USED_RESOURCE_TYPES = null;
                this.loadUsedResourceTypes(callback);
            }
        };

        return instance;
    }
]);
