define([
    'angularAMD',
    'services/translationService',
    'services/authenticatedUserService'
], function (angularAMD) {
    angularAMD.directive('dopShare', ['$rootScope', '$location', '$window', 'translationService', '$translate', 'authenticatedUserService', '$mdDialog', function($rootScope, $location, $window, translationService, $translate, authenticatedUserService, $mdDialog) {
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

                    if ($rootScope.savedPortfolio) {
                        if ($rootScope.savedPortfolio.visibility === 'PUBLIC' || $rootScope.savedPortfolio.visibility === 'NOT_LISTED') {
                            return true;
                        } else if ($rootScope.savedPortfolio.visibility === 'PRIVATE') {
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
                    if (!isOwner() && $rootScope.savedPortfolio.visibility !== 'PUBLIC') {
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
                            item: item
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
                    $scope.title = $translate.instant('THIS_IS_UNLISTED');
                    $scope.context = $translate.instant('THINK_AND_SHARE');
                    $scope.ariaLabel = $translate.instant('THIS_IS_UNLISTED');
                    $scope.ok = $translate.instant('BUTTON_SHARE');
                    $scope.cancel = $translate.instant('BUTTON_CANCEL');
                    $scope.url = locals.item.url;
                    $scope.target = locals.item.target;

                    $scope.close = function() {
                        $mdDialog.cancel();
                    };
                }

                setShareParams();
            }
        };
    }]);
});
