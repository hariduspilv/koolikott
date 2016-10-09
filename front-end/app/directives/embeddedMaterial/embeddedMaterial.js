define([
    'app',
    'services/translationService',
    'services/iconService',
    'services/embedService'
], function (app) {
    app.directive('dopEmbeddedMaterial', ['translationService', 'iconService', 'embedService', function (translationService, iconService, embedService) {
        return {
            scope: {
                material: '=',
                chapter: '='
            },
            templateUrl: 'directives/embeddedMaterial/embeddedMaterial.html',
            controller: function ($scope) {
                init();

                function init() {
                    if ($scope.material) {
                        embedService.getEmbed(getSource($scope.material), embedCallback);
                        $scope.materialType = getType();
                    }
                }

                $scope.removeMaterial = function ($event, material) {
                    $event.preventDefault();
                    $event.stopPropagation();

                    var index = $scope.chapter.materials.indexOf(material);
                    $scope.chapter.materials.splice(index, 1);
                };

                $scope.getCorrectLanguageTitle = function (material) {
                    if (material) {
                        return getCorrectLanguageString(material.titles, material.language);
                    }
                };

                function getType() {
                    if ($scope.material === undefined || $scope.material === null) return '';

                    return iconService.getMaterialIcon($scope.material.resourceTypes);
                }

                function getCorrectLanguageString(languageStringList, materialLanguage) {
                    if (languageStringList) {
                        return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), materialLanguage);
                    }
                }

                function embedCallback(res) {
                    if (res && res.data.html) {
                        $scope.embeddedDataIframe = null;
                        $scope.embeddedData = null;

                        if (res.data.html.contains("<iframe")) {
                            $scope.embeddedDataIframe = res.data.html.replace("http:", "");
                        } else {
                            $scope.embeddedData = res.data.html.replace("http:", "");
                        }
                    }
                }

            }
        };
    }]);
});
