'use strict';

angular.module('koolikottApp')
.component('dopCardSm', {
    bindings: {
        learningObject: '=',
        chapter: '=?'
    },
    templateUrl: 'directives/card/cardSM/cardSM.html',
    controller: dopCardSmController
});

dopCardSmController.$inject = ['$location', '$rootScope', 'translationService', 'authenticatedUserService', 'targetGroupService', 'storageService', 'taxonService', 'taxonGroupingService'];

function dopCardSmController ($location, $rootScope, translationService, authenticatedUserService, targetGroupService, storageService, taxonService, taxonGroupingService) {
    let vm = this;

    vm.$onInit = () => {
        vm.selected = false;
        vm.isEditPortfolioPage = $rootScope.isEditPortfolioPage;
        vm.isEditPortfolioMode = $rootScope.isEditPortfolioMode;
        vm.domains = [];
        vm.subjects = [];

        vm.domainSubjectList = taxonGroupingService.getDomainSubjectList(vm.learningObject.taxons);
        vm.targetGroups = targetGroupService.getConcentratedLabelByTargetGroups(vm.learningObject.targetGroups);
    };

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

    vm.getCorrectLanguageTitle = (material) => {
        if (material) {
            return getCorrectLanguageString(material.titles, material.language);
        }
    };

    function getCorrectLanguageString(languageStringList, materialLanguage) {
        if (languageStringList) {
            return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), materialLanguage);
        }
    }

    vm.formatMaterialIssueDate = (issueDate) => formatIssueDate(issueDate);

    vm.formatName = (name) => {
        if (name) {
            return formatNameToInitials(name.trim());
        }
    };

    vm.formatSurname = (surname) => {
        if (surname) {
            return formatSurnameToInitialsButLast(surname.trim());
        }
    };

    vm.formatDate = (date) => formatDateToDayMonthYear(date);

    vm.isAuthenticated = () => {
        let authenticated = authenticatedUserService.getUser() && !authenticatedUserService.isRestricted() && !$rootScope.isEditPortfolioPage;
        if (!authenticated && isMaterial(vm.learningObject.type)) {
            vm.learningObject.selected = false;
        }

        return authenticated;
    };

    vm.isMaterial = (type) => isMaterial(type);
    vm.isPortfolio = (type) => isPortfolio(type);

    vm.getTaxons = () => {
        return vm.domainSubjectList
    };

    vm.hoverEnter = () => {
        vm.cardHover = true;
    };

    vm.hoverLeave = () => {
        vm.cardHover = false;
    }
}
