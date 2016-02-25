define([
    'app',
    'services/translationService',
    'services/serverCallService'
], function(app) {
    app.directive('dopPortfolioBox', ['translationService', 'serverCallService', '$rootScope', function(translationService, serverCallService, $rootScope) {
        return {
            scope: {
                portfolio: '='
            },
            templateUrl: 'directives/portfolioBox/portfolioBox.html',
            controller: function($scope, $location, $rootScope) {
                $scope.navigateTo = function(portfolio, $event) {
                    $event.preventDefault();
                    $rootScope.savedPortfolio = portfolio;

                    $location.path('/portfolio').search({
                        id: portfolio.id
                    });
                }

                $scope.formatName = function(name) {
                    return formatNameToInitials(name);
                }

                $scope.formatSurname = function(surname) {
                    return formatSurnameToInitialsButLast(surname);
                }

                $scope.formatDate = function(date) {
                    return formatDateToDayMonthYear(date);
                }

                function fetchImage() {
                    if (!$scope.pictureLock) {
                        serverCallService.makeGet("rest/portfolio/getPicture?portfolioId=" + $scope.portfolio.id, {}, fetchImageSuccess, fetchImageFail, fetchImageFinally);
                        $scope.pictureLock = true;
                    }
                }

                function fetchImageSuccess(data) {
                    $scope.portfolio.picture = "data:image/jpeg;base64," + data;
                }

                function fetchImageFail(data) {
                    log("Getting portfolio image failed");
                }

                function fetchImageFinally() {
                    $scope.pictureLock = false;
                }

                function init() {
                    if ($scope.portfolio && $scope.portfolio.hasPicture) {
                        fetchImage();
                    }
                }

                init();
            }
        };
    }]);
});