define([
    'app',
    'angularAMD',
    'angular-youtube-mb',
    'angular-screenfull',
    'directives/copyPermalink/copyPermalink',
    'directives/report/improper/improper',
    'directives/report/brokenLink/brokenLink',
    'directives/recommend/recommend',
    'directives/rating/rating',
    'directives/commentsCard/commentsCard',
    'directives/slideshare/slideshare',
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
    'services/targetGroupService',
    'services/learningObjectHelper'
], function (app, angularAMD) {
    return ['$scope', 'serverCallService', '$route', 'translationService', '$rootScope', 'searchService', '$location', 'alertService', 'authenticatedUserService', 'dialogService', 'toastService', 'iconService', '$mdDialog', 'storageService', 'targetGroupService', 'learningObjectHelper',
        function ($scope, serverCallService, $route, translationService, $rootScope, searchService, $location, alertService, authenticatedUserService, dialogService, toastService, iconService, $mdDialog, storageService, targetGroupService, learningObjectHelper) {

            learningObjectHelper.loadBrokenMaterials(setItem);

            function setItem(data) {
                console.log(data);
            }

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

            // Find educational contexts and subjects in case the material taxons are loaded later
            $scope.taxonWatcher = $scope.$watchCollection('material.taxons', function (newTaxons, oldTaxons) {
                if (newTaxons !== oldTaxons) {
                    preprocessMaterialSubjects();
                    preprocessMaterialEducationalContexts();
                    if ($scope.material.educationalContexts.length > 0 || $scope.material.subjects.length > 0) {
                        $scope.taxonWatcher();
                    }
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
                    log('No data returned by getting material. Redirecting to landing page');
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
                    setSourceType();

                    if ($scope.material.taxons) {
                        preprocessMaterialSubjects();
                        preprocessMaterialEducationalContexts();
                    }

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
                processMaterial();
                $scope.material.source = getSource($scope.material);
                storageService.setMaterial(null);

                var viewCountParams = {
                    'type': '.Material',
                    'id': $scope.material.id
                };

                serverCallService.makePost("rest/material/increaseViewCount", viewCountParams, function () {
                }, function () {
                });
            }

            function preprocessMaterialSubjects() {
                $scope.material.subjects = [];

                for (var i = 0; i < $scope.material.taxons.length; i++) {
                    var taxon = $scope.material.taxons[i];
                    var subject = $rootScope.taxonUtils.getSubject(taxon);

                    if (subject && !containsObject(subject, $scope.material.subjects)) {
                        $scope.material.subjects.push(subject);
                    }
                }
            }

            function preprocessMaterialEducationalContexts() {
                var material = $scope.material;
                material.educationalContexts = [];

                for (var i = 0, j = 0; i < material.taxons.length; i++) {
                    var taxon = material.taxons[i];
                    var educationalContext = $rootScope.taxonUtils.getEducationalContext(taxon);

                    if (educationalContext && !containsObject(educationalContext, material.educationalContexts)) {
                        material.educationalContexts[j++] = educationalContext;
                    }
                }
            }

            $scope.getMaterialDomains = function () {
                var domains = [];

                if (!$scope.material || !$scope.material.taxons) {
                    return [];
                }

                for (var i = 0, j = 0; i < $scope.material.taxons.length; i++) {
                    var taxon = $scope.material.taxons[i];
                    var domain = $rootScope.taxonUtils.getDomain(taxon);

                    if (domain) {
                        domains[j++] = domain;
                    }
                }

                return domains;
            };

            $scope.getCorrectLanguageString = function (languageStringList) {
                if (languageStringList) {
                    return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), $scope.material.language);
                }
            };

            function isYoutubeVideo(url) {
                // regex taken from http://stackoverflow.com/questions/2964678/jquery-youtube-url-validation-with-regex #ULTIMATE YOUTUBE REGEX
                var youtubeUrlRegex = /^(?:https?:\/\/)?(?:www\.)?(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=))((\w|-){11})(?:\S+)?$/;
                return url && url.match(youtubeUrlRegex);
            }

            function isSlideshareLink(url) {
                var slideshareUrlRegex = /^https?\:\/\/www\.slideshare\.net\/[a-zA-Z0-9\-]+\/[a-zA-Z0-9\-]+$/;
                return url && url.match(slideshareUrlRegex);
            }

            function setSourceType() {
                if (isYoutubeVideo($scope.material.source)) {
                    $scope.sourceType = 'YOUTUBE';
                } else if (isSlideshareLink($scope.material.source)) {
                    $scope.sourceType = 'SLIDESHARE';
                } else {
                    $scope.sourceType = 'LINK';
                }
            }

            $scope.formatMaterialIssueDate = function (issueDate) {
                return formatIssueDate(issueDate);

            }

            $scope.formatMaterialUpdatedDate = function (updatedDate) {
                return formatDateToDayMonthYear(updatedDate);
            }

            $scope.isNullOrZeroLength = function (arg) {
                return !arg || !arg.length;
            }

            $scope.getAuthorSearchURL = function ($event, firstName, surName) {
                $event.preventDefault();

                searchService.setSearch('author:"' + firstName + " " + surName + '"');
                $location.url(searchService.getURL());
            }

            $scope.showSourceFullscreen = function ($event) {
                $event.preventDefault()

                $scope.fullscreenCtrl.toggleFullscreen();
            };

            $scope.slideshareFail = function () {
                $scope.sourceType = 'LINK';
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
                editMaterialScope.material = clone($scope.material);

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
                $location.url('/' + authenticatedUserService.getUser().username);
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

            $scope.restoreMaterial = function () {
                serverCallService.makePost("rest/material/restore", $scope.material, restoreSuccess, restoreFail);
            };

            function restoreSuccess() {
                $scope.material.deleted = false;
                toastService.show('MATERIAL_RESTORED');
            }

            function restoreFail() {
                log("Restoring material failed");
            }

            $scope.getTargetGroups = function () {
                if ($scope.material) {
                    return targetGroupService.getLabelByTargetGroupsOrAll($scope.material.targetGroups);
                }
            };
        }];
});
