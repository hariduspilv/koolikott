'use strict';

angular.module('koolikottApp')
    .factory('storageService',
        function () {
            let portfolio = null;
            let emptyPortfolio = null;
            let material = null;
            let storageService = {};

            storageService.setLearningObject = function (item) {
                if (item){
                    const {type} = item;
                    if (isMaterial(type)){
                        storageService.setMaterial(item)
                    } else if (isPortfolio(type)){
                        storageService.setPortfolio(item)
                    }
                }
            }

            storageService.setPortfolio = function (item) {
                portfolio = item;
            };

            storageService.getPortfolio = function () {
                return portfolio;
            };

            storageService.setEmptyPortfolio = function (item) {
                emptyPortfolio = item;
            };

            storageService.getEmptyPortfolio = function () {
                return emptyPortfolio;
            };

            storageService.setMaterial = function (item) {
                material = item;
            };

            storageService.getMaterial = function () {
                return material;
            };

            return storageService;
        });
