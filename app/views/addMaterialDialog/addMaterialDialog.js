define(['app'], function(app)
{
    app.controller('addMaterialDialog', ['$scope', '$mdDialog', 'serverCallService', 'translationService', 'Upload',
        function($scope, $mdDialog, serverCallService, translationService, Upload) {
            var preferredLanguage;

            var TABS_COUNT = 5;
            if($scope.material === undefined) {
                $scope.material = {};
            }

            $scope.material.metadata = [];
            $scope.material.tags = [];
            $scope.material.taxons = [{}];

            $scope.step = {};
            $scope.step.currentStep = 0;
            $scope.step.canProceed = false;
            $scope.step.isMaterialUrlStepValid = false;
            $scope.step.isMetadataStepValid = false;

            init();

            $scope.step.nextStep = function() {
                $scope.step.currentStep += 1;
            };

            $scope.step.previousStep = function() {
                $scope.step.currentStep -= 1;
            }

            $scope.step.isTabDisabled = function(index) {
                if (index == 0)
                    return false;

                return !isStepValid(index - 1);
            }

            $scope.step.canProceed = function() {
                return isStepValid($scope.step.currentStep);
            }

            $scope.step.canCreateMaterial = function() {
                return isStepValid(2);
            }

            $scope.step.isLastStep = function() {
                return $scope.step.currentStep === TABS_COUNT;
            }

            $scope.$watch('materialUrlForm.$valid', function(isValid) {
                $scope.step.isMaterialUrlStepValid = isValid;
            });

            $scope.addNewMetadata = function() {
                $scope.material.metadata.forEach(function(item) {
                    item.expanded = false
                });

                addNewMetadata();
            };

            $scope.deleteMetadata = function(index) {
                $scope.material.metadata.splice(index, 1);
            }

            $scope.addNewTaxon = function() {
                $scope.material.taxons.push({});
            }

            $scope.deleteTaxon = function(index) {
                $scope.material.taxons.splice(index, 1);
            }

            $scope.getLanguageById = function(id) {
                return $scope.material.languages.filter(function(language) {
                    return language.id == id;
                })[0].name;
            }

            $scope.cancel = function() {
                $mdDialog.hide();
            }

            $scope.createMaterial = function() {
                // TODO: Create material.
                $mdDialog.hide();
            }

            function isStepValid(index) {
                switch (index) {
                    case 0:
                        return $scope.step.isMaterialUrlStepValid;
                    case 1:
                        return isStepValid(0) && isMetadataStepValid();
                    default:
                        return isStepValid(index - 1);
                }
            }

            function init() {
                serverCallService.makeGet("rest/learningMaterialMetadata/language", {}, getLanguagesSuccess, getLanguagesFail, getLanguageFinally);
                serverCallService.makeGet("rest/learningMaterialMetadata/licenseType", {}, getLicenseTypeSuccess, getLicenseTypeFail);
                serverCallService.makeGet("rest/learningMaterialMetadata/resourceType", {}, getResourceTypeSuccess, getResourceTypeFail);
            }

            function getLanguagesSuccess(data) {
                if (!isEmpty(data)) {
                    $scope.material.languages = data;

                    setDefaultMaterialMetadataLanguage();
                }
            }

            function getLanguagesFail() {
                console.log('Failed to get languages.')
            }

            function getLanguageFinally() {
                addNewMetadata();
            }

            function getLicenseTypeSuccess(data) {
                if (!isEmpty(data)) {
                    $scope.material.licenceTypes = data;
                }
            }

            function getLicenseTypeFail() {
                console.log('Failed to get license types.');
            }

            function getResourceTypeSuccess(data) {
                if (!isEmpty(data)) {
                    $scope.material.resourceTypes = data;
                }
            }

            function getResourceTypeFail() {
                console.log('Failed to get resource types.');
            }

            function setDefaultMaterialMetadataLanguage() {
                var userLanguage = translationService.getLanguage();

                preferredLanguage = $scope.material.languages.filter(function(language) {
                    return language.code == userLanguage;
                });
            }

            function addNewMetadata() {
                var metadata = {
                    expanded: true,
                    title: ''
                };

                if (preferredLanguage !== null && preferredLanguage !== undefined)
                    metadata.language = preferredLanguage[0].id;

                $scope.material.metadata.push(metadata);
            }

            function isMetadataStepValid() {
                return $scope.material.metadata.filter(function(metadata) {
                           return metadata.title.length !== 0;
                       }).length !== 0;
            }
        }
    ]);
});