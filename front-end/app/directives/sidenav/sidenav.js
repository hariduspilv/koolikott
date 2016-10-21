define([
    'angularAMD',
    'services/serverCallService',
    'services/searchService',
    'services/userDataService',
    'directives/learningObjectRow/learningObjectRow',
    'directives/sidebarTaxon/sidebarTaxon'
], function (angularAMD) {
    angularAMD.directive('dopSidenav', ['serverCallService', '$location', '$sce', 'searchService', 'authenticatedUserService', '$mdDialog', 'userDataService', function () {
        return {
            scope: true,
            templateUrl: 'directives/sidenav/sidenav.html',
            controller: function ($rootScope, $scope, $location, serverCallService, searchService, $timeout, metadataService, authenticatedUserService, $sce, $mdDialog, userDataService) {

                $scope.oneAtATime = true;

                // List of taxon icons
                $scope.taxonIcons = [
                    'extension',
                    'accessibility',
                    'school',
                    'palette'
                ];

                function init() {
                    $scope.updateUserCounts();
                }

                $scope.$watch(function () {
                    return $location.url();
                }, function () {
                    $scope.isViewPortfolioAndEdit = $location.url().indexOf('/portfolio') != -1;
                }, true);

                $scope.$watch(function () {
                    return authenticatedUserService.getUser();
                }, function (user) {
                    $scope.user = user;
                }, true);

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
                    var isLocation = location === $location.path();
                    return isLocation;
                };

                metadataService.loadReducedTaxon(function (callback) {
                    $scope.reducedTaxon = callback;
                });

                if (window.innerWidth > 1280) {
                    $scope.sideNavOpen = true;
                }

                $scope.status = true;

                function openLoginDialog(e) {
                    $mdDialog.show(angularAMD.route({
                        templateUrl: 'views/loginDialog/loginDialog.html',
                        controllerUrl: 'views/loginDialog/loginDialog',
                        targetEvent: e
                    }));
                }

                function sortImproperItems(data) {
                    var impropers = data;
                    var improperMaterials = [];
                    var improperPortfolios = [];
                    for (var i = 0; i < impropers.length; i++) {
                        if (impropers[i].learningObject.type === '.Material' && !impropers[i].learningObject.deleted) {
                            improperMaterials.push(impropers[i]);
                        }
                        if (impropers[i].learningObject.type === '.Portfolio' && !impropers[i].learningObject.deleted) {
                            improperPortfolios.push(impropers[i]);
                        }
                    }

                    $scope.improperMaterialsCount = improperMaterials.length;
                    $scope.improperPortfoliosCount = improperPortfolios.length;
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
                $scope.updateImproperMarerialsCount = function () {
                    userDataService.loadImproperItems(function (data) {
                        sortImproperItems(data)
                    });
                };
                $scope.updateImproperPortfoliosCount = function () {
                    userDataService.loadImproperItems(function (data) {
                        sortImproperItems(data)
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
                    }

                    if ($scope.isAdmin() || $scope.isModerator()) {
                        $scope.updateAdminCounts();
                    }
                };

                $scope.updateAdminCounts = function () {
                    $scope.updateBrokenMaterialsCount();
                    $scope.updateDeletedMaterialsCount();
                    $scope.updateDeletedPortfoliosCount();
                    $scope.updateImproperMarerialsCount();
                    $scope.updateImproperPortfoliosCount();
                };

                init();
            }
        }
    }]);
});
