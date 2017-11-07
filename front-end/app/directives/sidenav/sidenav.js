'use strict'

angular.module('koolikottApp')
.directive('dopSidenav', [
    'serverCallService', '$location', '$sce', 'searchService', 'authenticatedUserService', '$mdDialog', 'userDataService', 'metadataService', 'taxonService',
    function (serverCallService, $location, $sce, searchService, authenticatedUserService, $mdDialog, userDataService, metadataService, taxonService) {
        return {
            scope: true,
            templateUrl: 'directives/sidenav/sidenav.html',
            controller: ['$rootScope', '$scope', '$location', '$timeout', function ($rootScope, $scope, $location, $timeout) {
                $scope.isTaxonomyOpen = !authenticatedUserService.isAuthenticated();

                // List of taxon icons
                $scope.taxonIcons = [
                    'extension',
                    'accessibility',
                    'school',
                    'build',
                    'palette'
                ];

                // List of sidenav adminLocations
                const adminLocations = [
                    '/dashboard/improperMaterials',
                    '/dashboard/improperPortfolios',
                    '/dashboard/unReviewed',
                    '/dashboard/changedLearningObjects',
                    '/dashboard/moderators',
                    '/dashboard/restrictedUsers',
                    '/dashboard/deletedMaterials',
                    '/dashboard/deletedPortfolios',
                    '/dashboard/brokenMaterials',
                ];

                // List of not active sidenav locations
                const notActiveLocations = [
                    '/portfolio',
                    '/material',
                ];

                function userLocations(username) {
                    return ['/' + username + '/portfolios', '/' + username + '/materials', '/' + username + '/favorites'];
                }


                $scope.$watch(function () {
                   return taxonService.getSidenavTaxons();
                }, function (newValue) {
                    if (newValue) {
                        $scope.taxon = newValue;
                    }
                });

                $scope.$watch(function () {
                    return $location.url();
                }, function () {
                    $rootScope.isViewPortfolioAndEdit = $location.url().indexOf('/portfolio') != -1 || $location.url().indexOf('/search') != -1;
                }, true);

                $scope.$watch(function () {
                    return authenticatedUserService.getUser();
                }, function (user) {
                    $scope.user = user;
                    $scope.updateUserCounts();
                }, true);

                $scope.$on('dashboard:adminCountsUpdated', function () {
                    $scope.updateAdminCounts();
                });

                $scope.isAdmin = function () {
                    return authenticatedUserService.isAdmin();
                };

                $scope.isModerator = function () {
                    return authenticatedUserService.isModerator();
                };

                $scope.checkUser = function (e, redirectURL) {
                    if ($scope.user) {
                        $location.url('/' + $scope.user.username + redirectURL);
                    } else {
                        $rootScope.afterAuthRedirectURL = redirectURL;
                        $rootScope.sidenavLogin = redirectURL;
                        openLoginDialog(e);
                    }
                };

                $scope.modUser = function () {
                    return !!(authenticatedUserService.isModerator() || authenticatedUserService.isAdmin());
                };

                $scope.adminUser = function () {
                    return !!(authenticatedUserService.isAdmin());
                };

                //Checks the location
                $scope.isLocationActive = function (menuLocation) {
                    if (!$scope.user){
                        return false;
                    }
                    let currentLocation = $location.path();
                    if (currentLocation === "/"){
                        return false;
                    }
                    if (!$scope.modUser()){
                        return menuLocation === currentLocation;
                    }
                    let isInMenu = adminLocations.includes(currentLocation) || userLocations($scope.user.username).includes(currentLocation);
                    if (isInMenu){
                        return menuLocation === currentLocation;
                    } else {
                        return $rootScope.private ?
                                false :
                                $rootScope.learningObjectDeleted
                                    ? menuLocation === '/dashboard/deletedPortfolios' :
                                    $rootScope.learningObjectImproper
                                        ? menuLocation === '/dashboard/improperPortfolios' :
                                        $rootScope.learningObjectUnreviewed
                                            ? menuLocation === '/dashboard/unReviewed' :
                                            $rootScope.learningObjectChanged
                                                ? menuLocation === '/dashboard/changedLearningObjects' :
                                                false;
                    }
                };

                if (window.innerWidth > BREAK_LG) {
                    $rootScope.sideNavOpen = true;
                }

                $scope.status = true;

                function openLoginDialog(e) {
                    $mdDialog.show({
                        templateUrl: 'views/loginDialog/loginDialog.html',
                        controller: 'loginDialogController',
                        targetEvent: e,
                        clickOutsideToClose: true,
                        escapeToClose: true
                    });
                }

                $scope.updateBrokenMaterialsCount = function () {
                    userDataService.loadBrokenMaterialsCount(function (data) {
                        $scope.brokenMaterialsCount = data;
                    });
                };
                $scope.updateDeletedMaterialsCount = function () {
                    userDataService.loadDeletedMaterialsCount(function (data) {
                        $scope.deletedMaterialsCount = data;
                    });
                };
                $scope.updateDeletedPortfoliosCount = function () {
                    userDataService.loadDeletedPortfoliosCount(function (data) {
                        $scope.deletedPortfoliosCount = data;
                    });
                };

                $scope.updateImproperMaterialsCount = function () {
                    userDataService.loadImproperMaterialsCount(function (data) {
                        $scope.improperMaterialsCount = data;
                    });
                };

                $scope.updateImproperPortfoliosCount = function () {
                    userDataService.loadImproperPortfoliosCount(function (data) {
                        $scope.improperPortfoliosCount = data;
                    });
                };

                $scope.updateUserMaterialsCount = function () {
                    if (authenticatedUserService.isAuthenticated()) {
                        userDataService.loadUserMaterialsCount(function (data) {
                            if (data >= 0) $scope.materials = data;
                        });
                    }
                };
                $scope.updateUserPortfoliosCount = function () {
                    if (authenticatedUserService.isAuthenticated()) {
                        userDataService.loadUserPortfoliosCount(function (data) {
                            if (data >= 0) $scope.portfolios = data;
                        });
                    }
                };
                $scope.updateUserFavoritesCount = function () {
                    if (authenticatedUserService.isAuthenticated()) {
                        userDataService.loadUserFavoritesCount(function (data) {
                            if (data >= 0) $scope.favorites = data;
                        });
                    }
                };
                $scope.updateModeratorsCount = function () {
                    userDataService.loadModeratorsCount(function (data) {
                        $scope.moderatorsCount = data;
                    });
                };
                $scope.updateRestrictedUsersCount = function () {
                    userDataService.loadRestrictedUsersCount(function (data) {
                        $scope.restrictedUsersCount = data;
                    });
                };
                $scope.updateChangedLearningObjectCount = function () {
                    userDataService.loadChangedLearningObjectCount(function (data) {
                        $scope.changedLearningObjectCount = data;
                    });
                };
                $scope.updateUserCounts = function () {
                    if (authenticatedUserService.isAuthenticated()) {
                        $scope.updateUserFavoritesCount();
                        $scope.updateUserMaterialsCount();
                        $scope.updateUserPortfoliosCount();

                        $scope.updateAdminCounts();
                    }
                };

                $scope.isAuthenticated = function () {
                    return authenticatedUserService.isAuthenticated();
                };

                $scope.updateAdminCounts = function () {
                    if ($scope.isAdmin() || $scope.isModerator()) {
                        $scope.updateBrokenMaterialsCount();
                        $scope.updateDeletedMaterialsCount();
                        $scope.updateDeletedPortfoliosCount();
                        $scope.updateImproperMaterialsCount();
                        $scope.updateImproperPortfoliosCount();
                        $scope.updateChangedLearningObjectCount();
                        $scope.updateUnReviewedLearningObjectCount();
                    }
                    if ($scope.isAdmin()) {
                        $scope.updateModeratorsCount();
                        $scope.updateRestrictedUsersCount();
                    }
                };

                $scope.updateUnReviewedLearningObjectCount = function () {
                    userDataService.loadUnReviewedLearningObjectCount(function (data) {
                        $scope.unReviewedLearningObjectCount = data;
                    });
                };

                $scope.$on('header:red', () => $scope.isHeaderRed = true);
                $scope.$on('header:default', () => $scope.isHeaderRed = false);

            }]
        }
    }
]);
