define(['app'], function(app) {

	app.factory('translationService', ["$translate", function($translate) {
		var languageChangeCallback;
		
		return {
			
			setLanguage : function(language) {
				$translate.use(language);
				if (languageChangeCallback) {
					languageChangeCallback(language);
				}
			},
			
	        getLanguage : function() {
	        	return $translate.use() || $translate.proposedLanguage() || $translate.preferredLanguage();
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