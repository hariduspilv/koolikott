'use strict'

angular.module('koolikottApp')
    .component('dopPickMaterial', {
        bindings: {
            learningObject: '='
        },
        templateUrl: 'directives/pickMaterial/pickMaterial.html',
        controller: dopPickMaterialController
    });

dopPickMaterialController.$inject = ['$rootScope', 'authenticatedUserService'];

function dopPickMaterialController($rootScope, authenticatedUserService) {
    let vm = this;

    function isVisible() {
        return vm.learningObject && isMaterial(vm.learningObject.type) && authenticatedUserService.isAuthenticated();
    }

    function pickMaterial ($event) {
        $event.preventDefault();
        $event.stopPropagation();

        if ($rootScope.selectedMaterials) {
            let index = $rootScope.selectedMaterials.indexOf(vm.learningObject);
            if (index == -1) {
                $rootScope.selectedMaterials.push(vm.learningObject);
                vm.learningObject.selected = true;
            } else {
                $rootScope.selectedMaterials.splice(index, 1);
                vm.learningObject.selected = false;
            }
        } else {
            $rootScope.selectedMaterials = [];
            $rootScope.selectedMaterials.push(vm.learningObject);
            vm.learningObject.selected = true;
        }

        $rootScope.$broadcast("detailedSearch:close");
    }

    vm.isVisible = isVisible;
    vm.pickMaterial = pickMaterial;
}
