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
                };

                $scope.formatName = function(name) {
                    return formatNameToInitials(name.trim());
                };

                $scope.formatSurname = function(surname) {
                    return formatSurnameToInitialsButLast(surname.trim());
                };

                $scope.formatDate = function(date) {
                    return formatDateToDayMonthYear(date);
                }
            }
        };
    }]);
});
