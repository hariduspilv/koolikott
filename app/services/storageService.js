define(['angularAMD'], function(angularAMD) {    
    angularAMD.factory('storageService', function() {
        var portfolio = null;
        var storageService = {};
        
        storageService.setPortfolio = function(item) {
            portfolio = item;
        };
        
        storageService.getPortfolio = function() {
            return portfolio;
        };
        
        //TODO: same logic for material too (hence the service name)
        
        return storageService;
    });
});
