define(['app'], function(app)
{
    
    app.directive('showFocus', function($timeout) {
        return function(scope, element, attrs) {
          scope.$watch(attrs.showFocus, 
            function (newValue) { 
                $timeout(function() {
                    newValue && elementt[0].focus();
                });
            },true);
        };    
    });
    
    app.directive('dopHeader', ['translationService', '$location', 'searchService', '$rootScope', 'serverCallService', 'loginService',
     function(translationService, $location, searchService, $rootScope, serverCallService, loginService) {

        function loginSuccess(authenticatedUser) {
            if (isEmpty(authenticatedUser)) {
                log('No data returned by logging in');
            } else {
                loginService.setAuthenticatedUser(authenticatedUser);
                $('#dropdowned').collapse('hide');
            }
        };
        
        function loginFail(material, status) {
            log('Logging in failed.');
        };


        return {
            scope: true,
            templateUrl: 'app/directives/header/header.html',
            controller: function ($scope, $location, $rootScope, loginService) {
                $scope.showLanguageSelection = false;
                $scope.showSearchBox = false;
                $scope.selectedLanguage = translationService.getLanguage();
                $rootScope.searchFields = {};
                $rootScope.searchFields.searchQuery = searchService.getQuery();

                $scope.languageSelectClick = function() {
                    $scope.showLanguageSelection = !$scope.showLanguageSelection; 
                };
                
                $scope.showSearchBoxClick = function() {
                    $scope.showSearchBox = !$scope.showSearchBox;
                    jQuery('<div class="modal-backdrop fade in hidden-md hidden-lg"></div>').appendTo(document.body);
                };
                
                $scope.closeLanguageSelection = function () {
                    $scope.$apply(function() {
                        $scope.showLanguageSelection = false;
                    });
                };

                $scope.closeSearchBox = function () {
                    $scope.showSearchBox = false;
                    jQuery('.modal-backdrop').fadeOut();
                };
                
                $scope.setLanguage = function(language) {
                    translationService.setLanguage(language);
                    $scope.selectedLanguage = language;
                    $scope.showLanguageSelection = false;
                };
                
                $scope.search = function() {
                    $scope.closeSearchBox();
                    if (!isEmpty($rootScope.searchFields.searchQuery)) {
                        searchService.setSearch($rootScope.searchFields.searchQuery);
                        $location.url(searchService.getURL());
                    }
                };

                $scope.idCardAuth = function() {
                    serverCallService.makeGet("rest/login/idCard", {}, loginSuccess, loginFail);
                };

                $scope.logout = function() {
                    $('#userMenu').dropdown('toggle');
                    loginService.logout();
                    $location.url('/');
                };

                $scope.$watch(function () {
                        return loginService.getUser();
                    }, function(user) {
                        $scope.user = user;
                }, true);
            }
        };
    }]);
    
    return app;
});