define(['angularAMD'], function(angularAMD) {    
    angularAMD.factory('storageService', function() {
        var portfolio = null;
        var material = null;
        var storageService = {};
        
        storageService.setPortfolio = function(item) {
            portfolio = item;
        };
        
        storageService.getPortfolio = function() {
            return portfolio;
        };

        storageService.setMaterial = function(item) {
            material = item;
        };

        storageService.getMaterial = function() {
            return material;
        };
        
        return storageService;
    });
});
