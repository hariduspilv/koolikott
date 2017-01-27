'use strict';

angular.module('koolikottApp')
    .controller('materialController', [
        '$scope', 'serverCallService', '$route', 'translationService', '$rootScope',
        'searchService', '$location', 'alertService', 'authenticatedUserService', 'dialogService',
        'toastService', 'iconService', '$mdDialog', 'storageService', 'targetGroupService', 'taxonService', 'taxonGroupingService', 'eventService',
        function ($scope, serverCallService, $route, translationService, $rootScope,
                  searchService, $location, alertService, authenticatedUserService, dialogService,
                  toastService, iconService, $mdDialog, storageService, targetGroupService, taxonService, taxonGroupingService, eventService) {

            $scope.showMaterialContent = false;
            $scope.newComment = {};
            $scope.pageUrl = $location.absUrl();
            $scope.getMaterialSuccess = getMaterialSuccess;
            $scope.taxonObject = {};
            $scope.materialCommentsOpen = false;

            const licenceTypeMap = {
                'CCBY':  ['by'],
                'CCBYSA': ['by', 'sa'],
                'CCBYND': ['by', 'nd'],
                'CCBYNC': ['by', 'nc'],
                'CCBYNCSA': ['by', 'nc', 'sa'],
                'CCBYNCND': ['by', 'nc', 'nd']
            };

            if (storageService.getMaterial() && storageService.getMaterial().type !== ".ReducedMaterial") {
                $scope.material = storageService.getMaterial();

                if ($rootScope.isEditPortfolioMode || authenticatedUserService.isAuthenticated()) {
                    $rootScope.selectedSingleMaterial = $scope.material;
                }

                init();
            } else {
                getMaterial(getMaterialSuccess, getMaterialFail);
            }

            $scope.$watch(() => {
                return $scope.material;
            }, () => {
                if ($scope.material && $scope.material.id) {
                    getContentType();
                }
            });

            $scope.toggleCommentSection = () => {
                $scope.commentsOpen = !$scope.commentsOpen;
            };

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
                    if ($scope.sourceType == "EBOOK" && isIE())$scope.material.source += "?archive=true";
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

            $rootScope.$on('fullscreenchange', () => {
                $scope.$apply(() => {
                    $scope.showMaterialContent = !$scope.showMaterialContent;
                });
            });

            $scope.$watch(() => {
                return storageService.getMaterial();
            }, updateMaterial);

            function updateMaterial(newMaterial, oldMaterial) {
                if (newMaterial !== oldMaterial) {
                    $scope.material = newMaterial;
                    processMaterial();
                }
            }

            function getMaterial(success, fail) {
                var materialId = $route.current.params.id;
                var params = {
                    'materialId': materialId
                };
                serverCallService.makeGet("rest/material", params, success, fail);
            }

            function getMaterialSuccess(material) {
                if (isEmpty(material)) {
                    console.log('No data returned by getting material. Redirecting to landing page');
                    alertService.setErrorAlert('ERROR_MATERIAL_NOT_FOUND');
                    $location.url("/");
                } else {
                    $scope.material = material;

                    if ($rootScope.isEditPortfolioMode || authenticatedUserService.isAuthenticated()) {
                        $rootScope.selectedSingleMaterial = $scope.material;
                    }
                    init();
                }
            }

            function getMaterialFail() {
                log('Getting materials failed. Redirecting to landing page');
                alertService.setErrorAlert('ERROR_MATERIAL_NOT_FOUND');
                $location.url("/");
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
                            $scope.material.iframeSource = $scope.material.source;
                        }
                    }
                }
            }

            function init() {
                $scope.material.source = getSource($scope.material);
                getContentType();
                processMaterial();

                eventService.subscribe($scope, 'taxonService:mapInitialized', getTaxonObject);
                eventService.subscribe($scope, 'material:reloadTaxonObject', getTaxonObject);

                eventService.notify('material:reloadTaxonObject');

                $rootScope.learningObjectBroken = ($scope.material.broken > 0);
                $rootScope.learningObjectImproper = ($scope.material.improper > 0);
                $rootScope.learningObjectDeleted = ($scope.material.deleted == true);

                if (authenticatedUserService.isAdmin() || authenticatedUserService.isModerator()) {
                    if ($scope.material.improper > 0) {
                        serverCallService.makeGet("rest/impropers", {}, sortImpropers, getItemsFail);
                    }
                }

                var viewCountParams = {
                    'type': '.Material',
                    'id': $scope.material.id
                };

                serverCallService.makePost("rest/material/increaseViewCount", viewCountParams, () => {
                }, () => {
                });

            }

            function getItemsFail() {
                console.log("Failed to get data");
            }

            function sortImpropers(impropers) {
                var improper;

                for (var i = 0; i < impropers.length; i++) {
                    if (impropers[i].learningObject.id === $scope.material.id) {
                        improper = impropers[i];
                    }
                }

                $rootScope.setReason(improper.reason);
            }

            $scope.getLicenseIconList = () => {
                if ($scope.material && $scope.material.licenseType) {
                    return licenceTypeMap[$scope.material.licenseType.name];
                }
            };

            $scope.getMaterialEducationalContexts = () => {
                var educationalContexts = [];
                if (!$scope.material || !$scope.material.taxons) return;

                $scope.material.taxons.forEach((taxon) => {
                    let edCtx = taxonService.getEducationalContext(taxon);
                    if (edCtx && !educationalContexts.includes(edCtx)) educationalContexts.push(edCtx);
                });

                return educationalContexts;
            };

            $scope.getCorrectLanguageString = (languageStringList) => {
                if (languageStringList) {
                    return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), $scope.material.language);
                }
            };

            $scope.formatMaterialIssueDate = (issueDate) => formatIssueDate(issueDate);
            $scope.formatMaterialUpdatedDate = (updatedDate) => formatDateToDayMonthYear(updatedDate);
            $scope.isNullOrZeroLength = (arg) => !arg || !arg.length;

            $scope.getAuthorSearchURL = ($event, firstName, surName) => {
                $event.preventDefault();

                searchService.setSearch('author:"' + firstName + " " + surName + '"');
                $location.url(searchService.getURL());
            };

            $scope.showSourceFullscreen = ($event) => {
                $event.preventDefault();

                $scope.fullscreenCtrl.toggleFullscreen();
            };

            $scope.isLoggedIn = () => authenticatedUserService.isAuthenticated();
            $scope.isAdmin = () => authenticatedUserService.isAdmin();
            $scope.isModerator = () => authenticatedUserService.isModerator();
            $scope.isRestricted = () => authenticatedUserService.isRestricted();
            $scope.modUser = () => !!(authenticatedUserService.isModerator() || authenticatedUserService.isAdmin());

            $scope.processMaterial = () => {
                processMaterial();
            };

            $scope.$on("tags:updateMaterial", (event, value) => {
                updateMaterial(value, $scope.material);
            });

            $scope.isAdminButtonsShowing = () => {
                return ($rootScope.learningObjectDeleted == false
                    && $rootScope.learningObjectImproper == false
                    && $rootScope.learningObjectBroken == true)
                    || ($rootScope.learningObjectDeleted == false
                    && $rootScope.learningObjectBroken == false
                    && $rootScope.learningObjectImproper == true)
                    || ($rootScope.learningObjectDeleted == false
                    && $rootScope.learningObjectBroken == true
                    && $rootScope.learningObjectImproper == true)
                    || ($rootScope.learningObjectDeleted == true);
            };

            function getTaxonObject() {
                if ($scope.material && $scope.material.taxons) {
                    $scope.taxonObject = taxonGroupingService.getTaxonObject($scope.material.taxons);
                }
            }

            function getSignedUserData() {
                serverCallService.makeGet("rest/user/getSignedUserData", {}, getSignedUserDataSuccess, getSignedUserDataFail);
            }

            function getSignedUserDataSuccess(data) {
                var url = $scope.material.source;
                var v = encodeURIComponent(data);
                url += (url.split('?')[1] ? '&' : '?') + "dop_token=" + v;

                $scope.material.iframeSource = url;
            }

            function getSignedUserDataFail(data, status) {
                console.log("Failed to get signed user data.")
            }

            $scope.addComment = () => {
                var url = "rest/comment/material";
                var params = {
                    'comment': $scope.newComment,
                    'material': {
                        'type': '.Material',
                        'id': $scope.material.id
                    }
                };
                serverCallService.makePost(url, params, addCommentSuccess, addCommentFailed);
            };

            $scope.edit = () => {
                var editMaterialScope = $scope.$new(true);
                editMaterialScope.material = $scope.material;

                $mdDialog.show({
                    templateUrl: 'addMaterialDialog.html',
                    controller: 'addMaterialDialogController',
                    scope: editMaterialScope
                }).then((material) => {
                    if (material) {
                        $scope.material = material;
                        processMaterial();
                        $rootScope.$broadcast('materialEditModalClosed');
                    }
                });
            };

            function addCommentSuccess() {
                $scope.newComment.text = "";

                getMaterial((material) => {
                    $scope.material = material;
                }, () => {
                    log("Comment success, but failed to reload material.");
                });
            }

            function addCommentFailed() {
                log('Adding comment failed.');
            }

            $scope.getType = () => {
                if ($scope.material === undefined || $scope.material === null) return '';

                return iconService.getMaterialIcon($scope.material.resourceTypes);
            };

            $scope.confirmMaterialDeletion = () => {
                dialogService.showConfirmationDialog(
                    'MATERIAL_CONFIRM_DELETE_DIALOG_TITLE',
                    'MATERIAL_CONFIRM_DELETE_DIALOG_CONTENT',
                    'ALERT_CONFIRM_POSITIVE',
                    'ALERT_CONFIRM_NEGATIVE',
                    deleteMaterial);
            };

            function deleteMaterial() {
                var url = "rest/material/" + $scope.material.id;
                serverCallService.makeDelete(url, {}, deleteMaterialSuccess, deleteMaterialFailed);
            }

            function deleteMaterialSuccess() {
                toastService.showOnRouteChange('MATERIAL_DELETED');
                $scope.material.deleted = true;
                $rootScope.learningObjectDeleted = true;
                $rootScope.$broadcast('dashboard:adminCountsUpdated');
            }

            function deleteMaterialFailed() {
                log('Deleting material failed.');
            }

            $scope.isUsersMaterial = () => {
                if ($scope.material && authenticatedUserService.isAuthenticated() && !authenticatedUserService.isRestricted()) {
                    var userID = authenticatedUserService.getUser().id;
                    var creator = $scope.material.creator;

                    return creator && creator.id === userID
                }
            };

            $scope.setNotImproper = () => {
                if ($scope.isAdmin() && $scope.material) {
                    var url = "rest/impropers?learningObject=" + $scope.material.id;
                    serverCallService.makeDelete(url, {}, setNotImproperSuccessful, setNotImproperFailed);
                }
            };

            function setNotImproperSuccessful() {
                $scope.isReported = false;
                $rootScope.learningObjectImproper = false;
                $rootScope.$broadcast('dashboard:adminCountsUpdated');
            }

            function setNotImproperFailed() {
                console.log("Setting not improper failed.")
            }

            $scope.restoreMaterial = () => {
                serverCallService.makePost("rest/material/restore", $scope.material, restoreSuccess, restoreFail);
            };

            $scope.markMaterialCorrect = () => {
                serverCallService.makePost("rest/material/setNotBroken", $scope.material, markCorrectSuccess, queryFailed);
            };

            function markCorrectSuccess() {
                $scope.isBroken = false;
                $scope.isBrokenReportedByUser = false;
                $rootScope.learningObjectBroken = false;
                $rootScope.$broadcast('dashboard:adminCountsUpdated');
            }

            function queryFailed() {
                log("Request failed");
            }

            $scope.$on("restore:learningObject", () => {
                $scope.restoreMaterial();
            });

            $scope.$on("delete:learningObject", () => {
                deleteMaterial();
            });

            $scope.$on("setNotImproper:learningObject", () => {
                $scope.setNotImproper();
            });

            $scope.$on("markCorrect:learningObject", () => {
                $scope.markMaterialCorrect();
            });

            function restoreSuccess() {
                toastService.show('MATERIAL_RESTORED');
                $scope.material.deleted = false;
                $rootScope.learningObjectDeleted = false;
                $rootScope.$broadcast('dashboard:adminCountsUpdated');
            }

            function restoreFail() {
                log("Restoring material failed");
            }

            function getTargetGroups() {
                if ($scope.material.targetGroups[0]) {
                    return targetGroupService.getConcentratedLabelByTargetGroups($scope.material.targetGroups);
                }
            }
        }
    ]);
