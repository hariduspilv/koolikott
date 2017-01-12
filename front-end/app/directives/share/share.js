'use strict'

angular.module('koolikottApp')
.directive('dopShare', function() {
    return {
        scope: {
            title: '=',
            description: '=',
            object: '='
        },
        templateUrl: 'directives/share/share.html',
        controller: ['$scope', '$rootScope', '$location', '$window', 'translationService', '$translate', 'authenticatedUserService', '$mdDialog', 'serverCallService', 'toastService', 'Socialshare', 'FB_APP_ID', '$timeout',
        function($scope, $rootScope, $location, $window, translationService, $translate, authenticatedUserService, $mdDialog, serverCallService, toastService, Socialshare, FB_APP_ID, $timeout) {

            $scope.isVisible = function () {
                if ($scope.object && $scope.object.deleted) {
                    return false;
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
            $scope.pictureName = '';

            $timeout(() => {
                if ($scope.object) {
                    $scope.pictureName = $scope.object.picture.name;
                }
            });

            $scope.shareMediaPlaces = [
                {
                    'provider': 'email',
                    'icon': 'icon-mail-squared'
                }, {
                    'provider': 'google',
                    'icon': 'icon-gplus-squared'
                }, {
                    'provider': 'twitter',
                    'icon': 'icon-twitter-squared'
                }, {
                    'provider': 'facebook',
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

            $scope.share = function ($event, item) {
                if ($scope.object.type === '.Material') {
                    setShareParams(item);
                } else if ($scope.object.type === '.Portfolio') {
                    if ((!isOwner() && !isPublic()) || (isOwner() && isPrivate())) {
                        $event.preventDefault();
                        showWarningDialog($event, item);
                    } else {
                        setShareParams(item);
                    }
                }
            };

            function showWarningDialog (ev, item) {
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

            function setShareParams(item) {
                switch (item.provider) {
                    case 'facebook':
                    Socialshare.share({
                        'provider': item.provider,
                        'attrs': {
                            'socialshareUrl': $scope.pageUrl,
                            'socialshareTitle': $translate.instant('READING_RECOMMENDATION') + ': ' + $scope.title,
                            'socialshareMedia': $location.$$protocol + '://' + $location.$$host + '/rest/picture/thumbnail/lg/' + $scope.pictureName,
                            'socialshareType': 'share',
                            'socialshareVia': FB_APP_ID
                        }
                    });
                    break;
                    case 'twitter':
                    Socialshare.share({
                        'provider': item.provider,
                        'attrs': {
                            'socialshareUrl': $scope.pageUrl,
                            'socialshareText': $translate.instant('READING_RECOMMENDATION') + ': ' + $scope.title
                        }
                    });
                    break;
                    case 'google':
                    Socialshare.share({
                        'provider': item.provider,
                        'attrs': {
                            'socialshareUrl': $scope.pageUrl
                        }
                    });
                    break;
                    case 'email':
                    Socialshare.share({
                        'provider': item.provider,
                        'attrs': {
                            'socialshareSubject': $translate.instant('READING_RECOMMENDATION') + ': ' + $scope.title,
                            'socialshareBody': $translate.instant('WELCOME_READ_HERE') + ': ' + $scope.pageUrl
                        }
                    });
                    break;
                }
            }

            function DialogController($scope, $mdDialog, locals) {
                if ((isOwner() || authenticatedUserService.isAdmin() || authenticatedUserService.isModerator()) && isPrivate()) {
                    $scope.showButtons = true;

                    $scope.title = $translate.instant('THIS_IS_PRIVATE');
                    $scope.context = $translate.instant('SHARE_PRIVATE_PORTFOLIO');
                    $scope.ariaLabel = $translate.instant('THIS_IS_PRIVATE');
                } else {
                    $scope.title = $translate.instant('THIS_IS_UNLISTED');
                    $scope.context = $translate.instant('THINK_AND_SHARE');
                    $scope.ariaLabel = $translate.instant('THIS_IS_UNLISTED');
                }

                $scope.updatePortfolio = function (state) {
                    var portfolioClone = angular.copy(locals.portfolio);
                    portfolioClone.visibility = state;
                    serverCallService.makePost("rest/portfolio/update", portfolioClone, updateSuccess, updateFail);

                    setShareParams(locals.item);

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
                    console.log("Updating portfolio failed");
                }

                $scope.back = function() {
                    $mdDialog.cancel();
                }

                $scope.success = function() {
                    setShareParams(locals.item);

                    $mdDialog.cancel();
                }
            }
        }]
    };
});
