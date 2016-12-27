'use strict'

angular.module('koolikottApp')
.directive('dopCardSm',
[
    'translationService', 'serverCallService', 'iconService', 'authenticatedUserService', 'targetGroupService', 'storageService', 'taxonService',
    function (translationService, serverCallService, iconService, authenticatedUserService, targetGroupService, storageService, taxonService) {
        return {
            scope: {
                learningObject: '=',
                chapter: '=?'
            },
            templateUrl: 'directives/card/cardSM/cardSM.html',
            controller: ['$scope', '$location', '$rootScope', function ($scope, $location, $rootScope) {
                $scope.selected = false;
                $scope.isEditPortfolioPage = $rootScope.isEditPortfolioPage;
                $scope.isEditPortfolioMode = $rootScope.isEditPortfolioMode;
                $scope.domains = [];
                $scope.subjects = [];

                function init() {
                    $scope.targetGroups = targetGroupService.getConcentratedLabelByTargetGroups($scope.learningObject.targetGroups);
                    loadDomainsAndSubjects();
                }

                init();

                $scope.navigateTo = function (learningObject, $event) {
                    $event.preventDefault();

                    if (learningObject.type == '.Material') {
                        storageService.setMaterial(learningObject);

                        $location.path('/material').search({
                            id: learningObject.id
                        });
                    }

                    if (learningObject.type == '.Portfolio') {
                        storageService.setPortfolio(learningObject);

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

                $scope.formatDate = function (date) {
                    return formatDateToDayMonthYear(date);
                };

                $scope.isAuthenticated = function () {
                    var authenticated = authenticatedUserService.getUser() && !authenticatedUserService.isRestricted() && !$rootScope.isEditPortfolioPage;
                    if (!authenticated && $scope.learningObject.type == '.Material') {
                        $scope.learningObject.selected = false;
                    }

                    return authenticated;
                };

                function loadDomainsAndSubjects() {
                    $scope.learningObject.taxons.forEach(function (taxon) {
                        var domain = taxonService.getDomain(taxon);
                        var subject = taxonService.getSubject(taxon);

                        if (domain) {
                            $scope.domains.push("DOMAIN_" + domain.name.toUpperCase());
                        }

                        if (subject) {
                            $scope.subjects.push("SUBJECT_" + subject.name.toUpperCase());
                        }
                    });
                }

                $rootScope.$watch('selectedMaterials', function (newValue) {
                    if (newValue && newValue.length == 0) {
                        $scope.learningObject.selected = false;
                    }
                });
            }]
        };
    }
]);
