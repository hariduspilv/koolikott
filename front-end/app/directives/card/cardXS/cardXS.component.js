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

    vm.navigateTo = (learningObject, $event) => {
        $event.preventDefault();

        if (isMaterial(learningObject.type)) {
            storageService.setMaterial(learningObject);

            $location.path('/material').search({
                id: learningObject.id
            });
        }

        if (isPortfolio(learningObject.type)) {
            storageService.setPortfolio(learningObject);

            $location.path('/portfolio').search({
                id: learningObject.id
            });
        }
    };

    vm.formatName = (name) => formatNameToInitials(name);
    vm.formatSurname = (surname) => formatSurnameToInitialsButLast(surname);

    vm.getCorrectLanguageTitle = (material) => {
        if (material) {
            return getCorrectLanguageString(material.titles, material.language);
        }
    };

    vm.isMaterial = (type) => isMaterial(type);
    vm.isPortfolio = (type) => isPortfolio(type);

    function getCorrectLanguageString(languageStringList, materialLanguage) {
        if (languageStringList) {
            return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), materialLanguage);
        }
    }
}
