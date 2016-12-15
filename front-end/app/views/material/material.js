define([
    'app',
    'angularAMD',
    'angular-screenfull',
    'directives/copyPermalink/copyPermalink',
    'directives/report/improper/improper',
    'directives/report/brokenLink/brokenLink',
    'directives/recommend/recommend',
    'directives/rating/rating',
    'directives/commentsCard/commentsCard',
    'directives/tags/tags',
    'directives/restrict/restrict',
    'directives/favorite/favorite',
    'directives/errorMessage/errorMessage',
    'services/serverCallService',
    'services/translationService',
    'services/searchService',
    'services/alertService',
    'services/authenticatedUserService',
    'services/dialogService',
    'services/iconService',
    'services/toastService',
    'services/storageService',
    'directives/sidebar/sidebar',
    'services/targetGroupService',
    'services/taxonService',
    'directives/embeddedMaterial/embeddedMaterial',
    'directives/share/share'
], function (app, angularAMD) {
    return ['$scope', 'serverCallService', '$route', 'translationService', '$rootScope', 'searchService', '$location', 'alertService', 'authenticatedUserService', 'dialogService', 'toastService', 'iconService', '$mdDialog', 'storageService', 'targetGroupService', 'taxonService',
        function ($scope, serverCallService, $route, translationService, $rootScope, searchService, $location, alertService, authenticatedUserService, dialogService, toastService, iconService, $mdDialog, storageService, targetGroupService, taxonService) {
            $scope.showMaterialContent = false;
            $scope.newComment = {};
            $scope.pageUrl = $location.absUrl();

            if ($rootScope.savedMaterial) {
                $scope.material = $rootScope.savedMaterial;
                $rootScope.savedMaterial = null;

                if ($rootScope.isEditPortfolioMode || authenticatedUserService.isAuthenticated()) {
                    $rootScope.selectedSingleMaterial = $scope.material;
                }

                init();
            } else {
                getMaterial(getMaterialSuccess, getMaterialFail);
            }

            function getContentType () {
                var baseUrl = document.location.origin;
                // If the initial type is a LINK, try to ask the type from our proxy
                if(matchType($scope.material.source) === 'LINK' && !$scope.material.source.startsWith(baseUrl)){
                    $scope.proxyUrl = baseUrl + "/rest/material/externalMaterial?url=" + encodeURIComponent($scope.material.source);
                    serverCallService.makeHead($scope.proxyUrl, {}, probeContentSuccess, probeContentFail);
                }else{
                    $scope.sourceType = matchType($scope.material.source);
                }
            }

            function probeContentSuccess(response) {
                if(!response()['content-disposition']){
                    $scope.sourceType = 'LINK';
                    return;
                }
                var filename = response()['content-disposition'].match(/filename="(.+)"/)[1];
                $scope.sourceType = matchType(filename);
                if($scope.sourceType !== 'LINK'){
                    $scope.material.source = $scope.proxyUrl;
                }
            }

            function probeContentFail() {
                console.log("Content probing failed!");
            }

            $rootScope.$on('fullscreenchange', function () {
                $scope.$apply(function () {
                    $scope.showMaterialContent = !$scope.showMaterialContent;
                });
            });

            $scope.$watch(function () {
                return storageService.getMaterial();
            }, function (newMaterial, oldMaterial) {
                if (newMaterial !== oldMaterial) {
                    $scope.material = newMaterial;
                }
            });

            function getMaterial(success, fail) {
                var materialId = $route.current.params.materialId;
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
                    $scope.targetGroups = getTargetGroups();

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
                $scope.sourceType = getContentType();
                processMaterial();
                storageService.setMaterial(null);

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

                serverCallService.makePost("rest/material/increaseViewCount", viewCountParams, function () {
                }, function () {
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

            $scope.getMaterialEducationalContexts = function () {
                var educationalContexts = [];
                if (!$scope.material || !$scope.material.taxons) return;

                $scope.material.taxons.forEach(function (taxon) {
                    var edCtx = taxonService.getEducationalContext(taxon);
                    if (edCtx) educationalContexts.push(edCtx);
                });

                return educationalContexts;
            };

            $scope.getMaterialDomains = function () {
                var domains = [];
                if (!$scope.material || !$scope.material.taxons) return;

                $scope.material.taxons.forEach(function (taxon) {
                    var domain = taxonService.getDomain(taxon);
                    if (domain) domains.push(domain);
                });

                return domains;
            };

            $scope.getMaterialSubjects = function () {
                var subjects = [];
                if (!$scope.material || !$scope.material.taxons) return;

                $scope.material.taxons.forEach(function (taxon) {
                    var subject = taxonService.getSubject(taxon);
                    if (subject) subjects.push(subject);
                });

                return subjects;
            };

            $scope.getCorrectLanguageString = function (languageStringList) {
                if (languageStringList) {
                    return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), $scope.material.language);
                }
            };

            $scope.formatMaterialIssueDate = function (issueDate) {
                return formatIssueDate(issueDate);
            };

            $scope.formatMaterialUpdatedDate = function (updatedDate) {
                return formatDateToDayMonthYear(updatedDate);
            };

            $scope.isNullOrZeroLength = function (arg) {
                return !arg || !arg.length;
            };

            $scope.getAuthorSearchURL = function ($event, firstName, surName) {
                $event.preventDefault();

                searchService.setSearch('author:"' + firstName + " " + surName + '"');
                $location.url(searchService.getURL());
            };

            $scope.showSourceFullscreen = function ($event) {
                $event.preventDefault();

                $scope.fullscreenCtrl.toggleFullscreen();
            };

            $scope.isLoggedIn = function () {
                return authenticatedUserService.isAuthenticated();
            };

            $scope.isAdmin = function () {
                return authenticatedUserService.isAdmin();
            };

            $scope.isModerator = function () {
                return authenticatedUserService.isModerator();
            };

            $scope.isRestricted = function () {
                return authenticatedUserService.isRestricted();
            };

            $scope.modUser = function () {
                return !!(authenticatedUserService.isModerator() || authenticatedUserService.isAdmin());
            };

            $scope.isAdminButtonsShowing = function () {
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

            $scope.addComment = function () {
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

            $scope.edit = function () {
                var editMaterialScope = $scope.$new(true);
                editMaterialScope.material = $scope.material;

                $mdDialog.show(angularAMD.route({
                    templateUrl: 'addMaterialDialog.html',
                    controllerUrl: 'views/addMaterialDialog/addMaterialDialog',
                    scope: editMaterialScope
                })).then(function (material) {
                    if (material) {
                        $scope.material = material;
                        processMaterial();
                    }
                });
            };

            function addCommentSuccess() {
                $scope.newComment.text = "";

                getMaterial(function (material) {
                    $scope.material = material;
                }, function () {
                    log("Comment success, but failed to reload material.");
                });
            }

            function addCommentFailed() {
                log('Adding comment failed.');
            }

            $scope.getType = function () {
                if ($scope.material === undefined || $scope.material === null) return '';

                return iconService.getMaterialIcon($scope.material.resourceTypes);
            };

            $scope.confirmMaterialDeletion = function () {
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

            $scope.isUsersMaterial = function () {
                if ($scope.material && authenticatedUserService.isAuthenticated() && !authenticatedUserService.isRestricted()) {
                    var userID = authenticatedUserService.getUser().id;
                    var creator = $scope.material.creator;

                    return creator && creator.id === userID
                }
            };

            $scope.setNotImproper = function () {
                if ($scope.isAdmin() && $scope.material) {
                    url = "rest/impropers?learningObject=" + $scope.material.id;
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

            $scope.restoreMaterial = function () {
                serverCallService.makePost("rest/material/restore", $scope.material, restoreSuccess, restoreFail);
            };

            $scope.markMaterialCorrect = function () {
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

            $scope.$on("restore:learningObject", function () {
                $scope.restoreMaterial();
            });

            $scope.$on("delete:learningObject", function () {
                deleteMaterial();
            });

            $scope.$on("setNotImproper:learningObject", function () {
                $scope.setNotImproper();
            });

            $scope.$on("markCorrect:learningObject", function () {
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
                    return targetGroupService.getLabelByTargetGroupsOrAll($scope.material.targetGroups);
                }
            }
        }];
});
