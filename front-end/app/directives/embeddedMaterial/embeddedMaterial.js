'use strict'

angular.module('koolikottApp')
    .directive('dopEmbeddedMaterial', function () {
            return {
                scope: {
                    material: '=material',
                    chapter: '=',
                    objIndex: '=',
                    rowIndex: '=',
                    contentRow: '=',
                    embeddable: '='
                },
                templateUrl: 'directives/embeddedMaterial/embeddedMaterial.html',
                controller: embeddedMaterialController,
            };
        }
    );

embeddedMaterialController.$inject = ['translationService', 'iconService', 'embedService',
    'serverCallService', 'dialogService', 'storageService', '$rootScope', '$sce', '$timeout',
    'materialService', '$route', '$scope', '$location'];

function embeddedMaterialController(translationService, iconService, embedService,
                                    serverCallService, dialogService, storageService, $rootScope, $sce, $timeout,
                                    materialService, $route, $scope, $location) {
    init();

    $scope.showMaterialContent = false;

    $rootScope.$on('fullscreenchange', () => {
        $scope.$apply(() => {
            $scope.showMaterialContent = !$scope.showMaterialContent;
        });
    });

    $scope.showSourceFullscreen = ($event, ctrl) => {
        $event.preventDefault();
        ctrl.toggleFullscreen();
    };

    function init() {
        $scope.canPlayVideo = false;
        $scope.canPlayAudio = false;
        $scope.videoType = "";
        $scope.audioType = "";
        $scope.sourceType = "";
        $scope.isEditPortfolioPage = $rootScope.isEditPortfolioPage;
        $scope.isEditPortfolioMode = $rootScope.isEditPortfolioMode;

        if ($scope.material) {
            $scope.material.source = getSource($scope.material);
            $scope.materialType = getType();
            canPlayAudioFormat();
            canPlayVideoFormat();
            getSourceType();
            getContentType();
        } else {
            //try to get material from route param and init again
            getMaterialFromRouteParam();
        }
    }

    function getMaterialFromRouteParam() {
        materialService.getMaterialById($route.current.params.id).then(getMaterialSuccess, getMaterialFail)
    }

    function getMaterialSuccess(material) {
        if (isEmpty(material)) {
            console.log('No data returned by getting material');
        } else {
            console.log('Material found by query');
            $scope.material = material;

            if ($rootScope.isEditPortfolioMode || authenticatedUserService.isAuthenticated()) {
                $rootScope.selectedSingleMaterial = $scope.material;
            }
            init();
        }
    }

    $scope.$watch(function () {
        return $scope.material;
    }, function () {
        if ($scope.material && $scope.material.id) {
            $scope.material.source = getSource($scope.material);
            $scope.materialType = getType();
            canPlayAudioFormat();
            getSourceType();
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
            if ($scope.sourceType === 'PDF') $scope.material.PDFLink = "/utils/pdfjs/web/viewer.html?file=" + encodeURIComponent($scope.proxyUrl);
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
        return $scope.isEditPortfolioMode || !$scope.sourceType || $scope.sourceType === 'LINK';
    };

    function canPlayVideoFormat() {
        let extension = getSource($scope.material).split('.').pop();
        let v = document.createElement('video');
        /* ogv is a subtype of ogg therefore if ogg is supported ogv is also */
        if (extension === "ogv") {
            extension = "ogg";
            let source = $scope.material.source;
            $scope.material.source = source.substr(0, source.lastIndexOf(".")) + ".ogg";
        }
        if (v.canPlayType && v.canPlayType('video/' + extension)) {
            $scope.videoType = extension;
            $scope.canPlayVideo = true;

            let videoElement = '.embed-video-' + $scope.material.id;
            if ($(videoElement).length !== 0) {

                let video = $('<video />', {
                    id: 'video',
                    src: $scope.material.source,
                    type: 'video/' + extension,
                    controls: true,
                    width: '100%',
                    preload: 'metadata'
                });

                $(videoElement).html(video);
                $(videoElement).on('error', canPlayVideoFormat, true);
                video.load();
            } else {
                $timeout(canPlayVideoFormat, 100);
            }
        }
    }

    function canPlayAudioFormat() {
        if ($scope.canPlayVideo)return;

        let extension = getSource($scope.material).split('.').pop();
        let v = document.createElement('audio');
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
            if (isIE()) {
                $scope.sourceType = 'LINK';
                $scope.material.source += "?archive=true";
                return;
            }

            $scope.sourceType = 'EBOOK';
            $scope.ebookLink = "/utils/bibi/bib/i/?book=" + $scope.material.uploadedFile.id + "/" + $scope.material.uploadedFile.name;

            let ebookElement = '.embed-ebook-' + $scope.material.id;
            if ($(ebookElement).length !== 0) {
                $(ebookElement).html('<iframe width="100%" height="500px" src="' + $scope.ebookLink + '"></iframe>');
            } else {
                $timeout(getSourceType, 100);
            }
        } else if (isPDFLink($scope.material.source)) {
            $scope.material.PDFLink = "/utils/pdfjs/web/viewer.html?file=" + $scope.material.source;
            $scope.sourceType = 'PDF';

            let pdfElement = '.embed-pdf-' + $scope.material.id;
            if ($(pdfElement).length !== 0) {
                $(pdfElement).html('<iframe width="100%" height="500px" src="' + $scope.material.PDFLink + '"></iframe>');
            } else {
                $timeout(getSourceType, 100);
            }
        } else {
            embedService.getEmbed(getSource($scope.material), embedCallback);
        }
    }

    function getType() {
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
        } else {
            if ($scope.material.source) {
                $scope.iframeSource = $sce.trustAsResourceUrl($scope.material.source.replace("http:", ""));
            }
        }
    }

    function getMaterialFail() {
        console.log('Getting materials failed');
    }
}
