'use strict'

angular.module('koolikottApp')
.directive('dopPortfolioBox',
[
    'translationService', 'serverCallService', '$rootScope', 'authenticatedUserService',
    function(translationService, serverCallService, $rootScope, authenticatedUserService) {
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
                };

                $scope.isAuthenticated = function () {
                    return authenticatedUserService.getUser() && !authenticatedUserService.isRestricted();
                }
            }
        };
    }
]);
