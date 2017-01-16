'use strict';

angular.module('koolikottApp')
.component('dopCardXs', {
    bindings: {
        learningObject: '='
    },
    templateUrl: 'directives/card/cardXS/cardXS.html',
    controller: dopCardXsController
});

dopCardXsController.$inject = ['$location', 'translationService', 'storageService'];

function dopCardXsController ($location, translationService, storageService) {
    let vm = this;

    vm.navigateTo = function (learningObject, $event) {
        $event.preventDefault();

        if (vm.isMaterial(learningObject.type)) {
            storageService.setMaterial(learningObject);

            $location.path('/material').search({
                id: learningObject.id
            });
        }

        if (vm.isPortfolio(learningObject.type)) {
            storageService.setPortfolio(learningObject);

            $location.path('/portfolio').search({
                id: learningObject.id
            });
        }
    };

    vm.formatName = function(name) {
        return formatNameToInitials(name);
    };

    vm.formatSurname = function(surname) {
        return formatSurnameToInitialsButLast(surname);
    };

    vm.getCorrectLanguageTitle = function (material) {
        if (material) {
            return getCorrectLanguageString(material.titles, material.language);
        }
    };

    vm.isMaterial = function (type) {
        return isMaterial(type);
    };

    vm.isPortfolio = function (type) {
        return isPortfolio(type);
    };

    function getCorrectLanguageString(languageStringList, materialLanguage) {
        if (languageStringList) {
            return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), materialLanguage);
        }
    }
}
