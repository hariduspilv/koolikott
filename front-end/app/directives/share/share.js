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
                    if ($rootScope.learningObjectDeleted || $rootScope.learningObjectImproper || $rootScope.learningObjectBroken) {
                        return false;
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
                }

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
                };

                $scope.checkOwnerAndShowDialog = function ($event, item) {
                    if (!isOwner()) {
                        $event.preventDefault();

                        showWarningDialog($event, item);
                    }
                }

                function showWarningDialog (ev, item) {
                    var confirm = $mdDialog.confirm()
                        .title($translate.instant('THIS_IS_UNLISTED'))
                        .textContent($translate.instant('THINK_AND_SHARE'))
                        .ariaLabel($translate.instant('THIS_IS_UNLISTED'))
                        .targetEvent(ev)
                        .ok($translate.instant('BUTTON_SHARE'))
                        .cancel($translate.instant('BUTTON_CANCEL'));

                    $mdDialog.show(confirm).then(function() {
                        $window.open(item.url, item.target);
                    }, function() {
                        console.log('Cancelled');
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

                setShareParams();
            }
        };
    }]);
});
