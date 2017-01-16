'use strict';

angular.module('koolikottApp')
.component('dopCardMedia', {
    bindings: {
        learningObject: '=',
        isAuthenticated: '<'
    },
    templateUrl: 'directives/card/cardMedia/cardMedia.html',
    controller: dopCardMediaController
});

dopCardMediaController.$inject = ['$rootScope', 'iconService'];

function dopCardMediaController ($rootScope, iconService) {
    let vm = this;

    vm.$onInit = () => {
        if (isMaterial(vm.learningObject.type)) {
            vm.learningObjectType = 'material';
            vm.materialType = iconService.getMaterialIcon(vm.learningObject.resourceTypes);
        } else if (isPortfolio(vm.learningObject.type)) {
            vm.learningObjectType = 'portfolio';
        }
    }

    vm.isMaterial = (type) => isMaterial(type);
    vm.isPortfolio = (type) => isPortfolio(type);

    vm.pickMaterial = ($event, material) => {
        $event.preventDefault();
        $event.stopPropagation();

        if ($rootScope.selectedMaterials) {
            let index = $rootScope.selectedMaterials.indexOf(material);
            if (index == -1) {
                $rootScope.selectedMaterials.push(material);
                material.selected = true;
            } else {
                $rootScope.selectedMaterials.splice(index, 1);
                material.selected = false;
            }
        } else {
            $rootScope.selectedMaterials = [];
            $rootScope.selectedMaterials.push(material);
            material.selected = true;
        }

        $rootScope.$broadcast("detailedSearch:close");
    };
}
