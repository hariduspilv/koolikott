'use strict';

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
        $scope.showMaterialContent = false;
        $scope.canPlayVideo = false;
        $scope.canPlayAudio = false;
        $scope.videoType = "";
        $scope.audioType = "";
        $scope.sourceType = "";
        $scope.isEditPortfolioPage = $rootScope.isEditPortfolioPage;
        $scope.isEditPortfolioMode = $rootScope.isEditPortfolioMode;

        if ($scope.material) {
            $scope.material.source = getSource($scope.material);
            $scope.materialType = getMaterialIcon();
            audioSetup();
            videoSetup();
            sourceTypeAndPdfSetup();
            externalUrlProxyModification();
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
            $scope.materialType = getMaterialIcon();
            audioSetup();
            sourceTypeAndPdfSetup();
            externalUrlProxyModification();
        }
    });

    function externalUrlProxyModification() {
        //todo baseUrl
        $scope.proxyUrl = undefined;
        const baseUrl = document.location.origin;
        //todo getSource
        const materialSource = getSource($scope.material);
        // If the initial type is a LINK, try to ask the type from our proxy
        //todo matchtype
        if (materialSource && (matchType(materialSource) === 'LINK' || !materialSource.startsWith(baseUrl))) {
            //todo matchtype
            $scope.fallbackType = matchType(materialSource);
            if (!$scope.material.source) {
                console.log("source is missing")
            }
            $scope.proxyUrl = baseUrl + "/rest/material/externalMaterial?url=" + encodeURIComponent($scope.material.source);
            serverCallService.makeHead($scope.proxyUrl, {}, probeContentSuccess, probeContentFail);
        }
        if (materialSource) {
            //todo matchType
            //todo getSource
            $scope.sourceType = matchType(getSource($scope.material));
        }
    }

    function probeContentSuccess(response) {
        console.log("Content probing succeeded!");
        if (!response()['content-disposition']) {
            $scope.sourceType = $scope.fallbackType;
            return;
        }
        let filename = response()['content-disposition'].match(/filename="(.+)"/)[1];
        //todo matchtype
        $scope.sourceType = matchType(filename);
        if ($scope.sourceType !== 'LINK' && $scope.sourceType === 'PDF') {
            $scope.material.PDFLink = pdfjsLink(encodeURIComponent($scope.proxyUrl));
        }
    }

    $scope.removeMaterial = function ($event, material) {
        $event.preventDefault();
        $event.stopPropagation();

        function removeMaterialFromChapter() {
            let index = $scope.contentRow.learningObjects.indexOf(material);
            $scope.contentRow.learningObjects.splice(index, 1);
        }

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

    function videoSetup() {
        //todo getSource
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
                $(videoElement).on('error', videoSetup, true);
                video.load();
            } else {
                $timeout(videoSetup, 100);
            }
        }
    }

    function audioSetup() {
        if ($scope.canPlayVideo)return;
        //todo getSource
        let extension = getSource($scope.material).split('.').pop();
        let v = document.createElement('audio');
        if (v.canPlayType && v.canPlayType('audio/' + extension)) {
            $scope.audioType = extension;
            $scope.canPlayAudio = true;
        }
    }

    function sourceTypeAndPdfSetup() {
        //todo set type and work on pdf in a different method
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
            $scope.ebookLink = bibiLink($scope.material.uploadedFile.id, $scope.material.uploadedFile.name);

            let ebookElement = '.embed-ebook-' + $scope.material.id;
            if ($(ebookElement).length !== 0) {
                $(ebookElement).html(iFrameLink($scope.ebookLink));
            } else {
                $timeout(sourceTypeAndPdfSetup, 100);
            }
        } else if (isPDFLink($scope.material.source)) {
            const baseUrl = document.location.origin;
            if ($scope.material.source.startsWith(baseUrl) && !$scope.proxyUrl) {
                $scope.material.PDFLink = pdfjsLink($scope.material.source);
            }
            $scope.sourceType = 'PDF';

            let pdfElement = '.embed-pdf-' + $scope.material.id;
            if ($(pdfElement).length !== 0) {
                $(pdfElement).html(iFrameLink($scope.material.PDFLink));
            } else {
                $timeout(sourceTypeAndPdfSetup, 100);
            }
        } else {
            embedService.getEmbed(getSource($scope.material), embedCallback);
        }
    }

    function getCorrectLanguageString(languageStringList, materialLanguage) {
        if (languageStringList) {
            return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), materialLanguage);
        }
    }

    function embedCallback(result) {
        if (result && result.data.html) {
            $scope.embeddedDataIframe = null;
            $scope.embeddedData = null;
            $scope.sourceType = 'NOEMBED';

            if (result.data.html.contains("<iframe")) {
                $scope.embeddedDataIframe = result.data.html.replace("http:", "");
            } else {
                $scope.embeddedData = result.data.html.replace("http:", "");
            }
        } else {
            if ($scope.material.source) {
                $scope.iframeSource = $sce.trustAsResourceUrl($scope.material.source.replace("http:", ""));
            }
        }
    }

    function getMaterialIcon() {
        return iconService.getMaterialIcon($scope.material.resourceTypes);
    }

    function iFrameLink(ebookLink) {
        return '<iframe width="100%" height="500px" src="' + ebookLink + '"></iframe>';
    }

    function bibiLink(fileId, filename) {
        return "/utils/bibi/bib/i/?book=" + fileId + "/" + filename;
    }

    function pdfjsLink(source) {
        return "/utils/pdfjs/web/viewer.html?file=" + source;
    }

    function getMaterialFail() {
        console.log('Getting materials failed');
    }

    function probeContentFail() {
        console.log("Content probing failed!");
    }
}
