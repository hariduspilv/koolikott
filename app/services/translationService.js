define(['app'], function(app) {

	app.factory('translationService', ["$rootScope", "$translate", function($rootScope, $translate) {
		var instance;
		
   		$rootScope.$watch(function () {
                return localStorage.getItem("userPreferredLanguage");
            }, function(newLanguage, oldLanguage) {
            	if (newLanguage !== oldLanguage) {
            		instance.setLanguage(newLanguage);
            	}
        }, true);

		instance = {
			
			setLanguage : function(language) {
				$translate.use(language);
				localStorage.setItem("userPreferredLanguage", language);
			},
			
	        getLanguage : function() {
	        	var language = localStorage.getItem("userPreferredLanguage");
	        	return $translate.proposedLanguage() || $translate.use() || $translate.preferredLanguage();
	        }
	    };

	    return instance;
	}]);
});