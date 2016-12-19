'use strict'

angular.module('koolikottApp')
.directive('dopCardSm',
[
    'translationService', 'serverCallService', 'iconService', 'authenticatedUserService',
    function (translationService, serverCallService, iconService, authenticatedUserService) {
        return {
            scope: {
                learningObject: '=',
                chapter: '=?'
            },
            templateUrl: 'directives/card/cardSM/cardSM.html',
            controller: function ($scope, $location, $rootScope) {

                $scope.selected = false;
                $scope.isEditPortfolioPage = $rootScope.isEditPortfolioPage;
                $scope.isEditPortfolioMode = $rootScope.isEditPortfolioMode;
                $scope.learningObjectType;

                if ($scope.learningObject.type === '.Material') {
                    $scope.learningObjectType = 'material';
                } else if ($scope.learningObject.type === '.Portfolio') {
                    $scope.learningObjectType = 'portfolio';
                }

                $scope.navigateTo = function (learningObject, $event) {
                    $event.preventDefault();

                    if (learningObject.type == '.Material') {
                        $rootScope.savedMaterial = learningObject;

                        $location.path('/material').search({
                            id: learningObject.id
                        });
                    }

                    if (learningObject.type == '.Portfolio') {
                        $rootScope.savedPortfolio = learningObject;

                        $location.path('/portfolio').search({
                            id: learningObject.id
                        });
                    }
                };

                $scope.getCorrectLanguageTitle = function (material) {
                    if (material) {
                        return getCorrectLanguageString(material.titles, material.language);
                    }
                };

                function getCorrectLanguageString(languageStringList, materialLanguage) {
                    if (languageStringList) {
                        return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), materialLanguage);
                    }
                }

                $scope.formatMaterialIssueDate = function (issueDate) {
                    return formatIssueDate(issueDate);

                };

                $scope.formatName = function (name) {
                    if (name) {
                        return formatNameToInitials(name.trim());
                    }
                };

                $scope.formatSurname = function (surname) {
                    if (surname) {
                        return formatSurnameToInitialsButLast(surname.trim());
                    }
                };

                $scope.formatDate = function(date) {
                    return formatDateToDayMonthYear(date);
                };

                $scope.isAuthenticated = function () {
                    var authenticated = authenticatedUserService.getUser() && !authenticatedUserService.isRestricted() && !$rootScope.isEditPortfolioPage;
                    if (!authenticated && $scope.learningObject.type == '.Material') {
                        $scope.learningObject.selected = false;
                    }

                    return authenticated;
                };

                $rootScope.$watch('selectedMaterials', function (newValue) {
                    if (newValue && newValue.length == 0) {
                        $scope.learningObject.selected = false;
                    }
                });
            }
        };
    }
]);
