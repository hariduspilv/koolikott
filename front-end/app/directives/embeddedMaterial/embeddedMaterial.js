define([
    'app',
    'angular-youtube-mb',
    'directives/slideshare/slideshare',
    'services/translationService',
    'services/iconService',
    'services/embedService'
], function (app) {
    app.directive('dopEmbeddedMaterial', ['translationService', 'iconService', 'embedService',
        function (translationService, iconService, embedService) {
            return {
                scope: {
                    material: '=',
                    chapter: '=',
                    index: '='
                },
                templateUrl: 'directives/embeddedMaterial/embeddedMaterial.html',
                controller: function ($scope, $rootScope, $location) {
                    init();

                    function init() {
                        $scope.isEditPortfolioPage = $rootScope.isEditPortfolioPage;
                        $scope.isEditPortfolioMode = $rootScope.isEditPortfolioMode;

                        if ($scope.material) {
                            $scope.materialType = getType();
                            getSourceType();
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

                    $scope.moveItem = function (origin, destination) {
                        var temp = $scope.chapter.materials[destination];
                        $scope.chapter.materials[destination] = $scope.chapter.materials[origin];
                        $scope.chapter.materials[origin] = temp;
                    };

                    $scope.listItemUp = function (itemIndex) {
                        $scope.moveItem(itemIndex, itemIndex - 1);
                    };

                    $scope.listItemDown = function (itemIndex) {
                        $scope.moveItem(itemIndex, itemIndex + 1);

                    };

                    $scope.navigateToMaterial = function (material, $event) {
                        $event.preventDefault();
                        $rootScope.savedMaterial = material;

                        $location.path('/material').search({
                            materialId: material.id
                        });
                    };

                    function getSourceType() {
                        if (isYoutubeVideo($scope.material.source)) {
                            $scope.sourceType = 'YOUTUBE';
                        } else if (isSlideshareLink($scope.material.source)) {
                            $scope.sourceType = 'SLIDESHARE';
                        } else {
                            $scope.sourceType = 'LINK';
                            embedService.getEmbed(getSource($scope.material), embedCallback);
                        }
                    }

                    function isYoutubeVideo(url) {
                        // regex taken from http://stackoverflow.com/questions/2964678/jquery-youtube-url-validation-with-regex #ULTIMATE YOUTUBE REGEX
                        var youtubeUrlRegex = /^(?:https?:\/\/)?(?:www\.)?(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=))((\w|-){11})(?:\S+)?$/;
                        return url && url.match(youtubeUrlRegex);
                    }

                    function isSlideshareLink(url) {
                        var slideshareUrlRegex = /^https?\:\/\/www\.slideshare\.net\/[a-zA-Z0-9\-]+\/[a-zA-Z0-9\-]+$/;
                        return url && url.match(slideshareUrlRegex);
                    }

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
