define(['app'], function (app) {
    app.controller('addMaterialDialog', ['$scope', '$mdDialog', 'serverCallService', 'translationService', 'metadataService', '$filter', '$location', '$rootScope', 'authenticatedUserService',
        function ($scope, $mdDialog, serverCallService, translationService, metadataService, $filter, $location, $rootScope, authenticatedUserService) {

            $scope.showHints = true;
            $scope.creatorIsPublisher = false;

            var preferredLanguage;

            var TABS_COUNT = 2;
            if ($scope.material === undefined) {
                $scope.material = {};
            }

            $scope.material.metadata = [];
            $scope.material.tags = [];
            $scope.material.taxons = [{}];
            $scope.material.authors = [{}];
            $scope.material.selectedKeyCompetences = [];
            $scope.material.selectedCrossCurricularThemes = [];

            $scope.step = {};
            $scope.step.currentStep = 0;
            $scope.step.canProceed = false;
            $scope.step.isMaterialUrlStepValid = false;
            $scope.step.isMetadataStepValid = false;

            $scope.isEducationalContextSelected = false;

            init();

            $scope.step.nextStep = function () {
                $scope.step.currentStep += 1;
            };

            $scope.step.previousStep = function () {
                $scope.step.currentStep -= 1;
            };

            $scope.step.isTabDisabled = function (index) {
                if (index == 0)
                    return false;

                return !isStepValid(index - 1);
            };

            $scope.step.canProceed = function () {
                return isStepValid($scope.step.currentStep);
            };

            $scope.step.canCreateMaterial = function () {
                return isStepValid(1);
            };

            $scope.step.isLastStep = function () {
                return $scope.step.currentStep === TABS_COUNT;
            };

            $scope.$watch('materialUrlForm.$valid', function (isValid) {
                $scope.step.isMaterialUrlStepValid = isValid;
            });

            $scope.addNewMetadata = function () {
                $scope.material.metadata.forEach(function (item) {
                    item.expanded = false
                });

                addNewMetadata();
            };

            $scope.addNewAuthor = function () {
                $scope.material.authors.push({});
            };

            $scope.deleteAuthor = function (index) {
                $scope.material.authors.splice(index, 1);
            };

            $scope.deleteMetadata = function (index) {
                $scope.material.metadata.splice(index, 1);
            };

            $scope.addNewTaxon = function () {
                var educationalContext = $rootScope.taxonUtils.getEducationalContext($scope.material.taxons[0]);

                $scope.material.taxons.push(educationalContext);
            };

            $scope.deleteTaxon = function (index) {
                $scope.material.taxons.splice(index, 1);
            };

            $scope.getLanguageById = function (id) {
                return $scope.material.languages.filter(function (language) {
                    return language.id == id;
                })[0].name;
            };

            $scope.$watch('material.taxons[0]', function(newValue, oldValue) {
                if (newValue.level === $rootScope.taxonUtils.constants.EDUCATIONAL_CONTEXT && newValue !== oldValue) {
                    $scope.isEducationalContextSelected = true;
                    $scope.educationalContextId = newValue.id;

                    $scope.material.taxons = $scope.material.taxons.slice(0, 1);
                }
            }, false);

            $scope.cancel = function () {
                $mdDialog.hide();
            };

            $scope.createMaterial = function () {
                var material = $scope.material;
                var metadata = getMetadata(material);
                var titles = metadata.titles;
                var descriptions = metadata.descriptions;
                var publisher = getPublisher(material);
                var authors = getAuthors(material);
                var licenseType = getLicenseType(material);
                var resourceTypes = getResourceTypes(material);
                var base64Picture = getPicture(material);
                var taxons = getTaxons(material);
                var keyCompetences = material.selectedKeyCompetences;
                var crossCurricularThemes = material.selectedCrossCurricularThemes;

                var newMaterial = {
                    type: '.Material',
                    source: material.url,
                    language: material.language,
                    titles: titles,
                    descriptions: descriptions,
                    tags: material.tags,
                    paid: material.paid,
                    publishers: [publisher],
                    authors: authors,
                    targetGroups: material.targetGroups,
                    licenseType: licenseType,
                    resourceTypes: resourceTypes,
                    picture: base64Picture,
                    taxons: taxons
                };

                if ($scope.educationalContextId === 2 || $scope.educationalContextId === 3) {
                    newMaterial.keyCompetences = keyCompetences;
                    newMaterial.crossCurricularThemes = crossCurricularThemes;
                }

                serverCallService.makePost("rest/material", newMaterial, postMaterialSuccess, postMaterialFail);
            };

            function getAuthors(material) {
                var authors = [];

                material.authors.forEach(function (author) {
                    if (author.name && author.surname) {
                        authors.push(author);
                    }
                });

                return authors;
            }

            function getTaxons(material) {
                if (material.taxons[0].id) {
                    var taxons = material.taxons
                }
                return taxons;
            }

            function getPicture(material) {
                if (material.picture) {
                    var base64Picture = material.picture.$ngfDataUrl;
                }
                return base64Picture;
            }

            function getPublisher(material) {
                if (material.publisher) {
                    var publisher = {
                        name: material.publisher
                    }
                }
                return publisher;
            }

            function getLicenseType(material) {
                if (material.licenseType) {
                    var licenseType = JSON.parse(material.licenseType)
                }
                return licenseType;
            }

            function getResourceTypes(material) {
                var resourceTypes = [];

                if (material.selectedResourceTypes) {
                    material.selectedResourceTypes.forEach(function(resourceType) {
                        resourceTypes.push(JSON.parse(resourceType));
                    });
                }

                return resourceTypes;
            }

            function getMetadata(material) {
                var titles = [];
                var descriptions = [];


                material.metadata.forEach(function (item) {
                    if (item.title) {
                        var title = {
                            language: item.language,
                            text: item.title
                        };

                        titles.push(title);
                    }

                    if (item.description) {
                        var description = {
                            language: item.language,
                            text: item.description
                        };

                        descriptions.push(description);
                    }
                });

                return {
                    titles: titles,
                    descriptions: descriptions
                };
            }

            function isStepValid(index) {
                switch (index) {
                    case 0:
                        return $scope.step.isMaterialUrlStepValid && isMetadataStepValid();
                    default:
                        return isStepValid(index - 1);
                }
            }

            $scope.translate = function (item, prefix) {
                return $filter("translate")(prefix + item.toUpperCase());
            };

            /**
             * Search for keyCompetences.
             */
            $scope.searchKeyCompetences = function (query) {
                return query ? $scope.material.keyCompetences
                    .filter(searchFilter(query, "KEY_COMPETENCE_")) : $scope.material.keyCompetences;
            };

            /**
             * Search for CrossCurricularThemes.
             */
            $scope.searchCrossCurricularThemes = function (query) {
                return query ? $scope.material.crossCurricularThemes
                    .filter(searchFilter(query, "CROSS_CURRICULAR_THEME_")) : $scope.material.crossCurricularThemes;
            };

            /**
             * Create filter function for a query string
             */
            function searchFilter(query, translationPrefix) {
                var lowercaseQuery = angular.lowercase(query);

                return function filterFn(filterSearchObject) {
                    var lowercaseItem = $scope.translate(filterSearchObject.name, translationPrefix);
                    lowercaseItem = angular.lowercase(lowercaseItem);

                    if (lowercaseItem.indexOf(lowercaseQuery) === 0) {
                        return filterSearchObject;
                    }
                };
            }


            function init() {
                if (authenticatedUserService.getUser() && authenticatedUserService.getUser().role === 'PUBLISHER') {
                    $scope.material.publisher = authenticatedUserService.getUser().username;
                    $scope.creatorIsPublisher = true;
                }

                serverCallService.makeGet("rest/learningMaterialMetadata/language", {}, getLanguagesSuccess, getLanguagesFail, getLanguageFinally);
                serverCallService.makeGet("rest/learningMaterialMetadata/licenseType", {}, getLicenseTypeSuccess, getLicenseTypeFail);
                serverCallService.makeGet("rest/learningMaterialMetadata/resourceType", {}, getResourceTypeSuccess, getResourceTypeFail);
                metadataService.loadKeyCompetences(setKeyCompetences);
                metadataService.loadCrossCurricularThemes(setCrossCurricularThemes);
            }

            function setCrossCurricularThemes(data) {
                if (!isEmpty(data)) {
                    $scope.material.crossCurricularThemes = data;
                }
            }

            function setKeyCompetences(data) {
                if (!isEmpty(data)) {
                    $scope.material.keyCompetences = data;
                }
            }

            function postMaterialSuccess(data) {
                if (!isEmpty(data)) {
                    $mdDialog.hide(data);
                    console.log("material added");
                    if(!$scope.isChapterMaterial) {
                        $location.url('/material?materialId=' + data.id);
                    }
                }
            }

            function postMaterialFail() {
                console.log('Failed to add material.')
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

                preferredLanguage = $scope.material.languages.filter(function (language) {
                    return language == userLanguage;
                });
            }

            function addNewMetadata() {
                var metadata = {
                    expanded: true,
                    title: ''
                };

                if (preferredLanguage !== null && preferredLanguage !== undefined)
                    metadata.language = preferredLanguage[0];

                $scope.material.metadata.push(metadata);
            }

            function isMetadataStepValid() {
                return $scope.material.metadata.filter(function (metadata) {
                        return metadata.title && metadata.title.length !== 0;
                    }).length !== 0;
            }

        }])
});
