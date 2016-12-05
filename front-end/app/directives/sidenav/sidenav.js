define([
    'angularAMD',
    'services/serverCallService',
    'services/searchService',
    'services/userDataService',
    'directives/learningObjectRow/learningObjectRow',
    'directives/sidenavTaxon/sidenavTaxon'
], function (angularAMD) {
    angularAMD.directive('dopSidenav', ['serverCallService', '$location', '$sce', 'searchService', 'authenticatedUserService', '$mdDialog', 'userDataService', function () {
        return {
            scope: true,
            templateUrl: 'directives/sidenav/sidenav.html',
            controller: function ($rootScope, $scope, $location, serverCallService, searchService, $timeout, metadataService, authenticatedUserService, $sce, $mdDialog, userDataService) {
                $scope.isTaxonomyOpen = true;
                $scope.dashboardOpen = $location.path().startsWith("/dashboard");

                // List of taxon icons
                $scope.taxonIcons = [
                    'extension',
                    'accessibility',
                    'school',
                    'build',
                    'palette'
                ];

                $scope.$watch(function () {
                    return $location.url();
                }, function () {
                    $rootScope.isViewPortfolioAndEdit = $location.url().indexOf('/portfolio') != -1;
                }, true);

                $scope.$watch(function () {
                    return authenticatedUserService.getUser();
                }, function (user) {
                    $scope.user = user;
                    $scope.updateUserCounts();
                }, true);

                $scope.$on('dashboard:adminCountsUpdated', function() {
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

                //Checks the location
                $scope.isLocation = function (location) {
                    return location === $location.path();
                };

                if (window.innerWidth > 1280) {
                    $rootScope.sideNavOpen = true;
                }

                $scope.status = true;

                function openLoginDialog(e) {
                    $mdDialog.show(angularAMD.route({
                        templateUrl: 'views/loginDialog/loginDialog.html',
                        controllerUrl: 'views/loginDialog/loginDialog',
                        targetEvent: e
                    }));
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
                    }
                };

                $scope.dashboardSearch = function () {
                    if ($scope.dashboardOpen === false) {
                        $location.url("/dashboard");
                        $scope.dashboardOpen = true;
                    } else {
                        $scope.dashboardOpen = false;
                    }
                };

            }
        }
    }]);
});
