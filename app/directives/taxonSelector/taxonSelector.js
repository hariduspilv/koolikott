define([
    'angularAMD',
    'services/serverCallService'
], function(angularAMD) {
    var EDUCATIONAL_CONTEXTS;


    angularAMD.directive('dopTaxonSelector', ['serverCallService', 'metadataService', function() {
        return {
            scope: {
                taxon: '=',
                disableEducationalContext: '=',
                isAddPortfolioView: '='
            },
            templateUrl: 'directives/taxonSelector/taxonSelector.html',
            controller: function($scope, serverCallService, $rootScope, $timeout, metadataService) {

                // get educational contexts
                if (!EDUCATIONAL_CONTEXTS) {
                    metadataService.loadEducationalContexts(getEducationalContextsSuccess)
                } else {
                    init();
                }

                function init() {
                    setEducationalContexts();
                    buildTaxonPath();
                    addTaxonPathListeners();

                    $scope.basicEducationDomainSubjects = EDUCATIONAL_CONTEXTS.basicEducationDomainSubjects;
                    $scope.secondaryEducationDomainSubjects = EDUCATIONAL_CONTEXTS.secondaryEducationDomainSubjects;
                    $timeout(function(){
                        $scope.isReady = true;
                    })
                }

                $scope.reset = function(taxon) {
                    $scope.taxon = taxon;
                };

                $scope.selectEducationalContext = function() {
                    $rootScope.dontCloseSearch = true;
                    $timeout(function() {
                        $rootScope.dontCloseSearch = false;
                    }, 500);
                };

                function addTaxonPathListeners() {
                    /*
                     * The order of the watchers is important and should be the same as the tree.
                     */

                    $scope.$watch('taxon.id', function(newTaxon, oldTaxon) {
                        buildTaxonPath();
                    }, true);

                    $scope.$watch('taxonPath.educationalContext.id', function(newEducationalContext, oldEducationalContext) {
                        if (newEducationalContext !== undefined && newEducationalContext !== oldEducationalContext) {
                            $scope.taxon = Object.create($scope.taxonPath.educationalContext);
                        }
                    }, true);

                    $scope.$watch('taxonPath.domain.id', function(newDomain, oldDomain) {
                        if (newDomain !== undefined && newDomain !== oldDomain) {
                            $scope.taxon = Object.create($scope.taxonPath.domain);
                        }
                    }, true);

                    $scope.$watch('taxonPath.subject.id', function(newSubject, oldSubject) {
                        if (newSubject !== undefined && newSubject !== oldSubject) {
                            $scope.taxon = Object.create($scope.taxonPath.subject);
                        }
                    }, true);

                    $scope.$watch('taxonPath.domainSubject.id', function(newDomainSubject, oldDomainSubject) {
                        if (newDomainSubject !== undefined && newDomainSubject !== oldDomainSubject) {
                            $scope.taxon = Object.create($scope.taxonPath.domainSubject);
                        }
                    }, true);

                    $scope.$watch('taxonPath.specialization.id', function(newSpecialization, oldSpecialization) {
                        if (newSpecialization !== undefined && newSpecialization !== oldSpecialization) {
                            $scope.taxon = Object.create($scope.taxonPath.specialization);
                        }
                    }, true);

                    $scope.$watch('taxonPath.module.id', function(newModule, oldModule) {
                        if (newModule !== undefined && newModule !== oldModule) {
                            $scope.taxon = Object.create($scope.taxonPath.module);
                        }
                    }, true);

                    $scope.$watch('taxonPath.topic.id', function(newTopic, oldTopic) {
                        if (newTopic !== undefined && newTopic !== oldTopic) {
                            $scope.taxon = Object.create($scope.taxonPath.topic);
                        }
                    }, true);

                    $scope.$watch('taxonPath.subtopic.id', function(newSubtopic, oldSubtopic) {
                        if (newSubtopic !== undefined && newSubtopic !== oldSubtopic) {
                            $scope.taxon = Object.create($scope.taxonPath.subtopic);
                        }
                    }, true);
                }

                function getEducationalContextsSuccess(educationalContexts) {
                    if (!isEmpty(educationalContexts)) {
                        EDUCATIONAL_CONTEXTS = educationalContexts;
                        buildDomainSubjects(educationalContexts);
                        init();
                    }
                }

                function buildDomainSubjects(educationalContexts) {
                    for (var i = 0; i < educationalContexts.length; i++) {
                        if (educationalContexts[i].name === 'BASICEDUCATION') {
                            EDUCATIONAL_CONTEXTS.basicEducationDomainSubjects = getDomainsAndSubjects(educationalContexts[i]);
                        } else if (educationalContexts[i].name === 'SECONDARYEDUCATION') {
                            EDUCATIONAL_CONTEXTS.secondaryEducationDomainSubjects = getDomainsAndSubjects(educationalContexts[i]);
                        }
                    }
                }

                function getDomainsAndSubjects(educationalContext) {
                    var results = [];

                    // for every Domain add it to the list and its children.
                    for (var j = 0; j < educationalContext.domains.length; j++) {
                        var domain = educationalContext.domains[j];
                        results.push(domain);
                        // Merge the second array into the first one
                        Array.prototype.push.apply(results, domain.subjects);
                    }

                    return results;
                }

                function setEducationalContexts() {
                    $scope.educationalContexts = EDUCATIONAL_CONTEXTS;
                    $scope.educationalContexts.sort(function(context1, context2) {
                        return context1.id - context2.id;
                    });
                }

                function buildTaxonPath() {
                    $scope.taxonPath = {};
                    $scope.taxonPath.educationalContext = $rootScope.taxonUtils.getEducationalContext($scope.taxon);
                    $scope.taxonPath.domain = $rootScope.taxonUtils.getDomain($scope.taxon);
                    $scope.taxonPath.subject = $rootScope.taxonUtils.getSubject($scope.taxon);

                    if ($scope.taxonPath.subject) {
                        $scope.taxonPath.domainSubject = $scope.taxonPath.subject;
                    } else {
                        $scope.taxonPath.domainSubject = $scope.taxonPath.domain;
                    }

                    $scope.taxonPath.specialization = $rootScope.taxonUtils.getSpecialization($scope.taxon);
                    $scope.taxonPath.module = $rootScope.taxonUtils.getModule($scope.taxon);
                    $scope.taxonPath.topic = $rootScope.taxonUtils.getTopic($scope.taxon);
                    $scope.taxonPath.subtopic = $rootScope.taxonUtils.getSubtopic($scope.taxon);

                }
            }
        };
    }]);
});
