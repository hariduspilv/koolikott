'use strict'

angular.module('koolikottApp').directive('dopEmbeddedMaterial', [
    'translationService', 'iconService', 'embedService', 'serverCallService', 'dialogService', 'storageService',
    function (translationService, iconService, embedService, serverCallService, dialogService, storageService) {
        return {
            scope: {
                material: '=',
                chapter: '=',
                objIndex: '=',
                rowIndex: '=',
                contentRow: '='
            },
            templateUrl: 'directives/embeddedMaterial/embeddedMaterial.html',
            controller: ['$scope', '$rootScope', '$location', function ($scope, $rootScope, $location) {
                init();

                function init() {
                    $scope.canPlayVideo = false;
                    $scope.canPlayAudio = false;
                    $scope.videoType = "";
                    $scope.audioType = "";
                    $scope.isEditPortfolioPage = $rootScope.isEditPortfolioPage;
                    $scope.isEditPortfolioMode = $rootScope.isEditPortfolioMode;

                    if ($scope.material) {
                        $scope.materialType = getType();
                    }
                }

                $scope.$watch(function () {
                    return $scope.material;
                }, function () {
                    if ($scope.material && $scope.material.id) {
                        getContentType();
                    }
                });

                function getContentType() {
                    var baseUrl = document.location.origin;
                    var materialSource = getSource($scope.material);
                    // If the initial type is a LINK, try to ask the type from our proxy
                    if (materialSource && (matchType(materialSource) === 'LINK' || !materialSource.startsWith(baseUrl))) {
                        $scope.fallbackType = matchType(materialSource);
                        $scope.proxyUrl = baseUrl + "/rest/material/externalMaterial?url=" + encodeURIComponent($scope.material.source);
                        serverCallService.makeHead($scope.proxyUrl, {}, probeContentSuccess, probeContentFail);
                    }
                    if (materialSource) {
                        $scope.sourceType = matchType(getSource($scope.material));
                    }
                }

                function probeContentSuccess(response) {
                    if (!response()['content-disposition']) {
                        $scope.sourceType = $scope.fallbackType;
                        return;
                    }
                    var filename = response()['content-disposition'].match(/filename="(.+)"/)[1];
                    $scope.sourceType = matchType(filename);
                    if ($scope.sourceType !== 'LINK') {
                        $scope.material.source = $scope.proxyUrl;
                    }
                }

                function probeContentFail() {
                    console.log("Content probing failed!");
                }

                $scope.removeMaterial = function ($event, material) {
                    $event.preventDefault();
                    $event.stopPropagation();

                    var removeMaterialFromChapter = function () {
                        var index = $scope.contentRow.learningObjects.indexOf(material);
                        $scope.contentRow.learningObjects.splice(index, 1);
                    };

                    dialogService.showDeleteConfirmationDialog(
                        'PORTFOLIO_DELETE_MATERIAL_CONFIRM_TITLE',
                        'PORTFOLIO_DELETE_MATERIAL_CONFIRM_MESSAGE',
                        removeMaterialFromChapter);
                };

                $scope.getCorrectLanguageTitle = function (material) {
                    if (material) {
                        return getCorrectLanguageString(material.titles, material.language);
                    }
                };

                $scope.moveItem = function (origin, destination) {
                    if ($scope.contentRow && $scope.contentRow.learningObjects.length < 2) {
                        $scope.chapter.contentRows.splice(origin, 1);
                    }

                    $scope.chapter.contentRows.splice(destination, 0, {learningObjects: [$scope.material]});
                };

                $scope.listItemUp = function () {
                    $scope.moveItem($scope.rowIndex, $scope.rowIndex - 1);
                };

                $scope.listItemDown = function () {
                    $scope.moveItem($scope.rowIndex, $scope.rowIndex + 1);
                };

                $scope.navigateToMaterial = function (material, $event) {
                    $event.preventDefault();
                    storageService.setMaterial(material);


                    $location.path('/material').search({
                        id: material.id
                    });
                };

                $scope.fallbackToLink = function () {
                    console.log($scope.isEditPortfolioMode || !$scope.sourceType || $scope.sourceType === 'LINK');
                    return $scope.isEditPortfolioMode || !$scope.sourceType || $scope.sourceType === 'LINK';
                };

                $scope.$watch(function () {
                    return getSource($scope.material);
                }, function (newValue, oldValue) {
                    if ($scope.material && $scope.material.id) {
                        getSourceType();
                        canPlayVideoFormat();
                        canPlayAudioFormat();
                    }
                });

                function canPlayVideoFormat() {
                    var extension = getSource($scope.material).split('.').pop();
                    var v = document.createElement('video');
                    /* ogv is a subtype of ogg therefore if ogg is supported ogv is also */
                    if (extension == "ogv") {
                        extension = "ogg"
                    }
                    if (v.canPlayType && v.canPlayType('video/' + extension)) {
                        $scope.videoType = extension;
                        $scope.canPlayVideo = true;
                    }
                }

                function canPlayAudioFormat() {
                    var extension = getSource($scope.material).split('.').pop();
                    var v = document.createElement('audio');
                    if (v.canPlayType && v.canPlayType('audio/' + extension)) {
                        $scope.audioType = extension;
                        $scope.canPlayAudio = true;
                    }
                }

                function getSourceType() {
                    if (isYoutubeVideo($scope.material.source)) {
                        $scope.sourceType = 'YOUTUBE';
                    } else if (isSlideshareLink($scope.material.source)) {
                        $scope.sourceType = 'SLIDESHARE';
                    } else if (isVideoLink($scope.material.source)) {
                        $scope.sourceType = 'VIDEO';
                    } else if (isAudioLink($scope.material.source)) {
                        $scope.sourceType = 'AUDIO';
                    } else if (isPictureLink($scope.material.source)) {
                        $scope.sourceType = 'PICTURE';
                    } else if (isEbookLink($scope.material.source)) {
                        // if (isIE()) {
                        //     $scope.sourceType = 'LINK';
                        //     return;
                        // }
                        $scope.sourceType = 'EBOOK';
                        $scope.ebookLink = "/utils/bibi/bib/i/?book=" + $scope.material.uploadedFile.id + "/" + $scope.material.uploadedFile.name;
                    } else if (isPDFLink($scope.material.source)) {
                        if (isIE()) {
                            $scope.sourceType = 'LINK';
                            return;
                        }
                        $scope.sourceType = 'PDF';
                    } else {
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

                function isVideoLink(url) {
                    if (!url) return;
                    var extension = url.split('.').pop().toLowerCase();
                    return extension == "mp4" || extension == "ogv" || extension == "webm";
                }

                function isAudioLink(url) {
                    if (!url) return;
                    var extension = url.split('.').pop().toLowerCase();
                    return extension == "mp3" || extension == "ogg" || extension == "wav";
                }

                function isPictureLink(url) {
                    if (!url) return;
                    var extension = url.split('.').pop().toLowerCase();
                    return extension == "jpg" || extension == "jpeg" || extension == "png" || extension == "gif";
                }

                function isEbookLink(url) {
                    if (!url) return;
                    var extension = url.split('.').pop().toLowerCase();
                    return extension == "epub";
                }

                function isPDFLink(url) {
                    if (!url) return;
                    var extension = url.split('.').pop().toLowerCase();
                    return extension == "pdf";
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
                        $scope.sourceType = 'NOEMBED';

                        if (res.data.html.contains("<iframe")) {
                            $scope.embeddedDataIframe = res.data.html.replace("http:", "");
                        } else {
                            $scope.embeddedData = res.data.html.replace("http:", "");
                        }
                    }
                }

            }]
        };
    }
]);
