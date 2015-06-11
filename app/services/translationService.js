define(['app'], function(app) {

	app.factory('translationService', ["$translate", function($translate) {
		return {
			setLanguage : function(language) {
				$translate.use(language);
			},
			
	        getLanguage : function() {
	        	return $translate.use() || $translate.preferredLanguage();
	        }
	    };
	}]);
});