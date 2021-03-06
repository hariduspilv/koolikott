'use strict';

angular.module('koolikottApp')
    .controller('materialController', [
        '$scope', 'serverCallService', '$route', 'translationService', '$rootScope',
        'searchService', '$location', 'authenticatedUserService', 'dialogService',
        'toastService', 'iconService', '$mdDialog', 'storageService', 'targetGroupService', 'taxonService', 'taxonGroupingService', 'eventService', 'materialService', '$sce', '$translate',
        function ($scope, serverCallService, $route, translationService, $rootScope,
                  searchService, $location, authenticatedUserService, dialogService,
                  toastService, iconService, $mdDialog, storageService, targetGroupService, taxonService, taxonGroupingService, eventService, materialService, $sce, $translate) {

            $scope.showMaterialContent = false;
            $rootScope.isFullScreen = false;
            $scope.pageUrl = $location.absUrl();
            $scope.path = $location.url()
            $scope.getMaterialSuccess = getMaterialSuccess;
            $scope.taxonObject = {};
            $scope.location = $location.absUrl()
            $scope.isActive = false;

            $scope.materialMetaData = {};

            document.addEventListener('keyup', (e) => {
                if (e.code === "Escape" && $rootScope.isFullScreen)
                    $scope.toggleFullScreen();
            });

            window.addEventListener('popstate', () => {
                if ($rootScope.isFullScreen) {
                    $rootScope.isFullScreen = !$rootScope.isFullScreen;
                    $scope.toggleFullScreen();
                }
            });

            const VISIBILITY_PUBLIC = 'PUBLIC'
            const VISIBILITY_PRIVATE = 'PRIVATE'

            const licenceTypeMap = {
                'CCBY': ['by'],
                'CCBYSA': ['by', 'sa'],
                'CCBYND': ['by', 'nd'],
                'CCBYNC': ['by', 'nc'],
                'CCBYNCSA': ['by', 'nc', 'sa'],
                'CCBYNCND': ['by', 'nc', 'nd'],
                'CCBYSA30': ['by', 'sa']
            };

            const storedMaterial = storageService.getMaterial()
            if (storedMaterial &&
                storedMaterial.type !== ".ReducedMaterial" &&
                storedMaterial.type !== ".AdminMaterial") {
                $scope.material = storedMaterial

                if ($rootScope.isEditPortfolioMode || authenticatedUserService.isAuthenticated()) {
                    $rootScope.selectedSingleMaterial = $scope.material;
                }

                init();
            } else {
                getMaterial(getMaterialSuccess, getMaterialFail);
            }
            $scope.$watch(() => {
                if ($scope.material)
                    $rootScope.tabTitle = $scope.getCorrectLanguageString($scope.material.titles);
                return $scope.material;
            }, () => {
                if ($scope.material && $scope.material.id) {
                    getContentType();
                }
            });

            function getContentType() {
                if ($scope.material.embedSource) {
                    $scope.sourceType = 'EMBEDSOURCE';
                    return;
                }
                const baseUrl = document.location.origin
                const materialSource = getSource($scope.material)
                // If the initial type is a LINK, try to ask the type from our proxy
                if (materialSource && (matchType(materialSource) === 'LINK' || !materialSource.startsWith(baseUrl))) {
                    if (!baseUrl.startsWith("http://localhost:3001")) {
                        $scope.fallbackType = matchType(materialSource);
                        $scope.proxyUrl = `${baseUrl}/rest/material/externalMaterial?id=${$scope.material.id}&url=${encodeURIComponent($scope.material.source)}`;
                        serverCallService.makeHead($scope.proxyUrl, {}, probeContentSuccess, probeContentFail);
                    }
                }
                if (materialSource) {
                    $scope.sourceType = matchType(getSource($scope.material));
                    if ($scope.sourceType === "EBOOK" && isIE()) $scope.material.source += "?archive=true";
                }
            }

            function probeContentSuccess(response) {
                if (!response()['content-disposition']) {
                    $scope.sourceType = $scope.fallbackType;
                    return;
                }
                const filename = response()['content-disposition'].match(/filename="(.+)"/)[1]
                $scope.sourceType = matchType(filename);
            }

            function probeContentFail() {
                console.log("Content probing failed!");
            }

            $scope.getMaterialEducationalContexts = () => {
                let educationalContexts = [];
                if (!$scope.material || !$scope.material.taxons) return;

                $scope.material.taxons.forEach((taxon) => {
                    let edCtx = taxonService.getEducationalContext(taxon);
                    if (edCtx && !educationalContexts.includes(edCtx)) educationalContexts.push(edCtx);
                });

                return educationalContexts;
            };

            $rootScope.$on('fullscreenchange', () => {
                $scope.$apply(() => {
                    $scope.showMaterialContent = !$scope.showMaterialContent;
                });
            });

            $scope.$watch(() => {
                return storageService.getMaterial();
            }, updateMaterial);

            function updateMaterial(newMaterial, oldMaterial) {
                if (newMaterial && oldMaterial && newMaterial !== oldMaterial) {
                    $scope.material = newMaterial;
                    $scope.material.source = decodeUTF8($scope.material.source);
                    processMaterial();
                }
            }

            $scope.getMaterialTitleForImage = () => {
                return $scope.getCorrectLanguageString($scope.material.titles).replace(/\s/g, '-').replace(/^-+|-+(?=-|$)/g, '')
            }

            function getMaterial(success, fail) {
                materialService.getMaterialById($route.current.params.id.split('-')[0]).then(success, fail);
            }

            function getMaterialLdJson(id, success) {
                materialService.getMaterialLdJsonById(id).then(success);
            }

            function getMaterialLdJsonByIdSuccess(ldJson) {
                $scope.materialMetaData = ldJson;
            }

            function getMaterialSuccess(material) {
                if (isEmpty(material)) {
                    console.log('No data returned by getting material. Redirecting to landing page');
                    toastService.show('ERROR_MATERIAL_NOT_FOUND');
                    window.location.replace('/404');
                } else {
                    $scope.material = material;
                    if ($rootScope.isEditPortfolioMode || authenticatedUserService.isAuthenticated()) {
                        $rootScope.selectedSingleMaterial = $scope.material;

                    }
                    init();
                }
            }

            function createLdJson() {
                getMaterialLdJson($scope.material.id, getMaterialLdJsonByIdSuccess);
            }

            function getMaterialFail() {
                console.log('Getting materials failed. Redirecting to landing page');
                toastService.show('ERROR_MATERIAL_NOT_FOUND');
                window.location.replace('/404');
            }

            function processMaterial() {
                if ($scope.material) {
                    if ($scope.sourceType == "EBOOK") {
                        $scope.ebookLink = "/libs/bibi/bib/i/?book=" +
                            $scope.material.uploadedFile.id + "/" +
                            $scope.material.uploadedFile.name;
                    }

                    eventService.notify('material:reloadTaxonObject');
                    $scope.targetGroups = getTargetGroups();
                    $rootScope.learningObjectChanged = ($scope.material.changed > 0);

                    if ($scope.material.embeddable && $scope.sourceType === 'LINK') {
                        if (authenticatedUserService.isAuthenticated()) {
                            getSignedUserData()
                        } else {
                            $scope.material.iframeSource = $sce.trustAsResourceUrl($scope.material.source);
                        }
                    }
                }
            }

            function init() {
                $scope.material.source = getSource($scope.material);
                getContentType();
                processMaterial();
                showUnreviewedMessage();
                createLdJson();

                $scope.getCorrectLanguageString = (languageStringList) => {
                    if (languageStringList) {
                        return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), $scope.material.language);
                    }
                };

                let correctLanguageTitle = $scope.getCorrectLanguageString($scope.material.titlesForUrl).replace(/(-)\1+/g, '-')
                correctLanguageTitle = correctLanguageTitle.charAt(correctLanguageTitle.length - 1) === '-' ? correctLanguageTitle.slice(0, -1) : correctLanguageTitle
                correctLanguageTitle = correctLanguageTitle.charAt(0) === '-' ? correctLanguageTitle.slice(1) : correctLanguageTitle

                $scope.materialUrl = `/oppematerjal/${$scope.material.id}-${correctLanguageTitle}`
                if ($scope.path !== $scope.materialUrl) {
                    $location.url($scope.materialUrl)
                }

                eventService.notify('material:reloadTaxonObject');

                $rootScope.learningObjectPrivate = ["PRIVATE"].includes($scope.material.visibility);

                $rootScope.learningObjectImproper = ($scope.material.improper > 0);
                $rootScope.learningObjectDeleted = ($scope.material.deleted === true);
                $rootScope.learningObjectUnreviewed = !!$scope.material.unReviewed;
                materialService.increaseViewCount($scope.material);

            }

            $scope.updateMaterialVisibility = () => {
                serverCallService
                    .makePost('rest/material/update', $scope.material)
                    .then(({data: material}) => {
                        if (material) {
                            storageService.setMaterial(null)
                            $location.url('/oppematerjal/' + $scope.material.id)
                            $route.reload()
                        }
                    })
            }

            $scope.makePrivate = () => {
                $scope.material.visibility = VISIBILITY_PRIVATE
                $scope.updateMaterialVisibility()
            }

            $scope.makePublic = () => {
                if (!$scope.material.licenseType || $scope.material.licenseType.name !== 'CCBYSA30' ||
                    ($scope.material.picture &&
                        $scope.material.picture.licenseType.name !== 'CCBYSA30')) {
                    $mdDialog.show({
                        templateUrl: '/views/learningObjectAgreementDialog/learningObjectLicenseAgreementDialog.html',
                        controller: 'learningObjectLicenseAgreementController',
                    }).then((res) => {
                        if (res.accept) {
                            $rootScope.materialLicenseTypeChanged = true
                            $scope.edit()
                        }
                    })
                } else {
                    $scope.material.visibility = VISIBILITY_PUBLIC
                    $scope.updateMaterialVisibility()
                }
            }

            $scope.getLicenseIconList = () => {
                if ($scope.material && $scope.material.licenseType) {
                    return licenceTypeMap[$scope.material.licenseType.name];
                }
            };

            $scope.formatMaterialIssueDate = (issueDate) => formatIssueDate(issueDate);
            $scope.formatMaterialUpdatedDate = (updatedDate) => formatDateToDayMonthYear(updatedDate);
            $scope.isNullOrZeroLength = (arg) => !arg || !arg.length;

            $scope.getAuthorSearchURL = (firstName, surName) => {
                return `/search/result?q=author:"${firstName} ${surName}"&type=all`;
            };

            $scope.getPublisherSearchURL = (name) => {
                return `/search/result?q=publisher:"${name}"&type=all`;
            };
            $scope.isLoggedIn = () => authenticatedUserService.isAuthenticated();
            $scope.isAdmin = () => authenticatedUserService.isAdmin();
            $scope.isModerator = () => authenticatedUserService.isModerator();
            $scope.isRestricted = () => authenticatedUserService.isRestricted();
            $scope.isOwner = () => authenticatedUserService.isOwner($scope.material);
            $scope.showImproper = () => $rootScope.learningObjectImproper;

            $scope.modUser = () => !!(authenticatedUserService.isModerator() || authenticatedUserService.isAdmin());

            function showUnreviewedMessage() {
                if ($scope.material && $scope.material.id) {
                    serverCallService.makeGet('rest/learningObject/showUnreviewed?id=' + $scope.material.id)
                        .then(response => {
                            $scope.showUnreviewedLO = response.data;
                        })
                }
            }

            $scope.processMaterial = () => {
                processMaterial();
            };

            $scope.$on("tags:updateMaterial", (event, value) => {
                updateMaterial(value, $scope.material);
            });

            $scope.isAdminButtonsShowing = function () {
                return $rootScope.learningObjectDeleted === true || $rootScope.learningObjectImproper === true;
            };

            $scope.dotsAreShowing = function () {
                return $rootScope.learningObjectDeleted === false || $scope.isAdmin();
            };

            function getSignedUserData() {
                serverCallService.makeGet("rest/user/getSignedUserData", {}, getSignedUserDataSuccess, getSignedUserDataFail);
            }

            function getSignedUserDataSuccess(data) {
                let url = $scope.material.source;
                url += (url.split('?')[1] ? '&' : '?') + "dop_token=" + encodeURIComponent(data);
                $scope.material.linkSource = url;
            }

            function getSignedUserDataFail(data, status) {
                console.log("Failed to get signed user data.")
                $scope.material.linkSource = $scope.material.source;
            }

            $scope.toggleFullScreen = () => {
                $rootScope.isFullScreen = !$rootScope.isFullScreen;
                toggleFullScreen();
                if ($rootScope.isFullScreen) {
                    toastService.show('YOU_CAN_LEAVE_PAGE_WITH_ESC', 15000, 'user-missing-id');
                    gTagCaptureEvent('full-screen', 'teaching material')
                } else {
                    toastService.hide()
                }
            }

            $scope.edit = () => {
                var editMaterialScope = $scope.$new(true);
                editMaterialScope.material = angular.copy($scope.material);

                gTagCaptureEvent('modify', 'teaching material')

                $mdDialog.show({
                    templateUrl: 'addMaterialDialog.html',
                    controller: 'addMaterialDialogController',
                    controllerAs: '$ctrl',
                    scope: editMaterialScope,
                    locals: {isEditMode: true}
                }).then((material) => {
                    if (material) {
                        $scope.material = material;
                        processMaterial();
                        $rootScope.$broadcast('materialEditModalClosed');
                    }
                });
            };

            $scope.getType = () => {
                if ($scope.material === undefined || $scope.material === null) return '';

                return iconService.getMaterialIcon($scope.material.resourceTypes);
            };

            $scope.getTypeName = () => {
                if (!$scope.material) return;

                var resourceTypes = $scope.material.resourceTypes;
                if (resourceTypes.length == 0) {
                    return 'NONE';
                }
                return resourceTypes[resourceTypes.length - 1].name;
            };

            $scope.confirmMaterialDeletion = () => {

                gTagCaptureEvent('delete', 'teaching material')

                dialogService.showConfirmationDialog(
                    'MATERIAL_CONFIRM_DELETE_DIALOG_TITLE',
                    'MATERIAL_CONFIRM_DELETE_DIALOG_CONTENT',
                    'ALERT_CONFIRM_POSITIVE',
                    'ALERT_CONFIRM_NEGATIVE',
                    () => {
                        deleteMaterial(serverCallService, toastService, $scope, $rootScope)
                    });
            };

            function deleteMaterial(serverCallService, toastService, $scope, $rootScope) {
                serverCallService
                    .makePost('rest/material/delete', {id: $scope.material.id, type: $scope.material.type})
                    .then(() => {
                        toastService.show('MATERIAL_DELETED');
                        $scope.material.deleted = true
                        $rootScope.learningObjectDeleted = true
                        $rootScope.$broadcast('dashboard:adminCountsUpdated')
                    })
            }

            $scope.isUsersMaterial = () => {
                if ($scope.material && authenticatedUserService.isAuthenticated()) {
                    var userID = authenticatedUserService.getUser().id;
                    var creator = $scope.material.creator;

                    return creator && creator.id === userID
                }
            };

            $scope.restoreMaterial = () => {
                serverCallService
                    .makePost('rest/admin/deleted/restore', $scope.material)
                    .then(restoreSuccess, restoreFail)
            };

            function restoreSuccess() {
                toastService.show('MATERIAL_RESTORED');
                $scope.material.deleted = false
                $scope.material.improper = false
                $scope.material.unReviewed = false
                $scope.material.changed = false
                $rootScope.learningObjectDeleted = false
                $rootScope.learningObjectImproper = false
                $rootScope.learningObjectUnreviewed = false
                $rootScope.learningObjectChanged = false
                $rootScope.$broadcast('dashboard:adminCountsUpdated');
            }

            function restoreFail() {
                log("Restoring material failed");
            }

            function getTargetGroups() {
                if (Array.isArray($scope.material.targetGroups) && $scope.material.targetGroups.length) {
                    return targetGroupService.getConcentratedLabelByTargetGroups($scope.material.targetGroups);
                }
            }

            $scope.isAdminOrModerator = function () {
                return (
                    authenticatedUserService.isAdmin() ||
                    authenticatedUserService.isModerator()
                )
            }

            $scope.isAdminOrModeratorOrCreator = function () {
                return (
                    authenticatedUserService.isAdmin() ||
                    authenticatedUserService.isModerator() ||
                    $scope.isUsersMaterial()
                )
            }

            $scope.canChangeMaterialVisibility = function () {
                if ($scope.material) {
                    return (
                        $scope.isAdminOrModeratorOrCreator &&
                            !$scope.material.deleted
                    )
                }
            }

            $scope.setRecommendation = (recommendation) => {
                if ($scope.material)
                    $scope.material.recommendation = recommendation
            }

            $scope.$on('$destroy', () =>
                storageService.setMaterial(null)
            )

            $scope.captureOutboundLink = function (url) {
                window.captureOutboundLink(url);
            };

            //Open all external links in new tab
            window.addEventListener('click', function (event) {
                let el = event.target

                if (el.tagName === 'A' && !el.isContentEditable && el.host !== window.location.host) {
                    el.setAttribute('target', '_blank')
                }
            }, true)
        }
    ]);
