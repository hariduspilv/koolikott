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
            $scope.newComment = {};
            $scope.pageUrl = $location.absUrl();
            $scope.getMaterialSuccess = getMaterialSuccess;
            $scope.taxonObject = {};
            $scope.location = $location.absUrl()
            $scope.isActive = false;

            document.addEventListener('keyup', (e) => {
                if (e.code === "Escape" && $rootScope.isFullScreen)
                    $scope.toggleFullScreen();
            });

            window.addEventListener('popstate',() => {
                if ($rootScope.isFullScreen) {
                    $rootScope.isFullScreen = !$rootScope.isFullScreen;
                    $scope.toggleFullScreen();
                }
            });

            const licenceTypeMap = {
                'CCBY': ['by'],
                'CCBYSA': ['by', 'sa'],
                'CCBYND': ['by', 'nd'],
                'CCBYNC': ['by', 'nc'],
                'CCBYNCSA': ['by', 'nc', 'sa'],
                'CCBYNCND': ['by', 'nc', 'nd']
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

            function getMaterial(success, fail) {
                materialService.getMaterialById($route.current.params.id.split('-')[0]).then(success, fail)
            }

            function getMaterialSuccess(material) {
                if (isEmpty(material)) {
                    console.log('No data returned by getting material. Redirecting to landing page');
                    toastService.show('ERROR_MATERIAL_NOT_FOUND');
                    window.location.replace('/404');
                } else {
                    $scope.material = material;
                    console.log(material.licenseType.name)
                    if ($rootScope.isEditPortfolioMode || authenticatedUserService.isAuthenticated()) {
                        $rootScope.selectedSingleMaterial = $scope.material;

                    }
                    init();

                    //metaandmete lisamine

                    $scope.materialMetaData = createMetaData(material);

                    addAuthors(material);

                    if (material.peerReviews.length > 0) return addPeerReview(material);

                }
            }

            function createMetaData(material) {
                return [
                    {
                        '@context': 'http://schema.org/',
                        '@type': 'CreativeWork',
                        'url': $scope.pageUrl,
                        'publisher': {
                            '@type': 'Organization',
                            'name': 'e-koolikott.ee'
                        },
                        'audience': {
                            '@type': 'Audience',
                            'audienceType': audienceType(material)
                        },
                        'dateCreated': formatIssueDate(material.issueDate),
                        'datePublished': material.added,
                        'license': addLicense(material.licenseType),
                        'typicalAgeRange': material.targetGroups.map(targetGroup => getTypicalAgeRange(targetGroup)),
                        'interactionCount': material.views,
                        'headline': material.titles.map(title => title.text),
                        'keywords': material.tags,
                        'text': material.descriptions.map(description => description.text)
                    },
                    {
                        '@context': 'https://schema.org',
                        '@type': 'Organization',
                        'url': 'https://e-koolikott.ee',
                        'logo': 'https://e-koolikott.ee/ekoolikott.png'
                    },
                    {
                        '@context': 'https://schema.org',
                        '@type': 'WebSite',
                        'url': 'https://www.e-koolikott.ee/',
                        'potentialAction': {
                            '@type': 'SearchAction',
                            'target': 'https://query.e-koolikott.ee/search?q={search_term_string}',
                            'query-input': 'required name=search_term_string'
                        }
                    },
                    {
                        '@context': 'https://schema.org',
                        '@type': 'BreadcrumbList',
                        'itemListElement': [{
                            '@type': 'ListItem',
                            'position': 1,
                            'name': $translate.instant(material.taxonPositionDto[0].taxonLevelName),
                            'item': `https://e-koolikott.ee/search/result/?taxon=${material.taxonPositionDto[0].taxonLevelId}`//TODO at the moment 1st taxonroute taken
                        }, {
                            '@type': 'ListItem',
                            'position': 2,
                            'name': $translate.instant((`DOMAIN_${material.taxonPositionDto[1].taxonLevelName}`).toUpperCase()),
                            'item': `https://e-koolikott.ee/search/result/?taxon=${material.taxonPositionDto[1].taxonLevelId}`
                        }]
                    }
                ]
            }

            function addPeerReview() {
                $scope.materialMetaData[0].review = {
                    '@type': 'Review',
                    'reviewRating': {
                        '@type': 'Rating',
                        'ratingValue': '5',
                        'bestRating': '5'
                    },
                    'reviewBody': 'Vastab nõuetele',
                    'publisher': {
                        '@type': 'Organization',
                        'name': 'e-koolikott.ee'
                    }
                }
            }

            function addAuthors(material) {
                let authorsNames = [];

                material.authors.map(materialAuthor => {
                    const author = {};
                    author['@type'] = 'Person';
                    author[`name`] = `${materialAuthor.name} ${materialAuthor.surname}`;
                    authorsNames.push(author);
                });
                $scope.materialMetaData[0].author = authorsNames;
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

                eventService.notify('material:reloadTaxonObject');

                $rootScope.learningObjectPrivate = ["PRIVATE"].includes($scope.material.visibility);
                $rootScope.learningObjectImproper = ($scope.material.improper > 0);
                $rootScope.learningObjectDeleted = ($scope.material.deleted === true);
                $rootScope.learningObjectUnreviewed = !!$scope.material.unReviewed;

                if ($scope.material)
                materialService.increaseViewCount($scope.material);

            }

            $scope.getLicenseIconList = () => {
                if ($scope.material && $scope.material.licenseType) {
                    return licenceTypeMap[$scope.material.licenseType.name];
                }
            };

            $scope.getCorrectLanguageString = (languageStringList) => {
                if (languageStringList) {
                    return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), $scope.material.language);
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
            $scope.isOwner= () => authenticatedUserService.isOwner($scope.material);

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

            $scope.isAdminButtonsShowing = function(){
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
                if ($rootScope.isFullScreen){
                    toastService.show('YOU_CAN_LEAVE_PAGE_WITH_ESC', 15000, 'user-missing-id');
                    gTagCaptureEvent('full-screen', 'teaching material')
                }
                else {
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
                    locals: { isEditMode: true }
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
                    .makePost('rest/material/delete', { id: $scope.material.id, type: $scope.material.type })
                    .then(() => {
                        toastService.show('MATERIAL_DELETED');
                        $scope.material.deleted = true
                        $rootScope.learningObjectDeleted = true
                        $rootScope.$broadcast('dashboard:adminCountsUpdated')
                    })
            }

            $scope.isUsersMaterial = () => {
                if ($scope.material && authenticatedUserService.isAuthenticated() && !authenticatedUserService.isRestricted()) {
                    var userID = authenticatedUserService.getUser().id;
                    var creator = $scope.material.creator;

                    return creator && creator.id === userID
                }
            };

            $scope.restoreMaterial = () => {
                materialService.restoreMaterial($scope.material)
                    .then(restoreSuccess, restoreFail);
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

            $scope.setRecommendation = (recommendation) => {
                if ($scope.material)
                    $scope.material.recommendation = recommendation
            }

            $scope.$on('$destroy', () =>
                storageService.setMaterial(null)
            )

            $scope.captureOutboundLink = function(url) {
                window.captureOutboundLink(url);
            };
        }
    ]);
