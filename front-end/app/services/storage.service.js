'use strict';

angular.module('koolikottApp')
    .factory('storageService',
        function () {
            var portfolio = null;
            var emptyPortfolio = null;
            var material = null;
            var storageService = {};

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
