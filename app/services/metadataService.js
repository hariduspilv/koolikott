define(['app'], function(app) {
	var instance;

	var CROSS_CURRICULAR_THEMES;
	var KEY_COMPETENCES;

	var crossCurricularThemesCallbacks = [];
	var keyCompetencesCallbacks = [];

	app.factory('metadataService', ["serverCallService",
		function(serverCallService) {

			init();

			function init() {
				serverCallService.makeGet("rest/learningMaterialMetadata/crossCurricularTheme", {}, getCrossCurricularThemesSuccess, getCrossCurricularThemesFail);
				serverCallService.makeGet("rest/learningMaterialMetadata/keyCompetence", {}, getKeyCompetencesSuccess, getKeyCompetencesFail);
			}

			function getCrossCurricularThemesSuccess(data) {
	            if (isEmpty(data)) {
	                getCrossCurricularThemesFail();
	            } else {
	                CROSS_CURRICULAR_THEMES = data;
					crossCurricularThemesCallbacks.forEach(function(callback) {
						callback(data);
					});
	            }
	        }

	        function getCrossCurricularThemesFail() {
	            console.log('Failed to get cross curricular themes.')
	        }

	        function getKeyCompetencesSuccess(data) {
	            if (isEmpty(data)) {
	                getKeyComptencesFail();
	            } else {
	                KEY_COMPETENCES = data;
					keyCompetencesCallbacks.forEach(function(callback) {
						callback(data);
					});
	            }
	        }

	        function getKeyCompetencesFail() {
	            console.log('Failed to get key comptences.')
	        }

			instance = {

				loadCrossCurricularThemes : function(callback) {
					if (CROSS_CURRICULAR_THEMES) {
						callback(CROSS_CURRICULAR_THEMES);
					} else {
						// Save callback, call it when data arrives
						crossCurricularThemesCallbacks.push(callback);
					}
				},

				loadKeyCompetences : function(callback) {
					if (KEY_COMPETENCES) {
						callback(KEY_COMPETENCES);
					} else {
						// Save callback, call it when data arrives
						keyCompetencesCallbacks.push(callback);
					}
				}
		        
		    };

		    return instance;
		}]);
});