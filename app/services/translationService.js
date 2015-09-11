define(['app'], function(app) {

	app.factory('translationService', ["$translate", function($translate) {
		var languageChangeCallback;
		
		return {
			
			setLanguage : function(language) {
				$translate.use(language);
				localStorage.setItem("userPreferredLanguage", language);
				if (languageChangeCallback) {
					languageChangeCallback(language);
				}
			},
			
	        getLanguage : function() {
	        	return $translate.proposedLanguage() || $translate.use() || $translate.preferredLanguage();
	        },
			
			setLanguageChangeListener : function(callback) {
				languageChangeCallback = callback;
			},
	        
	        removeLanguageChangeListener : function() {
	        	languageChangeCallback = null;
	        }
	    };
	}]);
});