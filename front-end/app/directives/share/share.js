'use strict'

angular.module('koolikottApp')
.directive('dopShare',
[
    '$rootScope', '$location', '$window', 'translationService', '$translate', 'authenticatedUserService', '$mdDialog', 'serverCallService', 'toastService',
    function($rootScope, $location, $window, translationService, $translate, authenticatedUserService, $mdDialog, serverCallService, toastService) {
        return {
            scope: {
                title: '=',
                description: '=',
                object: '='
            },
            templateUrl: 'directives/share/share.html',
            controller: function($scope) {

                $scope.isVisible = function () {
                    if ($scope.object) {
                        if ($scope.object.deleted) {
                            return false;
                        }
                    }

                    if ($rootScope.isEditPortfolioPage) {
                        return false;
                    }

                    if ($rootScope.isViewMaterialPage) {
                        return true;
                    }


                    if ($scope.object) {
                        if (isPublic() || isNotListed() || isOwner() || authenticatedUserService.isAdmin() || authenticatedUserService.isModerator()) {
                            return true;
                        } else if (isPrivate()) {
                            return false;
                        }
                    }

                    return false;
                };

                $scope.isOpen = false;
                $scope.pageUrl = $location.absUrl();
                if ($scope.object) {
                    $scope.pictureName = $scope.object.picture ? $scope.object.picture.name : '';
                }

                $scope.shareMediaPlaces = [
                    {
                        'name': 'email',
                        'url': 'mailto:?subject=' + $translate.instant('READING_RECOMMENDATION') + ':%20' + $scope.title + '&body=' + $translate.instant('WELCOME_READ_HERE') + ':' + $scope.pageUrl,
                        'target': '_self',
                        'icon': 'icon-mail-squared'
                    }, {
                        'name': 'google',
                        'url': 'https://plus.google.com/share?url=' + $scope.pageUrl,
                        'target': '_blank',
                        'icon': 'icon-gplus-squared'
                    }, {
                        'name': 'twitter',
                        'url': 'https://twitter.com/intent/tweet?url=' + $scope.pageUrl + '&amp;text=' + $translate.instant('READING_RECOMMENDATION') + ':%20' + $scope.title,
                        'target': '_blank',
                        'icon': 'icon-twitter-squared'
                    }, {
                        'name': 'facebook',
                        'url': 'https://www.facebook.com/sharer.php?u=' + $scope.pageUrl,
                        'target': '_blank',
                        'icon': 'icon-facebook-squared'
                    }
                ];

                function isPublic() {
                    return $scope.object.visibility === 'PUBLIC';
                }

                function isPrivate() {
                    return $scope.object.visibility === 'PRIVATE';
                }

                function isNotListed() {
                    return $scope.object.visibility === 'NOT_LISTED';
                }

                function isOwner () {
                    if (!authenticatedUserService.isAuthenticated()) {
                        return false;
                    }

                    if ($scope.object && $scope.object.creator) {
                        var creatorId = $scope.object.creator.id;
                        var userId = authenticatedUserService.getUser().id;
                        return creatorId === userId;
                    }
                }

                $scope.checkOwnerAndShowDialog = function ($event, item) {
                    if ((!isOwner() && !isPublic()) || (isOwner() && isPrivate())) {
                        $event.preventDefault();
                        showWarningDialog($event, item);
                    }
                };

                function showWarningDialog (ev, item) {
                    $scope.dialogItem = item;
                    $mdDialog.show({
                        templateUrl: 'sharedialog.tmpl.html',
                        controller: DialogController,
                        targetEvent: ev,
                        locals: {
                            item: item,
                            portfolio: $scope.object
                        }
                    });
                }

                function setShareParams() {
                    $rootScope.shareUrl = $scope.pageUrl;
                    $rootScope.shareTitle = $scope.title;
                    $rootScope.shareDescription = $scope.description;
                    if ($scope.pictureName) {
                        $rootScope.shareImage = $location.protocol() + '://' + $location.host() + ':' +  $location.port() + '/rest/picture/' + $scope.pictureName;
                    }
                }

                function DialogController($scope, $mdDialog, locals) {
                    if((isOwner() || authenticatedUserService.isAdmin() || authenticatedUserService.isModerator()) && isPrivate()) {
                        $scope.showButtons = true;

                        $scope.title = $translate.instant('THIS_IS_PRIVATE');
                        $scope.context = $translate.instant('SHARE_PRIVATE_PORTFOLIO');
                        $scope.ariaLabel = $translate.instant('THIS_IS_PRIVATE');
                    } else {
                        $scope.title = $translate.instant('THIS_IS_UNLISTED');
                        $scope.context = $translate.instant('THINK_AND_SHARE');
                        $scope.ariaLabel = $translate.instant('THIS_IS_UNLISTED');
                    }

                    $scope.url = locals.item.url;
                    $scope.target = locals.item.target;

                    $scope.updatePortfolio = function (state) {
                        var portfolioClone = angular.copy(locals.portfolio);
                        portfolioClone.visibility = state;
                        serverCallService.makePost("rest/portfolio/update", portfolioClone, updateSuccess, updateFail);
                        $mdDialog.cancel();
                    };

                    function updateSuccess(data) {
                        if (isEmpty(data)) {
                            updateFail();
                        } else {
                            locals.portfolio.visibility = data.visibility;
                            toastService.show('PORTFOLIO_SAVED');
                        }
                    }

                    function updateFail() {
                        console.log("Updating portfolio failed")
                    }

                    $scope.back = function() {
                        $mdDialog.cancel();
                    }

                    $scope.success = function() {
                        $mdDialog.cancel();
                    }
                }

                setShareParams();
            }
        };
    }
]);
