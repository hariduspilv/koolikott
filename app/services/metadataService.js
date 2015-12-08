define(['app'], function(app) {
	var instance;

	var CROSS_CURRICULAR_THEMES;

	var crossCurricularThemesCallbacks = [];

	app.factory('metadataService', ["serverCallService",
		function(serverCallService) {

			init();

			function init() {
				serverCallService.makeGet("rest/learningMaterialMetadata/crossCurricularTheme", {}, getCrossCurricularThemesSuccess, getCrossCurricularThemesFail);
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

			instance = {

				loadCrossCurricularThemes : function(callback) {
					if (CROSS_CURRICULAR_THEMES) {
						callback(CROSS_CURRICULAR_THEMES)
					} else {
						// Save callback, call it when data arrives
						crossCurricularThemesCallbacks.push(callback);
					}
				}
		        
		    };

		    return instance;
		}]);
});